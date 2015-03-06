/*
 * Copyright (c) 2015 EDMI NZ
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of EDMI. 
 * ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with EDMI.
 */

import java.util.HashMap;
import java.util.Map;

/**
 * P(AB) = P(A)P(B)
 *
 *
 * @author richard.parratt
 */
public class And extends AbstractRule {
  public And(Solve solve) {
    super(solve);
  }

  @Override
  public Known solve(Prob wanted, int depth) {
    if(++depth > 4) return null;

    if(!wanted.given.isEmpty()) return null;  // only applicable for absolute probs

    double result = 1.0;
    StringBuilder formula = new StringBuilder();
    StringBuilder evaluation = new StringBuilder();
    Map<String, Known> inputTerms = new HashMap<String, Known>();

    for(Term term : wanted.terms) {
      Prob prob = new Prob(term);
      Known factor = context.find(prob, depth);
      if(factor==null) return null;

      result *= factor.value;

      if(formula.length() > 0) {
        formula.append(" ");
        evaluation.append(" x ");
      }
      formula.append(prob.toString());

      evaluation.append(Double.toString(factor.value));

      inputTerms.put(prob.toString(), factor);
    }

    Known k = new Known(wanted, result);
    k.input = false;
    k.byRule = this;

    k.formula = formula.toString();
    k.evaluation = evaluation.toString();
    k.inputTerms = inputTerms;
    return k;
  }
}
