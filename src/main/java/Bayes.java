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

import java.util.Collections;
import java.util.HashMap;

/**
 * // TODO class Javadoc
 *
 * @author richard.parratt
 */
public class Bayes extends AbstractRule {
  public Bayes(Solve solve) {
    super(solve);
  }

  @Override
  public Known solve(Prob wanted, int depth) {
    if(++depth > 4) return null;

    if(wanted.given.isEmpty()) return null;  // only applicable for conditional probs

    Prob probYgivenX = wanted.swap();
    Known yGivenX = context.find(probYgivenX, depth);
    if(yGivenX==null) return null;

    Prob probX = wanted.abs();
    Known x = context.find(probX, depth);
    if(x==null) return null;

    Prob probY = wanted.dep();
    Known y = context.find(probY, depth);
    if(y==null) return null;

    Known k = new Known(wanted, (yGivenX.value * x.value) / y.value);
    k.input = false;
    k.byRule = this;

    k.formula = String.format("(%s %s) / %s", probYgivenX, probX, probY);
    k.evaluation = String.format("(%f x %f) / %f", yGivenX.value, x.value, y.value);
    k.inputTerms = new HashMap<String, Known>(){{
      put(probYgivenX.toString(), yGivenX);
      put(probX.toString(), x);
      put(probY.toString(), y);
    }};

    return k;
  }
}
