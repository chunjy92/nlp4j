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
package edu.emory.mathcs.nlp.common.treebank;

/**
 * Penn Korean Treebank Tags.
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public interface PKTBTag
{
//	/** The special tag for the artificial top node. */
//	String TOP  = "TOP";
//	/** The special tag for empty categories. */
//	String NONE = "-NONE-";

	/** The clausal tag for sentences. */
	String C_S    = "S";
	/** The phrasal tag for coordinate adverb phrases. */
	String C_ADCP = "ADCP";
	/** The phrasal tag for adjective phrases. */
	String C_ADJP = "ADJP";
	/** The phrasal tag for adverb phrases. */
	String C_ADVP = "ADVP";
	/** The phrasal tag for adnoun phrases. */
	String C_DANP = "DANP";
	/** The phrasal tag for interjections. */
	String C_INTJ = "INTJ";
	/** The phrasal tag for lists. */
	String C_LST = "LST";
	/** The phrasal tag for noun phrases. */
	String C_NP   = "NP";
	/** The phrasal tag for parenthetical phrases. */
	String C_PRN  = "PRN";
	/** The phrasal tag for verb phrases. */
	String C_VP   = "VP";
	/** The phrasal tag for wh-noun phrases. */
	String C_WHNP = "WHNP";
	/** The phrasal tag for others. */
	String C_X    = "X";

//	/** The phrasal function tag for adverbials. */
//	String CF_ADV  = "ADV";
//	/** The phrasal function tag for complements. */
//	String CF_COMP = "COMP";
//	/** The phrasal function tag for light verbs. */
//	String CF_LV   = "LV";
//	/** The phrasal function tag for objects. */
//	String CF_OBJ  = "OBJ";
//	/** The phrasal function tag for subjects. */
//	String CF_SBJ  = "SBJ";
//	/** The phrasal function tag for vocatives. */
//	String CF_VOC  = "VOC";

	/** The head-level tag for adverbialization. */
	String H_ADV = "ADV";
	/** The head-level tag for compound verb construction. */
	String H_CV  = "CV";
	/** The head-level tag for light verb construction. */
	String H_LV  = "LV";
	/** The head-level tag for adjectivization. */
	String H_VJ  = "VJ";
	/** The head-level tag for verbalization. */
	String H_VV  = "VV";
	/** The head-level tag for auxiliary predicate. */
	String H_VX  = "VX";

	// ????
	/** The part-of-speech tag for colons. */
	String P_COLON       = ":";
	/** The part-of-speech tag for semi-colons. */
	String P_SEMICOLON   = ";";
	/** The part-of-speech tag for commas. */
	String P_COMMA       = ",";
