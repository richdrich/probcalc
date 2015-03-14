package probcalc.identities;

import probcalc.Known;
import probcalc.Prob;
import probcalc.Solve;
import probcalc.Term;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Absolute independence: P(A|B) where A & B are independent = P(A)
 */
public class AbsoluteIndependence extends AbstractRule {

 	public AbsoluteIndependence(Solve solve) {
		super(solve);
	}

	@Override
	public Known solve(Prob wanted, int depth, Map<Prob, Known> alreadyFound) {
		if (++depth > 4) return null;
		if (wanted.prime) return null; // Not for primes
		if(wanted.given.isEmpty()) return null;

		if(anyNotIndependent(wanted.terms)) return null;	// One term not independent
		if(anyNotIndependent(wanted.given)) return null;	// One term not independent

		Prob probIndep = new Prob(wanted.terms);
		Known k = context.find(probIndep, depth+1, alreadyFound);
		if(k==null) return null;

		k.input = false;

		k.byRule = this;
		k.formula = probIndep.toString();
		k.evaluation = Double.toString(k.value);
		k.inputTerms = Collections.emptyMap();	// Avoid loop
		return k;
	}

	private boolean anyNotIndependent(Set<Term> terms) {
		return terms.stream().anyMatch(t -> !context.depends.isIndependent(t.name));
	}
}
