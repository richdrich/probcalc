package probcalc.identities;

import probcalc.Known;
import probcalc.Prob;
import probcalc.Solve;

import java.util.HashMap;
import java.util.Map;

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
  public Known solve(Prob wanted, int depth, Map<Prob, Known> alreadyFound) {
    if(++depth > 4) return null;

    if(wanted.given.isEmpty()) return null;  // only applicable for conditional probs
    if(wanted.prime) return null; // Not for primes

    Map<Prob, Known> inputTerms = new HashMap<Prob, Known>();

    Prob probYgivenX = wanted.swap();
    Known yGivenX = context.find(probYgivenX, depth, mapUnion(inputTerms, alreadyFound));
    if(yGivenX==null) return null;
	inputTerms.put(probYgivenX, yGivenX);

    Prob probX = wanted.abs();
    Known x = context.find(probX, depth, mapUnion(inputTerms, alreadyFound));
    if(x==null) return null;
	inputTerms.put(probX, x);

    Prob probY = wanted.dep();
    Known y = context.find(probY, depth, mapUnion(inputTerms, alreadyFound));
    if(y==null) return null;
	  inputTerms.put(probY, y);

    Known k = new Known(wanted, (yGivenX.value * x.value) / y.value);
    k.input = false;
    k.byRule = this;

    k.formula = String.format("(%s %s) / %s", probYgivenX, probX, probY);
    k.evaluation = String.format("(%f x %f) / %f", yGivenX.value, x.value, y.value);
    k.inputTerms = inputTerms;

    return k;
  }
}
