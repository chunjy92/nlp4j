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
package edu.emory.mathcs.nlp.common.treebank;

import edu.emory.mathcs.nlp.common.constituent.CTLib;
import edu.emory.mathcs.nlp.common.constituent.CTNode;
import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.common.util.PatternUtils;

import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Created by jayeolchun on 3/30/17.
 */
public class PKTBLib extends CTLib implements PKTBTag
{
    static final public Pattern P_PASSIVE_NULL = PatternUtils.createClosedORPattern("\\*","\\*-\\d+");

    static final public Predicate<CTNode> M_NP			= matchC(C_NP);
    static final public Predicate<CTNode> M_VP			= matchC(C_VP);
    //    static final public Predicate<CTNode> M_QP			= matchC(C_QP);
//    static final public Predicate<CTNode> M_ADVP		= matchC(C_ADVP);
//    static final public Predicate<CTNode> M_SBAR		= matchC(C_SBAR);
//    static final public Predicate<CTNode> M_EDITED		= matchC(C_EDITED);
//
    static final public Predicate<CTNode> M_SBJ			= matchF(F_SBJ);
    //    static final public Predicate<CTNode> M_NOM			= matchF(F_NOM);
//    static final public Predicate<CTNode> M_PRD			= matchF(F_PRD);
//
    static final public Predicate<CTNode> M_NP_SBJ		= matchCF(C_NP, F_SBJ);
//
//    static final public Predicate<CTNode> M_NNx			= matchCp(P_NN);
//    static final public Predicate<CTNode> M_VBx			= matchCp(P_VB);
//    static final public Predicate<CTNode> M_WHx			= matchCp("WH");
//    static final public Predicate<CTNode> M_Sx			= matchCp(C_S);
//    static final public Predicate<CTNode> M_SBARx		= matchCp(C_SBAR);
//
//    static final public Predicate<CTNode> M_S_SBAR		= matchCo(DSUtils.toHashSet(C_S, C_SBAR));
//    static final public Predicate<CTNode> M_NP_NML		= matchCo(DSUtils.toHashSet(C_NP, C_NML));
//    static final public Predicate<CTNode> M_VBD_VBN		= matchCo(DSUtils.toHashSet(P_VBD, P_VBN));
//    static final public Predicate<CTNode> M_VP_RRC_UCP	= matchCo(DSUtils.toHashSet(C_VP, C_RRC, C_UCP));
//
//    static final private Set<String> S_LGS_PHRASE		= DSUtils.toHashSet(C_PP, C_SBAR);
//    static final private Set<String> S_MAIN_CLAUSE		= DSUtils.toHashSet(C_S, C_SQ, C_SINV);
//    static final private Set<String> S_EDITED_PHRASE	= DSUtils.toHashSet(C_EDITED, C_EMBED);
//    static final private Set<String> S_NOMINAL_PHRASE	= DSUtils.toHashSet(C_NP, C_NML, C_NX, C_NAC);
//    static final private Set<String> S_WH_LINK			= DSUtils.toHashSet(C_WHNP, C_WHPP, C_WHADVP);
//    static final private Set<String> S_SEPARATOR		= DSUtils.toHashSet(P_COMMA, P_COLON);
//    static final private Set<String> S_CONJUNCTION		= DSUtils.toHashSet(P_CC, C_CONJP);
//
    static private final Set<String> S_PUNCTUATION = DSUtils.toHashSet(PKTBTag.P_COLON, PKTBTag.P_SEMICOLON,
        PKTBTag.P_COMMA, PKTBTag.P_PERIOD, PKTBTag.P_ELLIPSIS, PKTBTag.P_QUESTION, PKTBTag.P_EXCLAMATION,
        PKTBTag.P_HYPHEN, PKTBTag.P_LQ, PKTBTag.P_RQ, PKTBTag.P_LRB, PKTBTag.P_RRB, PKTBTag.P_LCB, PKTBTag.P_RCB,
        PKTBTag.P_LSB, PKTBTag.P_RSB);

//
//    public static final Set<String> S_RELATIVIZER = DSUtils.toHashSet(PTBTag.P_WDT, PTBTag.P_WP, PTBTag.P_WPS, PTBTag.P_WRB);

    static public boolean isPunctuation(String posTag)
    {
        return S_PUNCTUATION.contains(posTag);
    }

    private PKTBLib() {}


}