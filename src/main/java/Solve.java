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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * // TODO class Javadoc
 *
 * @author richard.parratt
 */
public class Solve {
  public Solve(Map<Prob, Double> inputs) {
    this.inputs = inputs.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> new Known(e.getValue())));
    this.rules = null;
  }

  public Known find(Prob wanted, int depth) {
    if(inputs.containsKey(wanted)) {
      return inputs.get(wanted);
    }

    return rules.stream().map(r -> r.solve(wanted, depth)).filter(k -> k != null).max((a,b) -> Integer.compare(b.maxDepth(), a.maxDepth())).orElse(null);
  }

  public Map<Prob, Known> inputs;
  public List<Rule> rules;
}
