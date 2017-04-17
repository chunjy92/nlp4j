# Penn Korean Treebank Conversion Documentation
by Jayeol Chun | Apr 17

###### Keeping the Penn POS morphemes for now, but conversion into UD Pos tags should be trivial.

## Current HeadRules:
```
S	r	VP;ADJP;S;NP;ADVP;.*
VP	r	VP;V.*;ADJP;CV;LV;NP;S;.*
NP	r	NP;S;N.*;VP;ADJP|ADVP.*
DANP	r	DANP|DAN;VP;.*
ADVP	r	ADVP;ADV;-ADV;VP;NP;S;.*
ADJP	r	ADJP;VJ;LV;.*
ADCP	r	ADC;VP;NP|S;.*
ADV	r	VJ;NNC;.*
VX	r	NNX;.*
VV	r	VV;NNC;VJ;.*
VJ	r	VJ;NNC;.*
PRN	r	N.*|NP|VP|S|ADJP|ADVP;.*
CV	r	VV;.*
LV	r	VV;J;.*
INTJ	r	INTJ;IJ;VP;.*
LST	r	NNU;.*
X	r	.*
```

## Current Heuristics for Dependency Label Inference
* Phrase-Level Tags **IJ**, **LST**, **X** do not exist in my data with .parse extension (needs confirmation)

```
def getDependencyLabel(C, P, p):
  C : Child Node
  P : Parent Node
  p : head Node
  c <- head constituent of C
  d <- head dependent of C

  - if head token id is 0:
    root
  - if C has PRN:
    appos
  /*- if d has NNX:
    clf*/
  - if d has DAN:
    det
  - if d has ADC:
    a) if d's word is in [또, 또는, 및, 그리고]
      cc
  - if d has any PUNCT:
    punct
  - if d has any VX:
    aux
  - if d has ADV:
    advmod
  - if C has ADJP or d has any VJ:
    amod
  - if C has function tag -COMP:
    a) if d has last morpheme PAD and the word is either 에게 or 게:
        iobj
  - if C has function tag -SBJ:
    a) if P has function tag -COMP:
        csubj
    b) else:
        nsubj
  - if C has function tag -OBJ:
    obj
  - if C has function tag -ADV:
    a) if d has last morpheme PAD:
        case
    b) else:
        advmod
  - if C has function tag -VOC:
    voc
  - if P has NP:
    a) if C has NNU:
        nummod
    b) if d has last morpheme PCJ:
        conj
    c) else:
        nmod
  - if P has CV:
    compound
  - if d has last morpheme PAD:
    advmod
  - if d only has PCA:
    case
  - else:
    dep
```

## Conversion examples
1. Simplest
```
(S (ADCP (ADC 반면))
   (S (NP-SBJ (NPR+NNX+PAU 피아트+측+은))
      (VP (NP-OBJ (NNC+PCA 논평+을))
          (VV (NNC+XSV+EPF+EFN 거부+하+었+다))))
   (SFN .))
1	반면	_	ADC	_	4	advmod	_	_
2	피아트+측+은	_	NPR+NNX+PAU	_	4	nsubj	_	_
3	논평+을	_	NNC+PCA	_	4	obj	_	_
4	거부+하+었+다	_	NNC+XSV+EPF+EFN	_	0	root	_	_
5	.	_	SFN	_	4	punct	_	_
```

2. Short # 1
```
(S (NP-SBJ (NPN+PAU 그+은))
   (VP (S-COMP (NP-SBJ (NPR+PCA 르노+이))
               (VP (VP (NP-ADV (NNU 3)
                               (NNX+NNX+PAU 월+말+까지))
                       (VP (NP-OBJ (NNC+NNC 인수+제의)
                                   (NNC+PCA 시한+을))
                           (VV+ECS 갖+고)))
                   (VX+EFN+PAD 있+다+고)))
       (VV+EPF+EFN 덧붙이+었+다))
   (SFN .))
1	그+은	_	NPN+PAU	_	9	nsubj	_	_
2	르노+이	_	NPR+PCA	_	7	csubj	_	_
3	3	_	NNU	_	4	nummod	_	_
4	월+말+까지	_	NNX+NNX+PAU	_	7	advmod	_	_
5	인수+제의	_	NNC+NNC	_	6	nmod	_	_
6	시한+을	_	NNC+PCA	_	7	obj	_	_
7	갖+고	_	VV+ECS	_	9	dep	_	_
8	있+다+고	_	VX+EFN+PAD	_	7	aux	_	_
9	덧붙이+었+다	_	VV+EPF+EFN	_	0	root	_	_
10	.	_	SFN	_	9	punct	_	_
```

