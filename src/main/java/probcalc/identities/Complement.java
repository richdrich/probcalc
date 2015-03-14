package probcalc.identities;

import probcalc.Known;
import probcalc.Prob;
import probcalc.Solve;

import java.util.Collections;
import java.util.Map;

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
  public Known solve(Prob wanted, int depth, Map<Prob, Known> alreadyFound) {
    if(++depth > 4) return null;
    if(wanted.prime) return null; // Not for primes

    if(wanted.terms.size()==1) {
      Prob inverseWanted = new Prob(wanted.terms.iterator().next().inverse(), wanted.given);
      Known inverse = context.find(inverseWanted, depth, alreadyFound);
      if(inverse!=null) {
        Known result = new Known(wanted, 1.0 - inverse.value);
        result.input = false;

        result.byRule = this;
        result.formula = String.format("1 - %s", inverseWanted);
        result.evaluation = String.format("1 - %f", inverse.value);
        result.inputTerms = Collections.singletonMap(inverseWanted, inverse);
        return result;
      }
    }

    // TODO: inverse of more complex probs
    return null;
  }


}
