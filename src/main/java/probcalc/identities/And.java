package probcalc.identities;

import probcalc.Known;
import probcalc.Prob;
import probcalc.Solve;
import probcalc.Term;

import java.util.HashMap;
import java.util.Map;

/**
 * P(AB) = P(A)P(B)
 * where A and B are independent
 *
 *
 * @author richard.parratt
 */
public class And extends AbstractRule {
  public And(Solve solve) {
    super(solve);
  }

  @Override
  public Known solve(Prob wanted, int depth, Map<Prob, Known> alreadyFound) {
    if(++depth > 4) return null;

//    if(!wanted.given.isEmpty()) return null;  // only applicable for absolute probs
    if(wanted.prime) return null; // Not for primes

    if(!context.depends.allIndependent(Term.nameSet(wanted.terms))) return null; // must all be independent of each other

    double result = 1.0;
    StringBuilder formula = new StringBuilder();
    StringBuilder evaluation = new StringBuilder();
    Map<Prob, Known> inputTerms = new HashMap<Prob, Known>();

    for(Term term : wanted.terms) {

      Prob prob = new Prob(term, wanted.given);
      Known factor = context.find(prob, depth, mapUnion(inputTerms, alreadyFound));
      if(factor==null) return null;

      result *= factor.value;

      if(formula.length() > 0) {
        formula.append(" ");
        evaluation.append(" x ");
      }
      formula.append(prob.toString());

      evaluation.append(Double.toString(factor.value));

      inputTerms.put(prob, factor);
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
