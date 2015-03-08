package probcalc;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    Set<Prob> seen = new HashSet<>();
    return dumpTerms(this, seen);
  }

  private String dumpTerms(Known topTerm, Set<Prob> seen) {
    if(seen.contains(prob)) return "";
    seen.add(prob);

    if(input) {
      return toString() + "\n";
    }

    StringBuilder res = new StringBuilder();
    for(Map.Entry<Prob, Known> e : inputTerms.entrySet()) {
      res.append(e.getValue().dumpTerms(this, seen));
    }

    res.append(toString() + "\n");

    return res.toString();
  }


	public static Known findInTree(Map<Prob, Known> alreadyFound, Prob wanted) {
		if (alreadyFound.containsKey(wanted)) {
			return alreadyFound.get(wanted);
		}

		for (Known k : alreadyFound.values()) {
			if(k.input) continue;

			Known res = findInTree(k.inputTerms, wanted);
			if (res != null) return res;
		}

		return null;
	}

  public Prob prob;
  public double value;
  public boolean input;
  public Rule byRule;
  public String formula;
  public String evaluation;
  public Map<Prob, Known> inputTerms;

}
