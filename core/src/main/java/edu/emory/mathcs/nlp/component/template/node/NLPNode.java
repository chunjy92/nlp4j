/**
 * Copyright 2015, Emory University
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
package edu.emory.mathcs.nlp.component.template.node;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NLPNode extends AbstractNLPNode<NLPNode>
{
	private static final long serialVersionUID = 5522467283393796925L;

	public NLPNode() {}

	/** To set word_form,start and end offset of word_form. */
    public NLPNode(String form, int startOffset, int endOffset)
    {
    	this(-1, form);
    	setStartOffset(startOffset);
        setEndOffset  (endOffset);
    }
    
	public NLPNode(int id, String form)
	{
		this(id, form, null);
	}
	
	public NLPNode(int id, String form, String posTag)
	{
		this(id, form, null, posTag, new FeatMap());
	}
	
	public NLPNode(int id, String form, String lemma, String posTag, FeatMap feats)
	{
		this(id, form, lemma, posTag, null, feats);
	}
	
	public NLPNode(int id, String form, String lemma, String posTag, String namentTag, FeatMap feats)
	{
		this(id, form, lemma, posTag, namentTag, feats, null, null);
	}
	
	public NLPNode(int id, String form, String lemma, String posTag, FeatMap feats, NLPNode dhead, String deprel)
	{
		this(id, form, lemma, posTag, null, feats, dhead, deprel);
	}
	
	public NLPNode(int id, String form, String lemma, String posTag, String namentTag, FeatMap feats, NLPNode dhead, String deprel)
	{
		set(id, form, lemma, posTag, namentTag, feats, dhead, deprel);
	}
	
	@Override
	public NLPNode self()
	{
		return this;
	}
}