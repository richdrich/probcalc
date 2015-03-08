package probcalc.identities;

import probcalc.Known;
import probcalc.Prob;
import probcalc.Solve;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by richardparratt on 8/03/15.
 */
public class BayesFromPartials  extends AbstractRule {
	public BayesFromPartials(Solve solve) {
		super(solve);
	}

	@Override
	public Known solve(Prob wanted, int depth, Map<Prob, Known> alreadyFound) {
		if(++depth > 4) return null;

		if(wanted.given.isEmpty()) return null;  // only applicable for conditional probs
		if(wanted.prime) return null; // Not for primes

		Map<Prob, Known> inputTerms = new HashMap<>();

		// We need partials for all combinations of wanteds
		List<Prob> wantedProbs = wanted.allTermCombs();
		double sum = 0;
		Known pPrimeWanted = null;
		StringBuilder sumFormula = new StringBuilder();
		StringBuilder sumEvaluation = new StringBuilder();

		for(Prob prob : wantedProbs) {
			boolean isNumerator = prob.equals(wanted);

			Prob partialProb = new Prob(prob);
			partialProb.prime = true;
			Known partial = context.find(partialProb, depth, mapUnion(inputTerms, alreadyFound));
			if(partial==null) return null;

			inputTerms.put(partialProb, partial);
			if(isNumerator) {
				pPrimeWanted = partial;
			}

			sum += partial.value;
			if(sumFormula.length() > 0) {
				sumFormula.append(" + ");
				sumEvaluation.append(" + ");
			}
			sumFormula.append(partialProb.toString());
			sumEvaluation.append(Double.toString(partial.value));
		}

		Known k = new Known(wanted, pPrimeWanted.value / sum);
		k.input = false;
		k.byRule = this;

		k.formula = pPrimeWanted.prob.toString() + "/ (" + sumFormula + ")";
		k.evaluation = String.format("%f / (%s)", pPrimeWanted.value, sumEvaluation);
		k.inputTerms = inputTerms;

		return k;
	}
}
