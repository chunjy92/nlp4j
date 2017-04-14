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
 * Created by jayeolchun on 4/7/17.
 */
public interface UDTag {
    // UD version 2.0

    /** UD Phrasal tag for adjective phrases */
    String P_ADJ   = "ADJ";
    /** UD Phrasal tag for adpositions */
    String P_ADP   = "ADP";
    /** UD Phrasal tag for adverbial phrases */
    String P_ADV   = "ADV";
    /** UD Phrasal tag for auxiliary verb phrases */
    String P_AUX   = "AUX";
    /** UD Phrasal tag for coordinating conjunctions */
    String P_CONJ  = "CONJ";
    /** UD Phrasal tag for determiners */
    String P_DET   = "DET";
    /** UD Phrasal tag for interjections */
    String P_INTJ  = "INTJ";
    /** UD Phrasal tag for noun phrases */
    String P_NOUN  = "NOUN";
    /** UD Phrasal tag for numerals */
    String P_NUM   = "NUM";
    /** UD Phrasal tag for particles */
    String P_PART  = "PART";
    /** UD Phrasal tag for pronouns */
    String P_PRON  = "PRON";
    /** UD Phrasal tag for proper nouns */
    String P_PROPN = "PROPN";
    /** UD Phrasal tag for punctuations */
    String P_PUNCT  = "PUNCT";
    /** UD Phrasal tag for subordinating conjunctions */
    String P_SCONJ  = "SCONJ";
    /** UD Phrasal tag for symbols */
    String P_SYM    = "SYM";
    /** UD Phrasal tag for verb phrases */
    String P_VERB   = "VERB";
    /** UD Phrasal tag for other, unidentified phrases */
    String P_X      = "X";

    /** UD 2.0 dependency label for clausal modifier of noun */
    String D_ACL   = "acl";
    /** UD 2.0 dependency label for adverbial clause modifier */
    String D_ADVCL   = "advcl";
    /** UD 2.0 dependency label for adverbial modifier */
    String D_ADVMOD   = "advmod";
    /** UD 2.0 dependency label for adjectival modifier */
    String D_AMOD   = "amod";
    /** UD 2.0 dependency label for appositional modifier */
    String D_APPOS   = "appos";
    /** UD 2.0 dependency label for auxiliary */
    String D_AUX   = "aux";
    /** UD 2.0 dependency label for case marking */
    String D_CASE   = "case";
    /** UD 2.0 dependency label for coordinating conjunction */
    String D_CC   = "cc";
    /** UD 2.0 dependency label for clausal complement */
    String D_CCOMP   = "ccomp";
    /** UD 2.0 dependency label for classifier */
    String D_CLF   = "clf";
    /** UD 2.0 dependency label for compound */
    String D_COMPOUND   = "compound";
    /** UD 2.0 dependency label for conjunct */
    String D_CONJ   = "conj";
    /** UD 2.0 dependency label for copula */
    String D_COP   = "cop";
    /** UD 2.0 dependency label for clausal subject */
    String D_CSUBJ   = "csubj";
    /** UD 2.0 dependency label for unspecified dependency */
    String D_DEP   = "dep";
    /** UD 2.0 dependency label for determiner */
    String D_DET   = "det";
    /** UD 2.0 dependency label for discourse element */
    String D_DISCOURSE   = "discourse";
    /** UD 2.0 dependency label for dislocated element */
    String D_DISLOCATED   = "dislocated";
    /** UD 2.0 dependency label for expletive */
    String D_EXPL   = "amod";
    /** UD 2.0 dependency label for fixed multiword expression */
    String D_FIXED   = "dislocated";
    /** UD 2.0 dependency label for flat multiword expression */
    String D_FLAT   = "amod";
    /** UD 2.0 dependency label for goes with */
    String D_GOESWITH   = "goeswith";
    /** UD 2.0 dependency label for indirect object */
    String D_IOBJ   = "iobj";
    /** UD 2.0 dependency label for list */
    String D_LIST   = "list";
    /** UD 2.0 dependency label for marker */
    String D_MARK   = "marker";
    /** UD 2.0 dependency label for nominal modifier */
    String D_NMOD   = "nmod";
    /** UD 2.0 dependency label for nominal subject */
    String D_NSUBJ   = "nsubj";
    /** UD 2.0 dependency label for numeric modifier */
    String D_NUMMOD   = "nummod";
    /** UD 2.0 dependency label for object */
    String D_OBJ   = "obj";
    /** UD 2.0 dependency label for oblique */
    String D_OBL   = "obl";
    /** UD 2.0 dependency label for orphan */
    String D_ORPHAN   = "orphan";
    /** UD 2.0 dependency label for parataxis */
    String D_PARATAXIS   = "parataxis";
    /** UD 2.0 dependency label for punctuation */
    String D_PUNCT   = "punct";
    /** UD 2.0 dependency label for overriden disfluency */
    String D_REPARANDUM  = "reparandum";
    /** UD 2.0 dependency label for root */
    String D_ROOT  = "root";
    /** UD 2.0 dependency label for vocative */
    String D_VOCATIVE   = "vocative";
    /** UD 2.0 dependency label for open clausal complement */
    String D_XCOMP  = "xcomp";
}