3. Short # 2
```
(S (NP-SBJ (NPR 보스니아)
           (NNC+PAU 주둔군+은))
   (VP (NP-COMP (S (WHNP-1 (-NONE- *op*))
                   (S (NP-SBJ (-NONE- *T*-1))
                      (VP (NP (NNC 현역)
                              (NNC+CO+EAN 부대+이+은)))))
                (NP (DAN 제)
                    (NNU 10)
                    (NNC+NNC+PAD 산악+사단+에서)))
       (NP-COMP (NP (NPR 텍사스)
                    (NNC+NNC 주+방위군))
                (NP (DAN 제)
                    (NNU 49)
                    (NNC+NNC+PAD 기갑+사단+으로)))
       (VV (NNC+XSV+EFN 교체+되+는다)))
   (SFN .))
1	보스니아	_	NPR	_	2	nmod	_	_
2	주둔군+은	_	NNC+PAU	_	13	nsubj	_	_
3	현역	_	NNC	_	4	nmod	_	_
4	부대+이+은	_	NNC+CO+EAN	_	7	nmod	_	_
5	제	_	DAN	_	7	det	_	_
6	10	_	NNU	_	7	nummod	_	_
7	산악+사단+에서	_	NNC+NNC+PAD	_	13	advmod	_	_
8	텍사스	_	NPR	_	9	nmod	_	_
9	주+방위군	_	NNC+NNC	_	12	nmod	_	_
10	제	_	DAN	_	12	det	_	_
11	49	_	NNU	_	12	nummod	_	_
12	기갑+사단+으로	_	NNC+NNC+PAD	_	13	advmod	_	_
13	교체+되+는다	_	NNC+XSV+EFN	_	0	root	_	_
14	.	_	SFN	_	13	punct	_	_
```

3. Medium # 1
```
(S (NP-SBJ (-NONE- *pro*))
   (VP (NP (S (NP-SBJ (NPR+PAU 우노퍼스트+은))
              (VP (VP (NP-ADV (NP (NNC+NPR+PCJ 남부+유럽+과))
                              (NP (NPR+PAD+PAU 중남미+에서+은)))
                      (VP (NP-OBJ (NP (NPR 우노)
                                      (SSY -)
                                      (NFW+PAN e+이라는))
                                  (NP (NNC+PCA 상표+을)))
                          (VV (-NONE- *?*))))
                  (SCM ,)
                  (VP (NP-ADV (NP (NP (NNC 북부)
                                      (NPR+PCJ 유럽+과))
                                  (NP (NPR 북미))
                                  (ADCP (ADC 및))
                                  (NP (NPR 아시아)))
                              (NP (NNC+PAD+PAU 지역+에서+은)))
                      (VP (NP-OBJ (NP (NPR 퍼스트)
                                      (SSY -)
                                      (NFW+PAN e+이라는))
                                  (NP (NNC+PCA 상표+을)))
                          (VV (NNC+XSV+EAN 사용+하+을))))))
           (NP (NNC+CO+EFN 계획+이+다))))
   (SFN .))
1	우노퍼스트+은	_	NPR+PAU	_	19	nsubj	_	_
2	남부+유럽+과	_	NNC+NPR+PCJ	_	3	conj	_	_
3	중남미+에서+은	_	NPR+PAD+PAU	_	7	advmod	_	_
4	우노	_	NPR	_	6	nmod	_	_
5	-	_	SSY	_	6	punct	_	_
6	e+이라는	_	NFW+PAN	_	7	nmod	_	_
7	상표+을	_	NNC+PCA	_	19	dep	_	_
8	,	_	SCM	_	19	punct	_	_
9	북부	_	NNC	_	10	nmod	_	_
10	유럽+과	_	NPR+PCJ	_	13	conj	_	_
11	북미	_	NPR	_	13	nmod	_	_
12	및	_	ADC	_	13	cc	_	_
13	아시아	_	NPR	_	14	nmod	_	_
14	지역+에서+은	_	NNC+PAD+PAU	_	19	advmod	_	_
15	퍼스트	_	NPR	_	17	nmod	_	_
16	-	_	SSY	_	17	punct	_	_
17	e+이라는	_	NFW+PAN	_	18	nmod	_	_
18	상표+을	_	NNC+PCA	_	19	obj	_	_
19	사용+하+을	_	NNC+XSV+EAN	_	20	nmod	_	_
20	계획+이+다	_	NNC+CO+EFN	_	0	root	_	_
21	.	_	SFN	_	20	punct	_	_
```
  - (7) 상표+을 -> obj
  - root should be..?
  - Must separately handle Coordinations

