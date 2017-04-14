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
import edu.emory.mathcs.nlp.conversion.util.HeadTagSet;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class KoreanC2DConverter extends C2DConverter
{

	private final Set<String> S_MARK		= DSUtils.toHashSet(PTBTag.P_IN, PTBTag.P_TO, PTBTag.P_DT);
	private final Set<String> S_META		= DSUtils.toHashSet(PKTBTag.C_LST);

	private final Set<String> S_NOUN_PHRASE	= DSUtils.toHashSet(PKTBTag.C_NP);
	private final Set<String> S_PUNCT		= DSUtils.toHashSet(PKTBTag.P_SCM, PKTBTag.P_SFN, PKTBTag.P_SLQ, PKTBTag.P_SRQ, PKTBTag.P_SSY);
//	private final Set<String> S_PUNCT		= DSUtils.toHashSet(PKTBTag.P_SCM, PKTBTag.P_SFN, PKTBTag.P_SLQ, PKTBTag.P_SRQ);


	/** Syntactic function tags. */
	private final Set<String> SYN_TAGS = DSUtils.toHashSet(PKTBTag.F_ADV, PKTBTag.F_COMP, PKTBTag.F_LV, PKTBTag.F_OBJ, PKTBTag.F_SBJ, PKTBTag.F_VOC);

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

			if (node.wordFormStartsWith(PKTBTag.E_TRACE)){
				mapTrace(cTree, node);
			}else if (node.wordFormStartsWith(PKTBTag.E_PRO)){
				mapPRO(cTree, node);
//			} else if (node.wordFormStartsWith(PTBTag.E_OP)){

			} else if (node.wordFormStartsWith(PKTBTag.E_ESM)){

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
//
//		// 1. apposition
//		findHeadsApposition(curr);
//
//		// 2. coordination head finding : left-branching
//
//		CTNode head = getHead(rule, curr.getChildrenList(), SIZE_HEAD_FLAGS);
//		if (head.getC2DInfo().getLabel() != null) head.getC2DInfo().setLabel(null);
//		curr.setC2DInfo(new C2DInfo(head));

		System.out.println("\n--- Inside SetHeadsAUX Method wihtin Korean C2D Converter...");
		// 1. coordination
//		if (findHeadsCoordination(rule, curr))	return;

//		System.out.println("Currently,");
//		System.out.println(curr);

		CTNode head = getHead(rule, curr.getChildrenList(), SIZE_HEAD_FLAGS);
		System.out.print(" ** Within Set Heads, what is head?\n\t");
		System.out.println("Current is:");
		System.out.println(curr);
		System.out.println("Head is:");
		System.out.println(head);
		System.out.println(head.getC2DInfo().getLabel());
		if (head.getC2DInfo().getLabel() != null) head.getC2DInfo().setLabel(null);
		curr.setC2DInfo(new C2DInfo(head));
		System.out.println("** Update!");
		System.out.println(head);
		System.out.println(head.getC2DInfo().getLabel());
		System.out.println(head.getC2DInfo().getTerminalHead());
		System.out.println(head.getC2DInfo().getNonTerminalHead());
		System.out.println("Finished set Heads Aux..\n");
		System.out.println("\n--- Exiting SetHeadsAUX Method wihtin Korean C2D Converter...");

		
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
		return node.hasFunctionTag(PKTBTag.F_ADV);
	}

	/* (non-Javadoc)
	 * @see edu.emory.mathcs.nlp.conversion.C2DConverter#getHeadFlag(edu.emory.mathcs.nlp.common.constituent.CTNode)
	 */
	@Override
	protected int getHeadFlag(CTNode child)
	{
		C2DInfo info = child.getC2DInfo();

		if (info.hasHead())// && info.getTerminalHead() != info.getNonTerminalHead())
			return -1;

		if (hasAdverbialTag(child))
			return 1;

		if (isMeta(child))
			return 2;

		if (child.isEmptyCategoryTerminal() || PKTBLib.isPunctuation(child.getConstituentTag()))
			return 3;

		return 0;
	}

	// obj vs iobj
	private String getObjectLabel(CTNode C)
	{
//		if (node.isConstituentTagAny(S_NOUN_PHRASE))
		if (C.hasFunctionTag(PKTBTag.F_OBJ))
		{
//			if (node.hasFunctionTag(PTBTag.F_PRD))
//				return DEPTagEn.DEP_ATTR;
//			else
//				return DEPTagEn.DEP_DOBJ;
			return UDTag.D_OBJ;
		}

		return null;
	}

	// nsubj vs csubj
	private String getSubjectLabel(CTNode C, CTNode P)
	{

		if (C.hasFunctionTag(PKTBTag.F_SBJ))
		{
//			if (PTBLib.isClause(C))
//				return DEPTagEn.DEP_CSUBJ;
//			else if (d.isConstituentTag(PTBTag.P_EX) || d.isWordFormIgnoreCase("there"))
//				return DEPTagEn.DEP_EXPL;
//			else

//				return DEPTagEn.DEP_NSUBJ;
			if (P.hasFunctionTag(PKTBTag.F_COMP))
				return UDTag.D_CSUBJ;
			return UDTag.D_NSUBJ;
		}
//		else if (C.hasFunctionTag(PTBTag.F_LGS))
//			return DEPTagEn.DEP_AGENT;

		return null;
	}

	private String getVocativeLabel(CTNode C, CTNode d)
	{
		if (C.hasFunctionTag(PKTBTag.F_VOC))
		{
//			if (PTBLib.isClause(C))
//				return DEPTagEn.DEP_CSUBJ;
//			else if (d.isConstituentTag(PTBTag.P_EX) || d.isWordFormIgnoreCase("there"))
//				return DEPTagEn.DEP_EXPL;
//			else

//				return DEPTagEn.DEP_NSUBJ;
			return UDTag.D_VOCATIVE;
		}
//		else if (C.hasFunctionTag(PTBTag.F_LGS))
//			return DEPTagEn.DEP_AGENT;

		return null;
	}

	private String getAdverbialLabel(CTNode C, CTNode d)
	{
		if (C.hasFunctionTag(PKTBTag.F_ADV))
		{
//			if (PTBLib.isClause(C))
//				return DEPTagEn.DEP_CSUBJ;
//			else if (d.isConstituentTag(PTBTag.P_EX) || d.isWordFormIgnoreCase("there"))
//				return DEPTagEn.DEP_EXPL;
//			else

//				return DEPTagEn.DEP_NSUBJ;

			if (d.getLastConstituentTag().equals(PKTBTag.P_PAD))
				return UDTag.D_CASE;

			return UDTag.D_ADVMOD;
		}
//		else if (C.hasFunctionTag(PTBTag.F_LGS))
//			return DEPTagEn.DEP_AGENT;

		return null;
	}

	private String getNmodLabel(CTNode C, CTNode d)
	{
//		if (C.isConstituentTagAny(S_PARTICIPIAL))
//			return DEPTagEn.DEP_AMOD;

//		if (C.isConstituentTagAny(PKTBTag.))
//			return DEPTagEn.DEP_DET;

//		if (C.isConstituentTagAny(S_NN) || (C.matches(CTLibEn.M_NNx) || C.isConstituentTag(CTLibEn.P_FW)))
//			return DEPTagEn.DEP_COMPOUND;

//		if (C.isConstituentTagAny(S_NUM) || d.isConstituentTag(CTLibEn.P_CD))
//			return DEPTagEn.DEP_NUMMOD;

		if (C.isConstituentTag(PKTBTag.P_NNU))
			return UDTag.D_NUMMOD;
		if (d.getLastConstituentTag().equals(PKTBTag.P_PCJ))
			return UDTag.D_CONJ;
//		if (C.isConstituentTag(PTBTag.P_POS))
//			return DEPTagEn.DEP_CASE;
//
//		if (C.isConstituentTag(PTBTag.P_PDT))
//			return DEPTagEn.DEP_PREDET;

//		return DEPTagEn.DEP_NMOD;
		return UDTag.D_NMOD;
	}

	/* (non-Javadoc)
	 * @see edu.emory.mathcs.nlp.conversion.C2DConverter#getDEPLabel(edu.emory.mathcs.nlp.common.constituent.CTNode, edu.emory.mathcs.nlp.common.constituent.CTNode, edu.emory.mathcs.nlp.common.constituent.CTNode)
	 */
	@Override
	protected String getDEPLabel(CTNode C, CTNode P, CTNode head)
	{
		// C : child node
		// P : parent node
		// head : head node
		CTNode c = C.getC2DInfo().getNonTerminalHead(); // c : head constituent of C
		CTNode d = C.getC2DInfo().getTerminalHead(); // d : head dependent of C
		String dw = d.getWordForm();
		String label = "";

		System.out.println("\n--- Inside Get Dep Label within Korean C2D Converter...");
		System.out.println("Child:\n\t" + C);
		System.out.println("Child NT head:\n\t" + c);
		System.out.println("Child T head:\n\t" + d);
		System.out.println(d.getConstituentTag());
		System.out.println("Parent:\n\t" + P);
		System.out.println("Head:\n\t" + head);

		if (head.getTokenID()==0)
			return UDTag.D_ROOT;

		if (C.isConstituentTag(PKTBTag.C_PRN)){
			System.out.println("Parenthetical expression");
			return UDTag.D_APPOS;
		}

//		if (d.isConstituentTag(PKTBTag.P_NNX))
//			return UDTag.D_CLF;

		if (d.isConstituentTag(PKTBTag.P_DAN))
			return UDTag.D_DET;

		if (d.isConstituentTag(PKTBTag.P_ADC)){
			if (dw.equals("또") || dw.equals("및") || dw.equals("또는") || dw.equals("그리고")) {
				System.out.println("Conjunctive Adverb");
				return UDTag.D_CC;
			}
			return UDTag.D_ADVMOD;
		}

		if (C.isConstituentTagAny(S_PUNCT)){
			System.out.println("PUNCT");
			return UDTag.D_PUNCT;
		}

		if (d.isConstituentTagSetAny(PKTBTag.H_VX)){
			System.out.println("Auxiliary Verb");
			return UDTag.D_AUX;
		}

		if (d.isConstituentTag(PKTBTag.H_ADV)){
			System.out.println("ADV!!@");
			return UDTag.D_ADVMOD;
		}

		if (C.isConstituentTag(PKTBTag.C_ADJP) || d.isConstituentTagSetAny(PKTBTag.P_VJ))
			return UDTag.D_AMOD;




//		if (PKTBLib.isPunctuation(C.getConstituentTag()))



		if (C.hasFunctionTag(PKTBTag.F_COMP)){
			System.out.println("COMP!!!!");
//			String s = C.getConstituentTag();
//			String[] se = s.split("\\+");
			System.out.println(c.getLastWordForm());

//			System.out.println(se[se.length-1]);
			String s = d.getLastConstituentTag();
			String w = d.getLastWordForm();
			if (s.equals("PAD") && (w.equals("에게") || w.equals("게"))){
				System.out.println("INDIRECT OBJ");
				return UDTag.D_IOBJ;
			}
//			System.out.println(s);
		}


//		if (head.getFunctionTagSet())
//		System.out.println("Pribnting child's function tagset");
//		for (String a : C.getFunctionTagSet()){
//			System.out.println(a);
//		}
		// -SBJ function tags
		if ((label = getSubjectLabel(C, P)) != null) {
			System.out.println("SBJ Funciton TAgs?");
			System.out.println(label);
			return label;
		} else if ((label = getObjectLabel(C)) != null) {
			System.out.println("OBJ Funciton TAgs?");
			System.out.println(label);
			return label;
		} else if ((label = getAdverbialLabel(C, d)) != null) {
			System.out.println("ADV Funciton TAgs?");
			System.out.println(label);
			return label;
		} else if ((label = getVocativeLabel(C, d)) != null) {
			System.out.println("AVOC Funciton TAgs?");
			System.out.println(label);
			return label;
		}

		if (P.isConstituentTag(PKTBTag.C_NP)) {
			System.out.println("N MOD");
			return getNmodLabel(C, d);
		} else if (P.isConstituentTag(PKTBTag.H_CV)){
			System.out.println("Compound VERB");
			return UDTag.D_COMPOUND;
		}

		if (d.getLastConstituentTag().equals(PKTBTag.P_PAD)) {
			return UDTag.D_ADVMOD;
		} else if (d.getConstituentTag().equals(PKTBTag.P_PCA))
			return UDTag.D_CASE;










//		return null;
//		return "subj";
		return UDTag.D_DEP;
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

		addDEPHeads(dTree, cTree);
		System.out.println("\nDone.\n");

		System.out.println("\nPrinting each node..");
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
				System.out.println("Label??");
				System.out.println(label);
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

//		if ((feat = getFunctionTags(cNode, SEM_TAGS)) != null)
//			cNode.getC2DInfo().putFeat(NLPUtils.FEAT_SEM, feat);

		if ((feat = getFunctionTags(cNode, SYN_TAGS)) != null)
			cNode.getC2DInfo().putFeat(NLPUtils.FEAT_SYN, feat);

//		System.out.println(cNode);
//		System.out.println("FEAT!!!!");
//		System.out.println(feat);

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
			if (isEmoticon(node))
				node.setDependencyLabel(DEPTagEn.DEP_DISCOURSE);
			else if (isVocative(node))
				node.setDependencyLabel(DEPTagEn.DEP_VOCATIVE);
		}
	}

	private boolean isDative(NLPNode node) // ~ 에, ~ 에게, ~ 한
	{
		if (!PTBLib.isVerb(node.getDependencyHead().getPartOfSpeechTag())) return false;
//		if (node.isDependencyLabel(DEPTagEn.DEP_IOBJ)) return true;
		String feat;

		if ((feat = node.getFeat(NLPUtils.FEAT_SYN)) != null && DSUtils.toHashSet(Splitter.splitCommas(feat)).contains(PTBTag.F_DTV)) return true;
		return false;
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
		String headrule_path = "src/main/resources/edu/emory/mathcs/nlp/conversion/headrule_kr_penn.txt";
//		headrule_path = "src/main/resources/edu/emory/mathcs/nlp/conversion/headrule_en_conll.txt";

		System.out.println("Reading in HeadRule Map...");
		HeadRuleMap hr = new HeadRuleMap(IOUtils.createFileInputStream(headrule_path));
		System.out.println("Done.");

		System.out.println("Initializing Converter...");
		KoreanC2DConverter converter = new KoreanC2DConverter(hr);
		System.out.println("Done.");

//		String path = "/Users/jayeolchun/Documents/Research/NLP/Korean/data/penn/newswire";
		String path = "/Users/jayeolchun/Documents/Research/NLP/Korean/data/penn/sample";
//		String out = "/Users/jayeolchun/Documents/Research/NLP/Korean/out/penn/newswire";
		CTReader reader = new CTReader();
		CTTree tree;

		System.out.println("Begin Converting.");
		for (String filename : FileUtils.getFileList(path, "parse"))
		{
			System.out.println(filename + "\n");
			reader.open(IOUtils.createFileInputStream(filename));

			while ((tree = reader.nextTree()) != null)
			{
				System.out.println(tree);
				converter.toDependencyGraph(tree);
				NLPNode[] dTree = converter.toDependencyGraph(tree);

			}

			reader.close();
		}




		System.out.println("Conversion Complete.");
	}

	private boolean isEmoticon(NLPNode node)
	{
		String s = node.getWordForm();
		int[] idx = emoticon.getEmoticonRange(s);
		return idx != null && idx[0] == 0 && idx[1] == s.length();
	}

	private boolean isVocative(NLPNode node)
	{
//		String feat;

//		return (feat = node.getFeat(NLPUtils.FEAT_SEM)) != null && feat.equals(PTBLib.F_VOC);

		String feat;
		return (feat = node.getFeat(NLPUtils.FEAT_SYN)) != null && feat.equals(PKTBLib.F_VOC);
	}
	private boolean isMeta(CTNode node)
	{
		return node.isConstituentTagAny(S_META);
	}


	public void addNamedEntities(NLPNode[] dTree, CTTree cTree)
	{
		for (CTNode node : cTree.getTokenList())
			dTree[node.getTokenID()+1].setNamedEntityTag(node.getNamedEntityTag());
	}
}
