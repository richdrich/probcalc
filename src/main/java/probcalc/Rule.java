package probcalc;

import java.util.Map;

/**
 * // TODO class Javadoc
 *
 * @author richard.parratt
 */
public interface Rule {

  Known solve(Prob wanted, int depth, Map<Prob, Known> alreadyFound);
}