4. Medium #2
```
(S (NP-ADV (NP (S (NP-SBJ (DAN 이)
                          (NNC+PCA 사건+이))
                  (VP (VV (NNC+XSV+EAN 발생+하+은))))
               (NP (NNX 지)))
           (NP (NNU 2)
               (NNX 주일)
               (NNC 뒤)))
   (S (NP-SBJ (NPR 마틴)
              (NPR 루터)
              (NPR 킹)
              (NNC+PAU 목사+은))
      (VP (S (NP-SBJ (-NONE- *pro*))
             (VP (NP-ADV (NP (NP (NNC+NNC+PAN 연방+법원+의))
                             (NP (NNC 보호)))
                         (NP (NNC 아래)))
                 (NP-ADV (S (WHNP-1 (-NONE- *op*))
                            (S (NP-SBJ (-NONE- *T*-1))
                               (VP (NP (NNC+CO+EAN 수도+이+은)))))
                         (NP (NPR+PAD 워싱턴+에서)))
                 (VP (VP (NP-OBJ (NNU 수)
                                 (NNU+NNX+PCA 백+명+을))
                         (VV+ECS 이끌+고))
                     (VP (VV (NNC+XSV+ECS 행진+하+어))))))
          (VP (NP-OBJ (NP (NNC+NNC+PAN 민권+운동+의))
                      (NP (NNC+PCA 불+을)))
              (VV+EPF+EFN 당기+었+다))))
   (SFN .))
1	이	_	DAN	_	2	det	_	_
2	사건+이	_	NNC+PCA	_	3	nsubj	_	_
3	발생+하+은	_	NNC+XSV+EAN	_	4	nmod	_	_
4	지	_	NNX	_	7	nmod	_	_
5	2	_	NNU	_	7	nummod	_	_
6	주일	_	NNX	_	7	nmod	_	_
7	뒤	_	NNC	_	23	advmod	_	_
8	마틴	_	NPR	_	11	nmod	_	_
9	루터	_	NPR	_	11	nmod	_	_
10	킹	_	NPR	_	11	nmod	_	_
11	목사+은	_	NNC+PAU	_	23	nsubj	_	_
12	연방+법원+의	_	NNC+NNC+PAN	_	13	nmod	_	_
13	보호	_	NNC	_	14	nmod	_	_
14	아래	_	NNC	_	20	advmod	_	_
15	수도+이+은	_	NNC+CO+EAN	_	16	nmod	_	_
16	워싱턴+에서	_	NPR+PAD	_	20	advmod	_	_
17	수	_	NNU	_	18	nummod	_	_
18	백+명+을	_	NNU+NNX+PCA	_	19	obj	_	_
19	이끌+고	_	VV+ECS	_	20	dep	_	_
20	행진+하+어	_	NNC+XSV+ECS	_	23	dep	_	_
21	민권+운동+의	_	NNC+NNC+PAN	_	22	nmod	_	_
22	불+을	_	NNC+PCA	_	23	obj	_	_
23	당기+었+다	_	VV+EPF+EFN	_	0	root	_	_
24	.	_	SFN	_	23	punct	_	_
```
  - (8 ~ 10) -> flat
  - (4) -> ??

