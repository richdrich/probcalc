package probcalc.identities;

import probcalc.Known;
import probcalc.Prob;
import probcalc.Solve;
import probcalc.Term;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by richardparratt on 8/03/15.
 */
public class BayesPartial extends AbstractRule {
	public BayesPartial(Solve solve) {
		super(solve);
	}


	@Override
	public Known solve(Prob wanted, int depth, Map<Prob, Known> alreadyFound) {
		if(wanted.given.isEmpty()) return null;  // only applicable for conditional probs
		if(!wanted.prime) return null; // Only for primes

		Map<Prob, Known> inputTerms = new HashMap<Prob, Known>();

		Prob probX = wanted.abs();
		Known x = context.find(probX, depth, mapUnion(inputTerms, alreadyFound));
		if(x==null) return null;
		inputTerms.put(probX, x);

		// Now multiply all the givens
		double product = x.value;
		StringBuilder formula = new StringBuilder();
		StringBuilder evaluation = new StringBuilder();
		for(Term givenTerm : wanted.given) {

			Prob probGivenX = new Prob(givenTerm, wanted.terms);
			Known givenX = context.find(probGivenX, depth, mapUnion(inputTerms, alreadyFound));
			if (givenX == null) return null;

			product *= givenX.value;
			inputTerms.put(probGivenX, givenX);
			if(formula.length() > 0) {
				formula.append(" ");
				evaluation.append(" x ");
			}
			formula.append(probGivenX.toString());
			evaluation.append(Double.toString(givenX.value));
		}


		Known k = new Known(wanted, product);
		k.input = false;
		k.byRule = this;

		k.formula = probX + " " + formula.toString();
		k.evaluation = String.format("%f x %s", x.value, evaluation);
		k.inputTerms = inputTerms;

		return k;
	}
}
