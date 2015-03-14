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
 * Eliminate dependents
 * P(A|B) = P(A|B C) P(C) + P(A|B ~C) P(~C)
 *
 */
public class EliminateDependents extends AbstractRule {
	public EliminateDependents(Solve solve) {
		super(solve);
	}

	@Override
	public Known solve(Prob wanted, int depth, Map<Prob, Known> alreadyFound) {
		if (++depth > 4) return null;
		if (wanted.prime) return null; // Not for primes
		if(wanted.terms.size() != 1) return null;
		if(wanted.given.isEmpty()) return null;	// must be conditional

		// Get direct ancestors of A, e.g. (B C)
		Term wantedTerm = wanted.terms.iterator().next();
		Set<String> ancestors = new HashSet<>(context.depends.directAncestors(wantedTerm.name));

		// remove the dependent B we are looking for => (C)
		ancestors.removeAll(Term.nameSet(wanted.given));
		if(ancestors.isEmpty()) {
			return null;	// No ancestors to search through
		}

		// iterate the combinations x=(C ~C) and sum P(A|B x) P(x)
		Map<Prob, Known> inputTerms = new HashMap<>();
		double sum = 0.0;
		StringBuilder formula = new StringBuilder();
		StringBuilder evaluation = new StringBuilder();
		for(Set<Term> xTerms : Term.combinations(ancestors)) {

			Set<Term> givenTerms = new HashSet<>(xTerms);
			givenTerms.addAll(wanted.given);

			Prob probAGivenBX = new Prob(wanted.terms, givenTerms);
			Known aGivenBX = context.find(probAGivenBX, depth, mapUnion(inputTerms, alreadyFound));
			if(aGivenBX==null) return null;
			inputTerms.put(probAGivenBX, aGivenBX);

			Prob probX = new Prob(xTerms);
			Known x = context.find(probX, depth, mapUnion(inputTerms, alreadyFound));
			if(x==null) return null;
			inputTerms.put(probX, x);

			sum += aGivenBX.value * x.value;

			if(formula.length() > 0) {
				formula.append(" + ");
				evaluation.append(" + ");
			}

			formula.append(probAGivenBX.toString());
			formula.append(probX.toString());

			evaluation.append(Double.toString(aGivenBX.value));
			evaluation.append(" x ");
			evaluation.append(Double.toString(x.value));
		}

		Known k = new Known(wanted, sum);
		k.input = false;
		k.byRule = this;

		k.formula = formula.toString();
		k.evaluation = evaluation.toString();
		k.inputTerms = inputTerms;
		return k;
	}
}