5. Medium Long
```
(S (NP-SBJ (NPR 잭슨)
           (NNC+PAU 목사+은))
   (VP (S-COMP (S (NP-SBJ (NP (NPR 클린턴)
                              (NNC+PAN 대통령+의))
                          (NP (NNC 정치)
                              (NNC+PCA 역정+이)))
                  (VP (VP (NP-COMP (NP (NP (NP (NNC+PAN 당시+의))
                                           (NP (NNC+PCJ 시위+과)))
                                       (NP (S (WHNP-1 (-NONE- *op*))
                                              (S (NP-SBJ (-NONE- *T*-1))
                                                 (VP (NP-OBJ (DAN 그)
                                                             (NNC+PCA 뒤+을))
                                                     (VV+EAN 잇+은))))
                                           (NP (NNC+NNC+PAN 민권+운동+의))))
                                   (NP (NNC+PAD 역사+에)))
                          (NP-OBJ (NNC+PCA 바탕+을))
                          (VV+ECS 두+고))
                      (VX+ECS 있+다면서)))
               (S (SLQ ")
                  (S (NP-SBJ (S (WHNP-2 (-NONE- *op*))
                                (S (NP-SBJ (-NONE- *T*-2))
                                   (ADJP (VJ+EAN 새롭+은))))
                             (NP (NP (NNC 남부))
                                 (ADCP (ADC 그리고))
                                 (NP (NPR+PAU 미국+은))))
                     (VP (NP-COMP (NPR+PAD 셀마+에서))
                         (VV (NNC+XSV+EPF+ECS 출발+하+었+으며))))
                  (S (S (NP-SBJ (NPR+PCA 셀마+이))
                        (ADJP (VJ+EPF+ECS 없+었+다면)))
                     (S (NP-SBJ (NPR 클린턴)
                                (NNC+PAU 대통령+도))
                        (VP (ADJP (VJ+EPF+EAN 없+었+을))
                            (VX (NNX 것)))))
                  (SRQ ")
                  (CO+EFN+PAD 이+라+고)))
       (VV+EPF+EFN 말하+었+다))
   (SFN .))
1	잭슨	_	NPR	_	2	nmod	_	_
2	목사+은	_	NNC+PAU	_	32	nsubj	_	_
3	클린턴	_	NPR	_	4	nmod	_	_
4	대통령+의	_	NNC+PAN	_	6	nmod	_	_
5	정치	_	NNC	_	6	nmod	_	_
6	역정+이	_	NNC+PCA	_	15	nsubj	_	_
7	당시+의	_	NNC+PAN	_	8	nmod	_	_
8	시위+과	_	NNC+PCJ	_	12	conj	_	_
9	그	_	DAN	_	10	det	_	_
10	뒤+을	_	NNC+PCA	_	11	obj	_	_
11	잇+은	_	VV+EAN	_	12	nmod	_	_
12	민권+운동+의	_	NNC+NNC+PAN	_	13	nmod	_	_
13	역사+에	_	NNC+PAD	_	15	advmod	_	_
14	바탕+을	_	NNC+PCA	_	15	obj	_	_
15	두+고	_	VV+ECS	_	29	dep	_	_
16	있+다면서	_	VX+ECS	_	15	aux	_	_
17	"	_	SLQ	_	29	punct	_	_
18	새롭+은	_	VJ+EAN	_	21	amod	_	_
19	남부	_	NNC	_	21	nmod	_	_
20	그리고	_	ADC	_	21	cc	_	_
21	미국+은	_	NPR+PAU	_	23	nsubj	_	_
22	셀마+에서	_	NPR+PAD	_	23	advmod	_	_
23	출발+하+었+으며	_	NNC+XSV+EPF+ECS	_	29	dep	_	_
24	셀마+이	_	NPR+PCA	_	25	nsubj	_	_
25	없+었+다면	_	VJ+EPF+ECS	_	29	amod	_	_
26	클린턴	_	NPR	_	27	nmod	_	_
27	대통령+도	_	NNC+PAU	_	29	nsubj	_	_
28	없+었+을	_	VJ+EPF+EAN	_	29	amod	_	_
29	것	_	NNX	_	32	dep	_	_
30	"	_	SRQ	_	29	punct	_	_
31	이+라+고	_	CO+EFN+PAD	_	29	advmod	_	_
32	말하+었+다	_	VV+EPF+EFN	_	0	root	_	_
33	.	_	SFN	_	32	punct	_	_
```
  - (9) -> det
  - (1) and (3) -> flat

