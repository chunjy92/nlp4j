/**
 * Copyright 2014, Emory University
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.emory.mathcs.nlp.common.constituent.CTNode;
import edu.emory.mathcs.nlp.common.constituent.CTTree;
import edu.emory.mathcs.nlp.common.treebank.CTTag;
import edu.emory.mathcs.nlp.common.util.PatternUtils;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.conversion.util.C2DInfo;
import edu.emory.mathcs.nlp.conversion.util.HeadRule;
import edu.emory.mathcs.nlp.conversion.util.HeadRuleMap;
import edu.emory.mathcs.nlp.conversion.util.HeadTagSet;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
abstract public class C2DConverter
{
	protected HeadRuleMap headrule_map;
	protected HeadRule    default_rule;
	
	/** @param defaultRule use this rule when no specified headrule matches. */
	public C2DConverter(HeadRuleMap headruleMap, HeadRule defaultRule)
	{
		headrule_map = headruleMap;
		default_rule = defaultRule;
	}
	
	/**
	 * @return the dependency graph converted from the constituent tree.
	 * If the constituent tree contains only empty categories, returns {@code null}.
	 * @param cTree the constituent tree to be converted.
	 */
	abstract public NLPNode[] toDependencyGraph(CTTree cTree);
	
//	============================= Empty categories ============================= 
	
	protected void removeNode(CTNode node)
	{
		CTNode parent = node.getParent();
	
		if (parent != null)
		{
			parent.removeChild(node);
			
			if (parent.getChildrenSize() == 0)
				removeNode(parent);			
		}
	}
	
	protected void replaceEmptyCategory(CTNode ec, CTNode ante)
	{
		removeNode(ante);
		ec.getParent().replaceChild(ec, ante);
	}
	
	
	/**
	 * Sets the head of the specific node and all its sub-nodes.
	 * Calls {@link C2DConverter#findHeads(CTNode)}.
	 */
	protected void setHeads(CTNode curr)
	{
		System.out.println("\n--- Inside Set Heads Method wihtin C2D Converter...");
		System.out.println("Current CT Node");
		System.out.println(curr);
		// terminal nodes become the heads of themselves
		if (curr.isTerminal())
		{
			System.out.println("Is Terminal? Itself becomes the head. return;;");
			curr.setC2DInfo(new C2DInfo(curr));
			System.out.println(curr.getC2DInfo().getTerminalHead());
			System.out.println(curr.getC2DInfo().getNonTerminalHead());
			return;
		}
		
		// set the heads of all children
		for (CTNode child : curr.getChildrenList()){
			System.out.println("Set heads of all children");
			setHeads(child);
			System.out.println("\n *** REturnined.");
			System.out.println("Current node is:");
			System.out.println(curr);
		}

		
//		 stop traversing if it is the top node
		if (curr.isConstituentTag(CTTag.TOP)){
			System.out.println("Encountered Top! Returning..");
//			setHeads()
			return;
		}

		// only one child
		if (curr.getChildrenSize() == 1)
		{
			System.out.println("Only Child?");
			curr.setC2DInfo(new C2DInfo(curr.getChild(0)));
			System.out.println(curr);
			System.out.println(curr.getC2DInfo().getNonTerminalHead());
			System.out.println(curr.getC2DInfo().getTerminalHead());

			return;
		}
		
		// find the headrule of the current node
		HeadRule rule = headrule_map.get(curr.getConstituentTag());
		System.out.println("Found the HEadrule:");
		System.out.println(rule);
				
		if (rule == null)
		{
			System.err.println("Error: headrules not found for \""+curr.getConstituentTag()+"\"");
			rule = default_rule;
		}
		
		// abstract method
		System.out.println("Calling Set Heads Aux from Set Heads Method!");
		System.out.println("Remember, curr is:");
		System.out.println(curr);
		setHeadsAux(rule, curr);
	}
	
	/**
	 * @return the head of the input node-list according to the headrule.
	 * Every other node in the list becomes the dependent of the head node.
	 * @param rule the headrule to be consulted.
	 * @param nodes the list of nodes.
	 * @param flagSize the number of head flags.
	 */
	protected CTNode getHead(HeadRule rule, List<CTNode> nodes, int flagSize)
	{
		System.out.println("\n--- Inside Get Head Method wihtin C2D Converter...");
		CTNode head = getDefaultHead(nodes);
		System.out.println("Current Default Head!");
		System.out.println(head);
		System.out.println("Current Rule");
		System.out.println(rule);
		
		if (head == null)
		{
			nodes = new ArrayList<>(nodes);
			if (rule.isRightToLeft()) Collections.reverse(nodes);
			
			int i, size = nodes.size(), flag;
			int[] flags = new int[size];
			System.out.println("Size: " + size);
			CTNode child;

			System.out.println("Looping through nodes..");
			// gets head Flag number for each node
			for (i=0; i<size; i++){
				CTNode a = nodes.get(i);

				System.out.println(a);
				System.out.println(getHeadFlag(a));
				flags[i] = getHeadFlag(nodes.get(i));
			}
			System.out.println("\nFirst Loop");
			outer: for (flag=0; flag<flagSize; flag++)
			{
				System.out.println("Curent Flag: " + flag);
				for (HeadTagSet tagset : rule.getHeadTags()) // for each tag wihtin a single line..
				{
					System.out.println("Current Tagset");
					System.out.println(tagset);
					for (i=0; i<size; i++) // for each child
					{
						child = nodes.get(i);
						System.out.println("Current child");
						System.out.println(child);
						if (flags[i] == flag && tagset.matches(child))
						{
							System.out.println("Head Tagset 1st Mathc!!!");
							head = child;
							System.out.println(child);
							System.out.println(tagset);
							break outer;
						}
					}
				}
			}

			System.out.println("\nSEcond Loop");
			outer: for (flag=0; flag<flagSize; flag++)
			{
				for (HeadTagSet tagset : rule.getHeadTags())
				{
					System.out.println("Current Tagset");
					System.out.println(tagset);
					for (i=0; i<size; i++)
					{
						child = nodes.get(i);
						System.out.println("Current child");
						System.out.println(child);
						if (flags[i] == flag && tagset.matches(child))
						{
							System.out.println("Head Tagset 2nd Mathc!!!");
							head = child;
							System.out.println(child);
							System.out.println(tagset);
							break outer;
						}
					}
				}
			}
		}

		System.out.println("\nAfter looping, the head has become:");
		System.out.println(head);
		
		if (head == null)
			throw new IllegalStateException("Head not found");
		
		CTNode parent = head.getParent();
		
		for (CTNode node : nodes)
		{
			if (node != head && !node.getC2DInfo().hasHead()) {
				System.out.println("Going to call Get Dep Label from SEt Heads method with:");
				System.out.println("Child Node:");
				System.out.println(node);
				System.out.println("Parent:");
				System.out.println(parent);
				System.out.println("Head:");
				System.out.println(head);
				System.out.println(node.getC2DInfo().hasHead());
				System.out.println(node.getC2DInfo().getNonTerminalHead());
				System.out.println(node.getC2DInfo().getTerminalHead());
				node.getC2DInfo().setHead(head, getDEPLabel(node, parent, head)); // get dep label here

			}
		}
		System.out.println("\n--- Exiting Get Head Method wihtin C2D Converter...");
		return head;
	}
	
	/** @return the default head if it is the only node in the list that is not an empty category. */
	private CTNode getDefaultHead(List<CTNode> nodes)
	{
		CTNode head = null;
		
		for (CTNode node : nodes)
		{
			if (!node.isEmptyCategoryTerminal())
			{
				if (head != null) return null;
				head = node;
			}
		}

		return head;
	}
	
	/** @return the dependency tree converted from the specific constituent tree without head information. */
	protected NLPNode[] initDEPTree(CTTree cTree)
	{
		System.out.println("Init Dep Tree");
		List<CTNode>  cNodes = cTree.getTokenList();
		NLPNode[]     dNodes = new NLPNode[cNodes.size()+1];
		String form, pos;
		NLPNode dNode;
		int id;

		dNodes[0] = new NLPNode().toRoot();

		for (CTNode cNode : cNodes)
		{
			id   = cNode.getTokenID()+1;
			form = PatternUtils.revertBrackets(cNode.getWordForm());
			pos  = cNode.getConstituentTag();

			dNode = new NLPNode(id, form, pos, cNode.getC2DInfo().getFeatMap());
			dNode.setSecondaryHeads(new ArrayList<>());
			dNodes[id] = dNode;
		}
		return dNodes;
	}
	
	/**
	 * Sets the head of the specific constituent node using the specific headrule.
	 * Called by {@link #setHeads(CTNode)}.
	 */
	abstract protected void setHeadsAux(HeadRule rule, CTNode curr);
	
	/**
	 * @return the head flag of the specific constituent node.
	 * @see EnglishC2DConverter#getHeadFlag(CTNode).
	 */
	abstract protected int getHeadFlag(CTNode child);
	
	/**
	 * Returns a dependency label given the specific phrase structure.
	 * @param C the current node.
	 * @param P the parent of {@code C}.
	 * @param p the head of {@code P}.
	 * @return a dependency label given the specific phrase structure.
	 */
	abstract protected String getDEPLabel(CTNode C, CTNode P, CTNode p);
}