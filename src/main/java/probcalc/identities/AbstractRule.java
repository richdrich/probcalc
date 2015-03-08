package probcalc.identities;

import probcalc.Known;
import probcalc.Prob;
import probcalc.Rule;
import probcalc.Solve;

import java.util.HashMap;
import java.util.Map;

/**
 * // TODO class Javadoc
 *
 * @author richard.parratt
 */
public abstract class AbstractRule implements Rule {

  public AbstractRule(Solve context) {
    this.context = context;
  }


  protected Map<Prob, Known> mapUnion(Map<Prob, Known> a, Map<Prob, Known> b) {
    Map<Prob, Known> res = new HashMap<>();
    res.putAll(a);
    res.putAll(b);
    return res;
  }

  protected final Solve context;
}