6. Long #1
```
(S (NP-SBJ (NNU 한)
           (NNC+NNC 민간+단체)
           (NNC+PAU 관계자+은))
   (VP (S-COMP (S (SLQ ")
                  (S (NP-SBJ (NPR 북한)
                             (NNC 사람)
                             (NNU 7)
                             (NNX+PCA 명+이))
                     (VP (S (NP-SBJ (-NONE- *pro*))
                            (VP (NP-ADV (NPR+PAD 러시아+으로))
                                (VP (VV (NNC+XSV+EPF+ECS 탈출+하+었+다)))))
                         (NP-ADV (DAN 지난)
                                 (NNU 1)
                                 (NNX 월))
                         (VP (NP-COMP (NPR+PAD 북한+으로))
                             (VV (NNC+XSV+ECS 송환+되+으면서)))))
                  (S (NP-COMP-2 (NNC 탈북자)
                                (NNC+PAD 문제+에))
                     (S (NP-SBJ (S (WHNP-1 (-NONE- *op*))
                                   (S (NP-SBJ (-NONE- *T*-1))
                                      (ADJP (VJ+EAN 지나치+은))))
                                (NP (NNC+PCA 관심+이)))
                        (VP (VP (NP-COMP (-NONE- *T*-2))
                                (VV+ECS 쏠리+고))
                            (VX+EFN 있+다))))
                  (SRQ ")
                  (ECS 며))
               (S (SLQ ")
                  (NP-SBJ (S (NP-SBJ (-NONE- *pro*))
                             (VP (S-OBJ (NP-ADV (NP (NPN+PAN 우리+의))
                                                (NP (NNC+PAU 경우+도)))
                                        (S (NP-SBJ (NP (S (WHNP-3 (-NONE- *op*))
                                                          (S (NP-SBJ (-NONE- *T*-3))
                                                             (VP (NP-OBJ (NPR+PCA 남한+을))
                                                                 (VV+EAN 떠나+은))))
                                                       (NP (NNC 사람)))
                                                   (SCM ,)
                                                   (ADCP (ADC 즉))
                                                   (NP (S (WHNP-4 (-NONE- *op*))
                                                          (S (NP-SBJ (-NONE- *pro*))
                                                             (VP (VP (NP-COMP (SLQ ')
                                                                              (NNC 탈남자)
                                                                              (SRQ ')
                                                                              (PAD 이라고))
                                                                     (NP-OBJ (-NONE- *T*-4))
                                                                     (VV+EAN 하+을))
                                                                 (VX (NNX 수)
                                                                     (VJ+EAN 있+는)))))
                                                       (NP (NNC+XSF+PCA 사람+들+이))))
                                           (VP (NP (NNC+CO+ENM+PCA 다수+이+음+을)))))
                                 (VV (NNC+XSV+EAN 상기+하+을))))
                          (NP (NNC+PCA 필요+이)))
                  (ADJP (VJ+EFN 있+다))
                  (SRQ ")
                  (PAD 고)))
       (VV+EPF+EFN 말하+었+다))
   (SFN .))
1	한	_	NNU	_	3	nummod	_	_
2	민간+단체	_	NNC+NNC	_	3	nmod	_	_
3	관계자+은	_	NNC+PAU	_	46	nsubj	_	_
4	"	_	SLQ	_	20	punct	_	_
5	북한	_	NPR	_	8	nmod	_	_
6	사람	_	NNC	_	8	nmod	_	_
7	7	_	NNU	_	8	nummod	_	_
8	명+이	_	NNX+PCA	_	15	nsubj	_	_
9	러시아+으로	_	NPR+PAD	_	10	advmod	_	_
10	탈출+하+었+다	_	NNC+XSV+EPF+ECS	_	15	dep	_	_
11	지난	_	DAN	_	13	det	_	_
12	1	_	NNU	_	13	nummod	_	_
13	월	_	NNX	_	15	advmod	_	_
14	북한+으로	_	NPR+PAD	_	15	advmod	_	_
15	송환+되+으면서	_	NNC+XSV+ECS	_	20	dep	_	_
16	탈북자	_	NNC	_	17	nmod	_	_
17	문제+에	_	NNC+PAD	_	20	advmod	_	_
18	지나치+은	_	VJ+EAN	_	19	amod	_	_
19	관심+이	_	NNC+PCA	_	20	nsubj	_	_
20	쏠리+고	_	VV+ECS	_	43	dep	_	_
21	있+다	_	VX+EFN	_	20	aux	_	_
22	"	_	SRQ	_	20	punct	_	_
23	며	_	ECS	_	20	dep	_	_
24	"	_	SLQ	_	43	punct	_	_
25	우리+의	_	NPN+PAN	_	26	nmod	_	_
26	경우+도	_	NNC+PAU	_	40	advmod	_	_
27	남한+을	_	NPR+PCA	_	28	obj	_	_
28	떠나+은	_	VV+EAN	_	29	nmod	_	_
29	사람	_	NNC	_	39	nmod	_	_
30	,	_	SCM	_	39	punct	_	_
31	즉	_	ADC	_	39	advmod	_	_
32	'	_	SLQ	_	33	punct	_	_
33	탈남자	_	NNC	_	36	dep	_	_
34	'	_	SRQ	_	33	punct	_	_
35	이라고	_	PAD	_	33	nmod	_	_
36	하+을	_	VV+EAN	_	39	nmod	_	_
37	수	_	NNX	_	36	dep	_	_
38	있+는	_	VJ+EAN	_	37	amod	_	_
39	사람+들+이	_	NNC+XSF+PCA	_	40	nsubj	_	_
40	다수+이+음+을	_	NNC+CO+ENM+PCA	_	41	obj	_	_
41	상기+하+을	_	NNC+XSV+EAN	_	42	nmod	_	_
42	필요+이	_	NNC+PCA	_	43	nsubj	_	_
43	있+다	_	VJ+EFN	_	46	amod	_	_
44	"	_	SRQ	_	43	punct	_	_
45	고	_	PAD	_	43	advmod	_	_
46	말하+었+다	_	VV+EPF+EFN	_	0	root	_	_
47	.	_	SFN	_	46	punct	_	_
```