//	/** The part-of-speech tag for dollar signs. */
//	String P_DOLLAR = "$";
	/** The part-of-speech tag for periods. */
	String P_PERIOD      = ".";
	/** The part-of-speech tag for ellpisis. */
	String P_ELLIPSIS    = "...";
	/** The part-of-speech tag for question marks. */
	String P_QUESTION    = "?";
	/** The part-of-speech tag for exclamation marks */
	String P_EXCLAMATION = "!";
	/** The part-of-speech tag for hyphens */
	String P_HYPHEN		 = "-";
	/** The part-of-speech tag for left quotes. */
	String P_LQ    		 = "``";
	/** The part-of-speech tag for right quotes. */
	String P_RQ    		 = "''";
	/** The part-of-speech tag for left round brackets. */
	String P_LRB   		 = "-LRB-";
	/** The part-of-speech tag for right round brackets. */
	String P_RRB   		 = "-RRB-";
	/** The part-of-speech tag for left curly brackets. */
	String P_LCB   		 = "-LCB-";
	/** The part-of-speech tag for right curly brackets. */
	String P_RCB   		 = "-RCB-";
	/** The part-of-speech tag for left square brackets. */
	String P_LSB   		 = "-LSB-";
	/** The part-of-speech tag for right square brackets. */
	String P_RSB    	 = "-RSB-";

	/** The part-of-speech tag for conjunctive adverb. */
	String P_ADC = "ADC";
	/** The part-of-speech tag for verbal/clausal adverb. */
	String P_ADV = "ADV";
	/** The part-of-speech tag for copula. */
	String P_CO  = "CO";
	/** The part-of-speech tag for adnominal. */
	String P_DAN = "DAN";
	/** The part-of-speech tag for adnominal ending. */
	String P_EAN = "EAN";
	/** The part-of-speech tag for auxiliary ending. */
	String P_EAU = "EAU";
	/** The part-of-speech tag for coordinate/subordinate/adverbial/complementizer ending. */
	String P_ECS = "ECS";
	/** The part-of-speech tag for final ending. */
	String P_EFN = "EFN";
	/** The part-of-speech tag for nominal ending. */
	String P_ENM = "ENM";
	/** The part-of-speech tag for pre-final ending. */
	String P_EPF = "EPF";
	/** The part-of-speech tag for interjection. */
	String P_IJ  = "IJ";
	/** The part-of-speech tag for list. */
	String P_LST = "LST";
	/** The part-of-speech tag for foreign word. */
	String P_NFW = "NFW";
	/** The part-of-speech tag for common noun. */
	String P_NNC = "NNC";
	/** The part-of-speech tag for number. */
	String P_NNU = "NNU";
	/** The part-of-speech tag for dependent noun. */
	String P_NNX = "NNX";
	/** The part-of-speech tag for pronoun. */
	String P_NPN = "NPN";
	/** The part-of-speech tag for proper noun. */
	String P_NPR = "NPR";
	/** The part-of-speech tag for adverbial postposition. */
	String P_PAD = "PAD";
	/** The part-of-speech tag for . */
	String P_PAN = "PAN";
	/** The part-of-speech tag for auxiliary postposition. */
	String P_PAU = "PAU";
	/** The part-of-speech tag for case postposition. */
	String P_PCA = "PCA";
	/** The part-of-speech tag for conjunctive postposition. */
	String P_PCJ = "PCJ";
	/** The part-of-speech tag for comma. */
	String P_SCM = "SCM";
	/** The part-of-speech tag for termination. */
	String P_SFN = "SFN";
	/** The part-of-speech tag for left quotation. */
	String P_SLQ = "SLQ";
	/** The part-of-speech tag for right quotation. */
	String P_SRQ = "SRQ";
	/** The part-of-speech tag for symbol. */
	String P_SSY = "SSY";
	/** The part-of-speech tag for adjective. */
	String P_VJ  = "VJ";
	/** The part-of-speech tag for verb. */
	String P_VV  = "VV";
	/** The part-of-speech tag for auxiliary predicate. */
	String P_VX  = "VX";
	/** The part-of-speech tag for pre-final ending. */
	String P_XPF = "XPF";
	/** The part-of-speech tag for suffix. */
	String P_XSF = "XSF";
	/** The part-of-speech tag for adjectivization suffix. */
	String P_XSJ = "XSJ";
	/** The part-of-speech tag for verbalization suffix. */
	String P_XSV = "XSV";

	/** The function tag for adverbial. */
	String F_ADV  = "ADV";
	/** The function tag for complement. */
	String F_COMP = "COMP";
	/** The function tag for light verb. */
	String F_LV   = "LV";
	/** The function tag for object. */
	String F_OBJ  = "OBJ";
	/** The function tag for subject. */
	String F_SBJ  = "SBJ";
	/** The function tag for vocative. */
	String F_VOC  = "VOC";

	/** The empty category representing ellipsed materials ({@code *?*}). */
	String E_ESM   = "*?*";
	/** The empty category representing traces ({@code *T*}). */
	String E_TRACE = "*T*";
	/** The empty category representing empty operators ({@code *op*}). */
	String E_OP    = "*op*";
	/** The empty category representing little pros ({@code *pro*}). */
	String E_PRO   = "*pro*";
}
