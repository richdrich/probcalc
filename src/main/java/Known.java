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

import java.util.Map;

/**
 * // TODO class Javadoc
 *
 * @author richard.parratt
 */
public class Known {
  public Known(Prob prob, double v) {
    this.prob = prob;
    value = v;
    input = true;
    byRule = null;
  }

  public int maxDepth() {
    if(input) return 0;

    return inputTerms.entrySet().stream().max((a,b) ->
            Integer.compare(b.getValue().maxDepth(), a.getValue().maxDepth())).orElseThrow(RuntimeException::new).getValue().maxDepth() + 1;
  }

  @Override
  public String toString() {
    if(input) {
      return String.format("%s: [%f]", prob, value);
    }

    return prob.toString() + ": {" + byRule.getClass().getSimpleName() + "} : " + formula + " => " + evaluation + " => " + Double.toString(value);
  }

  public String dump() {
    return dumpTerms(this);
  }

  private String dumpTerms(Known topTerm) {
    if(input) {
      return toString() + "\n";
    }

    StringBuilder res = new StringBuilder();
    for(Map.Entry<String, Known> e : inputTerms.entrySet()) {
      res.append(e.getValue().dumpTerms(this));
    }

    res.append(toString() + "\n");

    return res.toString();
  }

  public Prob prob;
  public double value;
  public boolean input;
  public Rule byRule;
  public String formula;
  public String evaluation;
  public Map<String, Known> inputTerms;

}
