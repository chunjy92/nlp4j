/**
 * Copyright 2016, Emory University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.emory.mathcs.nlp.conversion;

import edu.emory.mathcs.nlp.common.collection.arc.AbstractArc;
import edu.emory.mathcs.nlp.common.constituent.CTLib;
import edu.emory.mathcs.nlp.common.constituent.CTNode;
import edu.emory.mathcs.nlp.common.constituent.CTReader;
import edu.emory.mathcs.nlp.common.constituent.CTTree;
import edu.emory.mathcs.nlp.common.treebank.*;
import edu.emory.mathcs.nlp.common.util.*;
import edu.emory.mathcs.nlp.component.template.node.FeatMap;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.tokenizer.dictionary.Emoticon;
import edu.emory.mathcs.nlp.conversion.util.C2DInfo;
import edu.emory.mathcs.nlp.conversion.util.HeadRule;
import edu.emory.mathcs.nlp.conversion.util.HeadRuleMap;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class KoreanC2DConverter extends C2DConverter
{

	private final Set<String> S_MARK		= DSUtils.toHashSet(PTBTag.P_IN, PTBTag.P_TO, PTBTag.P_DT);

	private final Set<String> S_NOUN_PHRASE	= DSUtils.toHashSet(PKTBTag.C_NP);


	/** Syntactic function tags. */
	private final Set<String> SYN_TAGS = DSUtils.toHashSet(PTBTag.F_ADV, PTBTag.F_CLF, PTBTag.F_CLR, PTBTag.F_DTV, PTBTag.F_NOM, PTBTag.F_PUT, PTBTag.F_PRD, PTBTag.F_TPC);

	/** Semantic function tags. */
	private final Set<String> SEM_TAGS = DSUtils.toHashSet(PTBTag.F_BNF, PTBTag.F_DIR, PTBTag.F_EXT, PTBTag.F_LOC, PTBTag.F_MNR, PTBTag.F_PRP, PTBTag.F_TMP, PTBTag.F_VOC);

	private final int SIZE_HEAD_FLAGS = 4;

	/** {@code true} if the constituent tag is {@link PTBTag#P_POS}. */
	private final Predicate<CTNode> MT_POS    = CTLib.matchC(PTBTag.P_POS);
	/** {@code true} if the constituent tag is {@link PTBTag#C_NP} and the function tag is {@link PTBTag#F_PRD}. */
	private final Predicate<CTNode> MT_NP_PRD = CTLib.matchCF(PTBTag.C_NP, PTBTag.F_PRD);


	private final Emoticon emoticon = new Emoticon();

	public KoreanC2DConverter(HeadRuleMap headrules)
	{
		super(headrules, new HeadRule(HeadRule.DIR_RIGHT_TO_LEFT));		
	}
	
	@Override
	public NLPNode[] toDependencyGraph(CTTree cTree)
	{
//		PTBLib.preprocess(cTree);
		if (!mapEmtpyCategories(cTree))	return null;
		setHeads(cTree.getRoot());
		
		NLPNode[] tree = getDEPTree(cTree);
		if(tree != null) finalize(tree);
		return tree;
	}
	
