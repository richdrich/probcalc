package probcalc.identities;

import probcalc.Known;
import probcalc.Prob;
import probcalc.Solve;
import probcalc.Term;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Using conditional independence, find
 * P(A|B) from P(A|C) * P(C|B) + P(A|~C) * P(~C|B)
 * where C is a common ancestor
 */
public class ConditionalIndependence extends AbstractRule {
	public ConditionalIndependence(Solve solve) {
		super(solve);
	}

	@Override
	public Known solve(Prob wanted, int depth, Map<Prob, Known> alreadyFound) {
		if (++depth > 4) return null;
		if (wanted.prime) return null; // Not for primes

		// TODO - multiple terms
		if(wanted.terms.size() != 1) return null;
		if(wanted.given.size() != 1) return null;

		final Term givenTerm = wanted.given.iterator().next();
		final Term wantedTerm = wanted.terms.iterator().next();
		Set<String> commons = context.depends.nearestCommonAncestors(wantedTerm.name, givenTerm.name);

		for(String common : commons) {
			Map<Prob, Known> inputTerms = new HashMap<>();

			Prob probAgivenC = new Prob(wanted.terms, new Term(common));
			Known aGivenC = context.find(probAgivenC, depth, mapUnion(inputTerms, alreadyFound));
			if(aGivenC==null) continue;
			inputTerms.put(probAgivenC, aGivenC);

			Prob probCgivenB = new Prob(new Term(common), wanted.given);
			Known cGivenB = context.find(probCgivenB, depth, mapUnion(inputTerms, alreadyFound));
			if(cGivenB==null) continue;
			inputTerms.put(probCgivenB, cGivenB);

			Prob probAgivenNotC = new Prob(wanted.terms, new Term(common, false));
			Known aGivenNotC = context.find(probAgivenNotC, depth, mapUnion(inputTerms, alreadyFound));
			if(aGivenNotC==null) continue;
			inputTerms.put(probAgivenNotC, aGivenNotC);

			Prob probNotCgivenB = new Prob(new Term(common, false), wanted.given);
			Known notCgivenB = context.find(probNotCgivenB, depth, mapUnion(inputTerms, alreadyFound));
			if(notCgivenB==null) continue;
			inputTerms.put(probNotCgivenB, notCgivenB);


			Known k = new Known(wanted, aGivenC.value * cGivenB.value + aGivenNotC.value * notCgivenB.value);
			k.input = false;
			k.byRule = this;

			k.formula = String.format("(%s %s) + (%s %s)", probAgivenC, probCgivenB, probAgivenNotC, probNotCgivenB);
			k.evaluation = String.format("(%f x %f) + (%f x %f)", aGivenC.value, cGivenB.value, aGivenNotC.value, notCgivenB.value);
			k.inputTerms = inputTerms;
			return k;

		}

		return null;
	}

}