7. Long #2
```
(S (ADCP (ADC 더구나))
   (S (NP-SBJ (NPR+PAU 베트남+은))
      (VP (VP (S (NP-SBJ (-NONE- *pro*))
                 (VP (NP-OBJ (NP (NNC 양국)
                                 (NNC+PAN 업체+의))
                             (NP (NNC+NNC+PCA 과당+경쟁+을)))
                     (VP (ADVP (ADV 교묘히))
                         (VP (VV (NNC+XSV+ECS 이용+하+어))))))
              (S (NP-SBJ (-NONE- *pro*))
                 (VP (NP-OBJ (NP (S (WHNP-1 (-NONE- *op*))
                                    (S (S-SBJ (NP-SBJ (-NONE- *pro*))
                                              (VP (NP-ADV (DAN 다른)
                                                          (NNC+PAD+PAU 나라+에서+은))
                                                  (VP (NP-OBJ (-NONE- *T*-1))
                                                      (VV+ENM 찾아보+기))))
                                       (ADJP (VJ+EAN 힘들+은))))
                                 (NP (NNC 디스카운트)
                                     (NNC 오퍼)
                                     (PRN (SLQ -LRB-)
                                          (NP (S (NP-SBJ (-NONE- *pro*))
                                                 (VP (S (NP-SBJ (-NONE- *pro*))
                                                        (VP (NP-ADV (S (WHNP (-NONE- *op*))
                                                                       (S (NP-SBJ (-NONE- *pro*))
                                                                          (VP (NP-OBJ-LV (NNC+PCA 입찰+을))
                                                                              (LV (VV+EAN 하+을)))))
                                                                    (NP (NNX 때)))
                                                            (NP-ADV (NNC 입찰가)
                                                                    (NNX+PAD 외+에))
                                                            (VP (NP-OBJ (S (NP-SBJ (-NONE- *pro*))
                                                                           (VP (S (NP-SBJ (-NONE- *pro*))
                                                                                  (VP (NP-ADV (S (WHNP (-NONE- *op*))
                                                                                                 (S (ADVP (ADV 만약))
                                                                                                    (S (NP-SBJ (NNC+PCA 낙찰+이))
                                                                                                       (ADJP (VJ+EAN 어렵+을)))))
                                                                                              (NP (NNC+PAU 때+은)))
                                                                                      (NP-ADV (DAN 이)
                                                                                              (NNC+PAU+PAU 가격+까지+이라도))
                                                                                      (VP (NP-OBJ (NNC+PCA 디스카운트+을))
                                                                                          (VV+ECS 하+어서))))
                                                                               (VP (NP-OBJ-LV (NNC+PCA 공사+을))
                                                                                   (LV (VV+EPF+EAN 하+겠+다는)))))
                                                                        (NP (NNC+PCA 액수+을)))
                                                                (VV+ECS 적+어))))
                                                     (VP (NP-OBJ (-NONE- *pro*))
                                                         (VV+EAN 내+는))))
                                              (NP (NNC 일)))
                                          (SRQ -RRB-))
                                     (PAN 이라는)))
                             (NP (S (WHNP-2 (-NONE- *op*))
                                    (S (NP-SBJ (-NONE- *T*-2))
                                       (ADJP (VJ+EAN 새롭+은))))
                                 (NP (NNC+PCA 방식+을))))
                     (VV+ECS 만들+어)))
              (VP (NP-OBJ (NNC+NNC+PCA 출혈+경쟁+을))
                  (VV (NNC+XSV+ECS 유도+하+고))))
          (VX+EFN 있+다)))
   (SFN .))
   1	더구나	_	ADC	_	40	advmod	_	_
2	베트남+은	_	NPR+PAU	_	40	nsubj	_	_
3	양국	_	NNC	_	4	nmod	_	_
4	업체+의	_	NNC+PAN	_	5	nmod	_	_
5	과당+경쟁+을	_	NNC+NNC+PCA	_	7	obj	_	_
6	교묘히	_	ADV	_	7	advmod	_	_
7	이용+하+어	_	NNC+XSV+ECS	_	40	dep	_	_
8	다른	_	DAN	_	9	det	_	_
9	나라+에서+은	_	NNC+PAD+PAU	_	10	advmod	_	_
10	찾아보+기	_	VV+ENM	_	13	nmod	_	_
11	힘들+은	_	VJ+EAN	_	10	amod	_	_
12	디스카운트	_	NNC	_	13	nmod	_	_
13	오퍼	_	NNC	_	37	nmod	_	_
14	(	_	SLQ	_	33	punct	_	_
15	입찰+을	_	NNC+PCA	_	16	obj	_	_
16	하+을	_	VV+EAN	_	17	nmod	_	_
17	때	_	NNX	_	31	advmod	_	_
18	입찰가	_	NNC	_	19	nmod	_	_
19	외+에	_	NNX+PAD	_	31	case	_	_
20	만약	_	ADV	_	22	advmod	_	_
21	낙찰+이	_	NNC+PCA	_	22	nsubj	_	_
22	어렵+을	_	VJ+EAN	_	23	amod	_	_
23	때+은	_	NNC+PAU	_	27	advmod	_	_
24	이	_	DAN	_	25	det	_	_
25	가격+까지+이라도	_	NNC+PAU+PAU	_	27	advmod	_	_
26	디스카운트+을	_	NNC+PCA	_	27	obj	_	_
27	하+어서	_	VV+ECS	_	29	dep	_	_
28	공사+을	_	NNC+PCA	_	29	obj	_	_
29	하+겠+다는	_	VV+EPF+EAN	_	30	nmod	_	_
30	액수+을	_	NNC+PCA	_	31	obj	_	_
31	적+어	_	VV+ECS	_	32	dep	_	_
32	내+는	_	VV+EAN	_	33	nmod	_	_
33	일	_	NNC	_	13	appos	_	_
34	)	_	SRQ	_	33	punct	_	_
35	이라는	_	PAN	_	13	nmod	_	_
36	새롭+은	_	VJ+EAN	_	37	amod	_	_
37	방식+을	_	NNC+PCA	_	38	obj	_	_
38	만들+어	_	VV+ECS	_	40	dep	_	_
39	출혈+경쟁+을	_	NNC+NNC+PCA	_	40	obj	_	_
40	유도+하+고	_	NNC+XSV+ECS	_	0	root	_	_
41	있+다	_	VX+EFN	_	40	aux	_	_
42	.	_	SFN	_	40	punct	_	_
```

