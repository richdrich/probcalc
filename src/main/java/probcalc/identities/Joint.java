package probcalc.identities;

import probcalc.Known;
import probcalc.Prob;
import probcalc.Solve;
import probcalc.Term;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by richardparratt on 7/03/15.
 */
public class Joint extends AbstractRule {
	public Joint(Solve solve) {
		super(solve);
	}


	@Override
	public Known solve(Prob wanted, int depth, Map<Prob, Known> alreadyFound) {
		if(++depth > 4) return null;
		if(wanted.prime) return null; // Not for primes

		if(!wanted.given.isEmpty()) return null;  // only applicable for absolute probs

		// Find dependent term
		Set<Term> dependentTerms = wanted.terms.stream().filter(t -> {
			Set<String> others = new HashSet<String>(wanted.terms.stream().map(wt -> wt.name).filter(wtn -> !wtn.equals(t.name)).collect(Collectors.toSet()));
			return context.depends.anyDependent(others, t.name);
		}).collect(Collectors.toSet());

		if(dependentTerms.isEmpty() || dependentTerms.size() > 1) return null;	// expect only one dependent term
		Term dependentTerm = dependentTerms.iterator().next();

		// We have P(A B C..) where we (maybe) know P(A | B C..) and P(B C ..)

		// Try and get those terms
		List<Term> fromTerms = wanted.terms.stream().filter(t -> !t.name.equals(dependentTerm.name)).collect(Collectors.toList());
		Map<Prob, Known> inputTerms = new HashMap<Prob, Known>();

		Prob toProb = new Prob(dependentTerm, fromTerms);
		Known to = context.find(toProb, depth, mapUnion(inputTerms, alreadyFound));
		if(to==null) {
			return null;
		}
		inputTerms.put(toProb, to);

		Prob fromProb = new Prob(fromTerms);
		Known from = context.find(fromProb, depth, mapUnion(inputTerms, alreadyFound));
		if(from==null) {
			return null;
		}
		inputTerms.put(fromProb, from);

		// Multiply to get joint prob
		double result = to.value * from.value;
		Known k = new Known(wanted, result);
		k.input = false;
		k.byRule = this;

		k.formula = toProb.toString() + " " + fromProb.toString();
		k.evaluation = String.format("%f x %f", to.value, from.value);
		k.inputTerms = inputTerms;

		return k;
	}
}
