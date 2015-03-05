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

/**
 * // TODO class Javadoc
 *
 * @author richard.parratt
 */
public class Complement extends AbstractRule {

  public Complement(Solve solve) {
    super(solve);
  }

  @Override
  public Known solve(Prob wanted, int depth) {
    if(++depth > 4) return null;

    if(wanted.terms.size()==1) {
      Prob inverseWanted = new Prob(wanted.terms.get(0).inverse(), wanted.given);
      Known inverse = context.find(inverseWanted, depth);
      if(inverse!=null) {
        Known result = new Known(1.0 - inverse.value);
        result.input = false;

        result.byRule = this;
        result.formula = String.format("1 - %s", inverseWanted);
        result.evaluation = String.format("1 - %f", inverse.value);
        result.inputTerms = Collections.singletonMap(inverseWanted.toString(), inverse);
        return result;
      }
    }

    // TODO: inverse of more complex probs
    return null;
  }


}
