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

import edu.emory.mathcs.nlp.common.constituent.CTNode;
import edu.emory.mathcs.nlp.common.constituent.CTReader;
import edu.emory.mathcs.nlp.common.constituent.CTTree;
import edu.emory.mathcs.nlp.common.treebank.PTBTag;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.conversion.util.HeadRule;
import edu.emory.mathcs.nlp.conversion.util.HeadRuleMap;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class KoreanC2DConverter extends C2DConverter
{
	public KoreanC2DConverter(HeadRuleMap headrules)
	{
		super(headrules, new HeadRule(HeadRule.DIR_RIGHT_TO_LEFT));		
	}
	
	@Override
	public NLPNode[] toDependencyGraph(CTTree cTree)
	{
		if (!mapEmtpyCategories(cTree))	return null;
		setHeads(cTree.getRoot());
		
		NLPNode[] tree = null;
		return tree;
	}
	
//	============================= Empty Categories =============================
	
	private boolean mapEmtpyCategories(CTTree cTree)
	{
		for (CTNode node : cTree.getTerminalList())
		{
			if (!node.isEmptyCategory())	continue;
			if (node.getParent() == null)	continue;
			
			if (node.wordFormStartsWith(PTBTag.E_TRACE))
				mapTrace(cTree, node);
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


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	/* (non-Javadoc)
	 * @see edu.emory.mathcs.nlp.conversion.C2DConverter#setHeadsAux(edu.emory.mathcs.nlp.conversion.util.HeadRule, edu.emory.mathcs.nlp.common.constituent.CTNode)
	 */
	@Override
	protected void setHeadsAux(HeadRule rule, CTNode curr)
	{
		// TODO Auto-generated method stub
		
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



	static public void main(String[] args){
		String headrule_path = "src/main/resources/edu/emory/mathcs/nlp/conversion/headrule_en_conll.txt";
//		String headrule_path = "src/main/resources/edu/emory/mathcs/nlp/conversion/headrule_kr_penn.txt";
		System.out.println("Reading in HeadRule Map...");
		HeadRuleMap hr = new HeadRuleMap(IOUtils.createFileInputStream(headrule_path));
		System.out.println("Done.");

		System.out.println("Initializing Converter...");
		KoreanC2DConverter converter = new KoreanC2DConverter(hr);
		System.out.println("Done.");

		String path = "/Users/jayeolchun/Documents/Research/NLP/Korean/data/penn/newswire";
		CTReader reader = new CTReader();
		CTTree tree;

		System.out.println("Begin Converting.");
		for (String filename : FileUtils.getFileList(path, "parse"))
		{
			System.out.println(filename);
			reader.open(IOUtils.createFileInputStream(filename));

			while ((tree = reader.nextTree()) != null)
			{
//				count(tree.getRoot(), phraseTags, posTags, functionTags, emptyCategories);
//				wc += tree.getTokenList().size();
				converter.toDependencyGraph(tree);
			}

			reader.close();
		}




		System.out.println("Conversion Complete.");
	}
}
