package probcalc.identities;

import probcalc.Known;
import probcalc.Prob;
import probcalc.Solve;
import probcalc.Term;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Chain rule (inverse):
 * P(A | B C) = P(A B C) / P(B C)
 */
public class InverseChain extends AbstractRule {

	public InverseChain(Solve solve) {
		super(solve);
	}


	@Override
	public Known solve(Prob wanted, int depth, Map<Prob, Known> alreadyFound) {
		if (++depth > 4) return null;
		if(wanted.given.isEmpty()) return null;	// conditionals only

		Map<Prob, Known> inputTerms = new HashMap<Prob, Known>();

		// P(A B C) = prob of all terms
		Set<Term> allTermSet = new HashSet<>(wanted.terms);
		allTermSet.addAll(wanted.given);
		Prob probAllTerms = new Prob(allTermSet);
		Known allTerms = context.find(probAllTerms, depth, mapUnion(inputTerms, alreadyFound));
		inputTerms.put(probAllTerms, allTerms);
		if(allTerms==null) return null;

		Prob probGiven = new Prob(wanted.given);
		Known given = context.find(probGiven, depth, mapUnion(inputTerms, alreadyFound));
		inputTerms.put(probGiven, given);
		if(given==null) return null;

		Known k = new Known(wanted, allTerms.value / given.value);
		k.input = false;
		k.byRule = this;

		k.formula = probAllTerms.toString() + " / " + probGiven.toString();
		k.evaluation = String.format("%f / %f", allTerms.value, given.value);
		k.inputTerms = inputTerms;
		return k;
	}
}
