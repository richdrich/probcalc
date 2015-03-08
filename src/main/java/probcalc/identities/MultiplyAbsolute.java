package probcalc.identities;

import com.google.common.collect.Sets;
import probcalc.Known;
import probcalc.Prob;
import probcalc.Solve;
import probcalc.Term;

import java.util.*;

/**
 * // TODO class Javadoc
 *
 * @author richard.parratt
 */
public class MultiplyAbsolute extends AbstractRule {
  public MultiplyAbsolute(Solve solve) {
    super(solve);
  }


  @Override
  public Known solve(Prob wanted, int depth, Map<Prob, Known> alreadyFound) {
    if(++depth > 4) return null;

    if(!wanted.given.isEmpty()) return null;  // only applicable for absolute probs
    if(wanted.prime) return null; // Not for primes

    // Find a set of conditional properties for term
    // with the same givens

    List<Term> wantedTerms = wanted.terms;

    // First we find all the candidates
    Set<String> allGivenNames = context.allTermNames();
    wanted.terms.stream().forEach(t -> allGivenNames.remove(t.name));

    // Now we need all the combinations (e.g: A AB B / A B C AB BC ABC
    Set<Set<String>> givenCombs = Sets.powerSet(allGivenNames);

    // Iterate the power set
    for(Set<String> givenComb : givenCombs) {
      if(givenComb.isEmpty()) continue;   // ignore the empty set

      // Now we are interested in +/- for each value
      List<Known[]> factors = new ArrayList<>();
      List<String> termNames = new ArrayList(givenComb);
		Map<Prob, Known> inputTerms = new HashMap<Prob, Known>();

      int numBits = givenComb.size();
      int numValues = 1 << numBits;
      boolean incomplete = false;
      for(int n=0; n<numValues; n++) {

        List<Term> givenTerms = new ArrayList<>();
        for(int pv=0; pv<numBits; pv++) {
          boolean bitState = ((n >> pv) & 1) == 1;
          Term term = new Term(termNames.get(pv), bitState);
          givenTerms.add(term);
        }

        Prob condProb = new Prob(wantedTerms, givenTerms);
        Known cond = context.find(condProb, depth, mapUnion(inputTerms, alreadyFound));
        if(cond==null) {
          incomplete = true;
          break;
        }
        inputTerms.put(condProb, cond);

        Prob absProb = new Prob(givenTerms);
        Known abs = context.find(absProb, depth, mapUnion(inputTerms, alreadyFound));
        if(abs==null) {
          incomplete = true;
          break;
        }
        inputTerms.put(absProb, abs);

        factors.add(new Known[] {abs, cond});
      }

      if(!incomplete) {
        // Multiply and sum
        double result = 0.0;
        StringBuilder formula = new StringBuilder();
        StringBuilder evaluation = new StringBuilder();

        for(Known[] factor : factors) {
          result += factor[0].value * factor[1].value;

          if(formula.length() > 0) {
            formula.append(" + ");
            evaluation.append(" + ");
          }
          String absProbName = factor[0].prob.toString();
          formula.append(absProbName);
          String condProbName = factor[1].prob.toString();
          formula.append(condProbName);

          evaluation.append(Double.toString(factor[0].value));
          evaluation.append(" x ");
          evaluation.append(Double.toString(factor[1].value));
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

    return null;
  }
}