5. Too many *dep* (complements) example
```
(S (NP-SBJ (-NONE- *pro*))
   (VP (ADVP (ADV 또))
       (VP (NP-COMP (S (NP-SBJ (S (WHNP-1 (-NONE- *op*))
                                  (S (NP-SBJ (-NONE- *T*-1))
                                     (VP (VP (S (NP-SBJ (-NONE- *pro*))
                                                (VP (S (NP-SBJ (-NONE- *pro*))
                                                       (VP (NP-OBJ (S (WHNP (-NONE- *op*))
                                                                      (S (NP-SBJ (NNC+NNC+PCA 입국+사증+이))
                                                                         (S (NP-SBJ (NNC 필요))
                                                                            (ADJP (VJ+EAN 없+는)))))
                                                                   (NP (NP (NP (NPR 캐나다))
                                                                           (NP (NNX 등)))
                                                                       (NP (NP (NNC 미주))
                                                                           (NP (DAN 다른)
                                                                               (NNC+PCA 나라+을)))))
                                                           (VV (NNC+XSV+ECS 경유+하+어))))
                                                    (VP (NP-COMP (NPR+PAD 미국+으로))
                                                        (VV (NNC+XSV+ECS 밀입국+하+다)))))
                                             (VP (NP-COMP (NPR 미)
                                                          (NNC+NNC+PAD 이민+당국+에))
                                                 (VV (NNC+XSV+ECS 체포+되+거나))))
                                         (VP (VP (S (NP-SBJ (-NONE- *pro*))
                                                    (VP (NP-ADV (NNC 여행자)
                                                                (NNC+PAD 신분+으로))
                                                        (VP (NP-COMP (NPR+PAD 미국+에))
                                                            (VV+EPF+ECS 가+었+다))))
                                                 (VP (VV+ECS 돌아오+지)))
                                             (VX+EAN 않+는)))))
                               (NP (NNC+NNC+XSF+PAU 불법+체류+자+도)))
                       (VP (NP-COMP (NNC 최소)
                                    (NNU 3)
                                    (NNU+XSF+NNX+PAD 만+여+명+에))
                           (VV+EAN 이르+는)))
                    (NP (NNX+PAD 것+으로)))
           (VV (VV+XSV+EPF+EFN 알리+어지+었+다))))
   (SFN .))
1	또	_	ADV	_	28	advmod	_	_
2	입국+사증+이	_	NNC+NNC+PCA	_	4	nsubj	_	_
3	필요	_	NNC	_	4	nsubj	_	_
4	없+는	_	VJ+EAN	_	9	amod	_	_
5	캐나다	_	NPR	_	6	nmod	_	_
6	등	_	NNX	_	9	nmod	_	_
7	미주	_	NNC	_	9	nmod	_	_
8	다른	_	DAN	_	9	det	_	_
9	나라+을	_	NNC+PCA	_	10	obj	_	_
10	경유+하+어	_	NNC+XSV+ECS	_	12	dep	_	_
11	미국+으로	_	NPR+PAD	_	12	advmod	_	_
12	밀입국+하+다	_	NNC+XSV+ECS	_	15	dep	_	_
13	미	_	NPR	_	14	nmod	_	_
14	이민+당국+에	_	NNC+NNC+PAD	_	15	advmod	_	_
15	체포+되+거나	_	NNC+XSV+ECS	_	20	dep	_	_
16	여행자	_	NNC	_	17	nmod	_	_
17	신분+으로	_	NNC+PAD	_	19	advmod	_	_
18	미국+에	_	NPR+PAD	_	19	advmod	_	_
19	가+었+다	_	VV+EPF+ECS	_	20	dep	_	_
20	돌아오+지	_	VV+ECS	_	22	nmod	_	_
21	않+는	_	VX+EAN	_	20	aux	_	_
22	불법+체류+자+도	_	NNC+NNC+XSF+PAU	_	26	nsubj	_	_
23	최소	_	NNC	_	25	nmod	_	_
24	3	_	NNU	_	25	nummod	_	_
25	만+여+명+에	_	NNU+XSF+NNX+PAD	_	26	advmod	_	_
26	이르+는	_	VV+EAN	_	27	nmod	_	_
27	것+으로	_	NNX+PAD	_	28	advmod	_	_
28	알리+어지+었+다	_	VV+XSV+EPF+EFN	_	0	root	_	_
29	.	_	SFN	_	28	punct	_	_
```

## Timeline:
* Initial Final Conversion by next week (Apr 21)
* Final Version in two weeks (Apr 28)
* KAIST in three weeks (May 5)

## Questions:
* Keep the morphemes in the final conversion?
* How to handle those *dep*? mostly clausal comlements
* *clf* ??? *dislocated*? *reparandum*?
* Direction for Compound verbs (CV): sometimes left?
* Difference between *advmod*, *advcl*, *acl* in Korean
* When would *cop* be ever used?
* *ccomp* and *xcomp* ?? subject vs non-subject?
* passives?? (ex. det:pass)
* *case* vs *mark*
