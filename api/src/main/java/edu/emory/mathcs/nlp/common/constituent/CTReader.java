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
package edu.emory.mathcs.nlp.common.constituent;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.StringTokenizer;

import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.treebank.CTTag;

/**
 * Constituent tree reader.
 * @see CTTree 
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class CTReader
{
	private LineNumberReader f_reader;
	private Deque<String>    d_tokens;
	
	public CTReader() {}
	
	/** @param in internally wrapped by {@code new LineNumberReader(new InputStreamReader(new BufferedInputStream(in)))}}. */
	public CTReader(InputStream in)
	{
		open(in);
	}
	
	/** @param in internally wrapped by {@code new LineNumberReader(new InputStreamReader(new BufferedInputStream(in)))}}. */
	public void open(InputStream in)
	{
		open(in, "UTF-8");
	}
	
	public void open(InputStream in, String charsetName)
	{
		try
		{
			f_reader = new LineNumberReader(new InputStreamReader(new BufferedInputStream(in), charsetName));
			d_tokens = new ArrayDeque<String>();
		}
		catch (UnsupportedEncodingException e) {e.printStackTrace();}
	}
	
	/** Closes the current reader. */
	public void close()
	{
		if (f_reader != null)
		{
			try
			{
				f_reader.close();
			}
			catch (IOException e) {e.printStackTrace();}			
		}
	}
	
	/** @return a list of all constituent trees in the input stream. */
	public List<CTTree> getTreeList()
	{
		List<CTTree> trees = new ArrayList<>();
		CTTree tree;
		
		while ((tree = nextTree()) != null)
			trees.add(tree);

		return trees;
	}
	
	/**
	 * @return the next tree if exists; otherwise, {@code null}.
	 * Returns {@code null} if the next tree is incomplete or erroneous.
	 * Automatically links antecedents of all co-indexed empty categories.
	 */
	public CTTree nextTree()
	{
//		System.out.println("Insie CT Tree Next Trea");
		String token = nextToken(), tags;
		
		if (token == null)
			return null;
		
		if (!token.equals(StringConst.LRB))
		{
			System.err.println("Error: \""+token+"\" found, \"(\" expected - line "+f_reader.getLineNumber());
			return null;
		}
		
		int nBrackets = 1, startLine = f_reader.getLineNumber();
//		System.out.println(startLine);
		CTNode root = new CTNode(CTTag.TOP, null);
		CTNode curr = root, node;
//		System.out.println(root);
//		System.out.println(curr);

		if (token.equals(StringConst.LRB)){
			tags = nextToken();
			node = new CTNode(tags);
			curr.addChild(node);
			curr = node;
//			nBrackets++;
		}

		while ((token = nextToken()) != null)
		{
			if (nBrackets == 1 && token.equals(CTTag.TOP))
				continue;

			if (token.equals(StringConst.LRB))
			{
				tags = nextToken();
				node = new CTNode(tags);
				curr.addChild(node);
				curr = node;
				nBrackets++;
			}
			else if (token.equals(StringConst.RRB))
			{
				curr = curr.getParent();
				nBrackets--;
			}
			else
				curr.setWordForm(token);

			if (nBrackets == 0)
				return new CTTree(root);
		}
		
		System.err.println("Error: brackets mismatch - starting line "+startLine);
		return null;
	}
	
	/**
	 * @return the next tree after skipping the specific number of trees if exists; otherwise, {@code null}.
	 * @param skip the number of trees to skip.
	 */
	public CTTree nextTree(int skip)
	{
		CTTree tree = null;
		int i;
		
		for (i=0; i<=skip; i++)
		{
			tree = nextTree();
			if (tree == null) return null;
		}
		
		return tree;
	}

	/** Called by {@link #nextTree()}. */
	private String nextToken()
	{
		if (d_tokens.isEmpty())
		{
			String line = null;
			
			try
			{
				line = f_reader.readLine();
			}
			catch (IOException e) {e.printStackTrace();}

			if (line == null)
				return null;
			
			line = line.trim();
			if (line.isEmpty())
				return nextToken();
			
			StringTokenizer tok = new StringTokenizer(line, "() \t\n\r\f", true);
			String str;
			
			while (tok.hasMoreTokens())
			{
				str = tok.nextToken().trim();
				if (!str.isEmpty()) d_tokens.add(str);
			}
		}
		
		return d_tokens.pop();
	}
}