//	============================= Empty Categories =============================
	
	private boolean mapEmtpyCategories(CTTree cTree)
	{
		for (CTNode node : cTree.getTerminalList()) {
			if (!node.isEmptyCategory()) continue;
			if (node.getParent() == null) continue;

			if (node.wordFormStartsWith(PTBTag.E_TRACE)){
				mapTrace(cTree, node);
			}else if (node.wordFormStartsWith(PTBTag.E_PRO)){
				mapPRO(cTree, node);
//			} else if (node.wordFormStartsWith(PTBTag.E_OP)){

			} else if (node.wordFormStartsWith(PTBTag.E_ESM)){

			} else {
				removeNode(node);
			}
		}
		return cTree.getRoot().getChildrenSize() > 0; 
	}

	/** Called by {@link #mapEmtpyCategories(CTTree)}. */
	private void mapTrace(CTTree cTree, CTNode ec)
	{
		CTNode ante = ec.getAntecedent();
		
		if (ante != null && !ec.isDescendantOf(ante))
			replaceEmptyCategory(ec, ante);
	}


	private void mapPRO(CTTree cTree, CTNode ec)
	{
		CTNode np = ec.getParent();
		CTNode vp = np.getParent().getFirstLowestChainedDescendant(PTBLib.M_VP);

		if (vp == null) {    // small clauses
//			handleSmallClause(np, ec);
		} else
		{
			CTNode ante;

			if ((ante = ec.getAntecedent()) != null && PTBLib.isWhPhrase(ante))	// relative clauses
			{
				if (cTree.getEmptyCategoryList(ante.getEmptyCategoryIndex()).size() == 1)
					mapTrace(cTree, ec);
			}

		}
	}

	/* (non-Javadoc)
	 * @see edu.emory.mathcs.nlp.conversion.C2DConverter#setHeadsAux(edu.emory.mathcs.nlp.conversion.util.HeadRule, edu.emory.mathcs.nlp.common.constituent.CTNode)
	 */
	@Override
	protected void setHeadsAux(HeadRule rule, CTNode curr)
	{

		// 1. apposition
		findHeadsApposition(curr);

		// 2. coordination head finding : left-branching

		CTNode head = getHead(rule, curr.getChildrenList(), SIZE_HEAD_FLAGS);
		if (head.getC2DInfo().getLabel() != null) head.getC2DInfo().setLabel(null);
		curr.setC2DInfo(new C2DInfo(head));

		
	}

	/**
	 * Finds the head of appositional modifiers.
	 * @param curr the constituent node to be processed.
	 * @return {@code true} if the specific node contains appositional modifiers.
	 */
	private boolean findHeadsApposition(CTNode curr)
	{
		if (!curr.isConstituentTagAny(S_NOUN_PHRASE) || curr.containsChild(PTBLib.M_NNx))
			return false;

		CTNode fst = curr.getFirstChild(PTBLib.M_NP_NML);
		while (fst != null && fst.containsChild(MT_POS))
			fst = fst.getRightNearestSibling(PTBLib.M_NP_NML);

		if (fst == null || fst.getC2DInfo().hasHead())	return false;

		boolean hasAppo = false;
		CTNode snd = fst;

		while ((snd = snd.getRightSibling()) != null)
		{
			if (snd.getC2DInfo().hasHead())	continue;

			if ((snd.isConstituentTagAny(S_NOUN_PHRASE) && !hasAdverbialTag(snd)) ||
					(snd.hasFunctionTagAny(PTBTag.F_HLN, PTBTag.F_TTL)) ||
					(snd.isConstituentTag(PTBTag.C_RRC) && snd.containsChild(MT_NP_PRD)))
			{
				snd.getC2DInfo().setHead(fst, DEPTagEn.DEP_APPOS);
				hasAppo = true;
			}
		}

		return hasAppo;
	}

	private boolean hasAdverbialTag(CTNode node)
	{
		return node.hasFunctionTag(PTBTag.F_ADV) || DSUtils.hasIntersection(node.getFunctionTagSet(), SEM_TAGS);
	}

	/* (non-Javadoc)
	 * @see edu.emory.mathcs.nlp.conversion.C2DConverter#getHeadFlag(edu.emory.mathcs.nlp.common.constituent.CTNode)
	 */
	@Override
	protected int getHeadFlag(CTNode child)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see edu.emory.mathcs.nlp.conversion.C2DConverter#getDEPLabel(edu.emory.mathcs.nlp.common.constituent.CTNode, edu.emory.mathcs.nlp.common.constituent.CTNode, edu.emory.mathcs.nlp.common.constituent.CTNode)
	 */
	@Override
	protected String getDEPLabel(CTNode C, CTNode P, CTNode p2)
	{
		// TODO Auto-generated method stub
		return null;
	}


	// ============================= Get a dependency tree =============================

	private NLPNode[] getDEPTree(CTTree cTree)
	{
		NLPNode[] dTree = initDEPTree(cTree);
		System.out.println("Init Dept Tree");
		for (NLPNode node: dTree){
			System.out.println(node);
		}

		System.out.println("\nInit Dept Complete.\n");

		System.out.println("\nDone\n");

		addDEPHeads(dTree, cTree);
		for (NLPNode node: dTree){
			System.out.println(node);
		}

		System.out.println("\nDone.\n");

		if (NLPUtils.containsCycle(dTree))
			throw new UnknownFormatConversionException("Cyclic depedency relation.");

		DEPLibEn.enrichLabels(dTree);
		addFeats(dTree, cTree, cTree.getRoot());

		if (cTree.hasNamedEntity())
			addNamedEntities(dTree, cTree);

		return getDEPTreeWithoutEdited(cTree, dTree);
	}

	public void addNamedEntities(NLPNode[] dTree, CTTree cTree)
	{
		for (CTNode node : cTree.getTokenList())
			dTree[node.getTokenID()+1].setNamedEntityTag(node.getNamedEntityTag());
	}


	/** Adds dependency heads. */
	private int addDEPHeads(NLPNode[] dTree, CTTree cTree)
	{
		int currId, headId, size = dTree.length, rootCount = 0;
		CTNode cNode, ante;
		NLPNode dNode;
		String label;

		System.out.println("\nAdding Dep Heads..\n");

		for (currId=1; currId<size; currId++) {
			System.out.println();
			dNode = dTree[currId];
			cNode = cTree.getToken(currId - 1);
			System.out.println(dNode);
			System.out.println(cNode);
			headId = cNode.getC2DInfo().getTerminalHead().getTokenID() + 1;
			System.out.println("Head Id");
			System.out.println(headId);

			if (currId == headId)    // root
			{
				System.out.println("\t1: Root case");
				dNode.setDependencyHead(dTree[0], DEPTagEn.DEP_ROOT);
				rootCount++;
			} else {
				System.out.println("\t2: Else");
				label = cNode.getC2DInfo().getLabel();

				if (cNode.isConstituentTagAny(S_MARK) && cNode.getParent().isConstituentTag(PTBTag.C_SBAR))// && !label.equals(DEPTagEn.DEP_COMPLM))
					label = DEPTagEn.DEP_MARK;

				dNode.setDependencyHead(dTree[headId], label);
			}

			if ((ante = cNode.getAntecedent()) != null)
				dNode.addSecondaryHead(getNLPNode(dTree, ante), DEPTagEn.DEP2_REF);
		}
		return rootCount;
	}

	/** Called by {@link #getDEPTree(CTTree)}. */
	private void addFeats(NLPNode[] dTree, CTTree cTree, CTNode cNode)
	{
		CTNode ante;
		String feat;

		if (!cNode.isEmptyCategoryTerminal() && cNode.getGappingRelationIndex() != -1 && cNode.getParent().getGappingRelationIndex() == -1 && (ante = cTree.getAntecedent(cNode.getGappingRelationIndex())) != null)
		{
			NLPNode dNode = getNLPNode(dTree, cNode);
			dNode.addSecondaryHead(getNLPNode(dTree, ante), DEPTagEn.DEP2_GAP);
		}

		if ((feat = getFunctionTags(cNode, SEM_TAGS)) != null)
			cNode.getC2DInfo().putFeat(NLPUtils.FEAT_SEM, feat);

		if ((feat = getFunctionTags(cNode, SYN_TAGS)) != null)
			cNode.getC2DInfo().putFeat(NLPUtils.FEAT_SYN, feat);

		for (CTNode child : cNode.getChildrenList())
			addFeats(dTree, cTree, child);
	}

	/** Called by {@link #addFeats(DEPTree, CTTree, CTNode)}. */
	private String getFunctionTags(CTNode node, Set<String> sTags)
	{
		List<String> tags = new ArrayList<>();

		for (String tag : node.getFunctionTagSet())
		{
			if (sTags.contains(tag))
				tags.add(tag);
		}

		if (tags.isEmpty())	return null;
		Collections.sort(tags);
		return Joiner.join(tags, FeatMap.DELIM_VALUES);
	}

	private NLPNode getNLPNode(NLPNode[] dTree, CTNode cNode)
	{
		if (cNode.isConstituentTag(CTTag.TOP)) return null;
		CTNode cHead = cNode.isTerminal() ? cNode : cNode.getC2DInfo().getTerminalHead();
		return cHead.isEmptyCategory() ? null : dTree[cHead.getTokenID()+1];
//		return cNode.isTerminal() ? dTree.get(cNode.getTokenID()+1) : dTree.get(cNode.getC2DInfo().getTerminalHead().getTokenID()+1);
	}

	public NLPNode[] getDEPTreeWithoutEdited(CTTree cTree, NLPNode[] dTree)
	{
		List<NLPNode> nodes = new ArrayList<>();
		Set<Integer> set = new HashSet<>();
		int id = 1;

		addEditedTokensAux(cTree.getRoot(), set);

		for (NLPNode node : dTree)
		{
			if (!set.contains(node.getID()))
			{
				removeEditedHeads(node.getSecondaryHeadList(), set);
				removeEditedHeads(node.getSemanticHeadList() , set);
				node.setID(id++);
				nodes.add(node);
			}
		}

		return (nodes.size() > 0) ? NLPUtils.toDependencyTree(nodes, NLPNode::new) : null;
	}

	/** Called by {@link #getDEPTreeWithoutEdited(CTTree, DEPTree)}. */
	private void addEditedTokensAux(CTNode curr, Set<Integer> set)
	{
		for (CTNode child : curr.getChildrenList())
		{
			if (PTBLib.isEditedPhrase(child))
			{
				for (CTNode sub : child.getTokenList())
					set.add(sub.getTokenID()+1);
			}
			else if (!child.isTerminal())
				addEditedTokensAux(child, set);
		}
	}

	/** Called by {@link #getDEPTreeWithoutEdited(CTTree, DEPTree)}. */
	private <T extends AbstractArc<NLPNode>>void removeEditedHeads(List<T> heads, Set<Integer> set)
	{
		if (heads == null) return;
		List<T> remove = new ArrayList<>();

		for (T arc : heads)
		{
			if (arc.getNode() == null || set.contains(arc.getNode().getID()))
				remove.add(arc);
		}

		heads.removeAll(remove);
	}


	private void finalize(NLPNode[] tree)
	{
		finalizeLabels(tree);
		finalizeCompound(tree, PTBTag.P_NN, DEPTagEn.DEP_NMOD , n -> n.getPartOfSpeechTag().startsWith(PTBTag.P_NNP) || n.isDependencyLabel(DEPTagEn.DEP_NMOD) || n.isDependencyLabel(DEPTagEn.DEP_DEP));
		finalizeCompound(tree, PTBTag.P_CD, DEPTagEn.DEP_QMOD, n -> n.isDependencyLabel(DEPTagEn.DEP_QMOD) || n.isDependencyLabel(DEPTagEn.DEP_DEP));
	}

	private void finalizeLabels(NLPNode[] tree)
	{
		for (NLPNode node : tree)
		{
			if (isDative(node))
				node.setDependencyLabel(DEPTagEn.DEP_DATIVE);
			else if (isEmoticon(node))
				node.setDependencyLabel(DEPTagEn.DEP_DISCOURSE);
			else if (isVocative(node))
				node.setDependencyLabel(DEPTagEn.DEP_VOCATIVE);
		}
	}

	private boolean isDative(NLPNode node)
	{
		if (!PTBLib.isVerb(node.getDependencyHead().getPartOfSpeechTag())) return false;
//		if (node.isDependencyLabel(DEPTagEn.DEP_IOBJ)) return true;
		String feat;

		if ((feat = node.getFeat(NLPUtils.FEAT_SYN)) != null && DSUtils.toHashSet(Splitter.splitCommas(feat)).contains(PTBTag.F_DTV)) return true;
		if (PTBTag.F_BNF.equals(node.getFeat(NLPUtils.FEAT_SEM))) return true;

		return false;
	}

	private boolean isEmoticon(NLPNode node)
	{
		String s = node.getWordForm();
		int[] idx = emoticon.getEmoticonRange(s);
		return idx != null && idx[0] == 0 && idx[1] == s.length();
	}

	private boolean isVocative(NLPNode node)
	{
		String feat;
		return (feat = node.getFeat(NLPUtils.FEAT_SEM)) != null && feat.equals(PTBLib.F_VOC);
	}

	private void finalizeCompound(NLPNode[] tree, String pos, String label, Predicate<NLPNode> p)
	{
		NLPNode node, head;
		int i, j;

		for (i=tree.length-1; i>0; i--)
		{
			head = tree[i];

			if (head.getPartOfSpeechTag().startsWith(pos) && !head.isDependencyLabel(label))
			{
				for (j=i-1; j>0; j--)
				{
					node = tree[j];

					if (node.getPartOfSpeechTag().startsWith(pos) && node.isDescendantOf(head) && node.getDependencyHead().getID() > node.getID() && p.test(node))
					{
						node.setDependencyLabel(DEPTagEn.DEP_COMPOUND);
						i = j;
					}
					else if (node.isPartOfSpeechTag(PTBTag.P_HYPH))
						continue;
					else
						break;
				}
			}
		}
	}


	static public void main(String[] args){
		String headrule_path = "src/main/resources/edu/emory/mathcs/nlp/conversion/headrule_en_conll.txt";
//		String headrule_path = "src/main/resources/edu/emory/mathcs/nlp/conversion/headrule_kr_penn.txt";
		System.out.println("Reading in HeadRule Map...");
		HeadRuleMap hr = new HeadRuleMap(IOUtils.createFileInputStream(headrule_path));
		System.out.println("Done.");

		System.out.println("Initializing Converter...");
		KoreanC2DConverter converter = new KoreanC2DConverter(hr);
		System.out.println("Done.");

//		String path = "/Users/jayeolchun/Documents/Research/NLP/Korean/data/penn/newswire";
		String path = "/Users/jayeolchun/Documents/Research/NLP/Korean/data/penn/sample";
		CTReader reader = new CTReader();
		CTTree tree;

		System.out.println("Begin Converting.");
		for (String filename : FileUtils.getFileList(path, "parse"))
		{
			System.out.println(filename + "\n");
			reader.open(IOUtils.createFileInputStream(filename));

			while ((tree = reader.nextTree()) != null)
			{
//				count(tree.getRoot(), phraseTags, posTags, functionTags, emptyCategories);
//				wc += tree.getTokenList().size();
//				converter.toDependencyGraph(tree);
				System.out.println(tree);
				converter.toDependencyGraph(tree);
				System.out.println(tree);
				System.out.println();
			}

			reader.close();
		}




		System.out.println("Conversion Complete.");
	}
}
