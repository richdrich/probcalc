package probcalc;

import probcalc.identities.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * // TODO class Javadoc
 *
 * @author richard.parratt
 */
public class Solve {
  public Solve(Map<Prob, Double> inputs) {
    this.inputs = inputs.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> new Known(e.getKey(), e.getValue())));
    makeDepends();
    rules = Arrays.asList(new Rule[]{new Complement(this), new And(this),
            new BayesPartial(this), new BayesFromPartials(this),
            new Bayes(this), new MultiplyAbsolute(this), new Joint(this)});

  }

  public Known find(Prob wanted) {
    Map<Prob, Known> alreadyFound = new HashMap<>();

    return find(wanted, 0, alreadyFound);
  }

	public Known find(Prob wanted, int depth, Map<Prob, Known> alreadyFound) {
		if (inputs.containsKey(wanted)) {
			return inputs.get(wanted);
		}

		Known foundPrevious = Known.findInTree(alreadyFound, wanted);
		if (foundPrevious != null) {
			return foundPrevious;
		}

		Known res = rules.stream().map(r -> r.solve(wanted, depth, alreadyFound)).filter(k -> k != null).max((a, b) -> Integer.compare(b.maxDepth(), a.maxDepth())).orElse(null);
		//alreadyFound.putAll(res.findings);
		return res;
	}

  public Set<String> allTermNames() {
    Set<String> res = new HashSet<>();

    inputs.keySet().stream().forEach(p -> res.addAll(p.allTermNames()));

    return res;
  }

  public void clearInputs() {
    inputs.clear();
    makeDepends();
  }

  public void addInputs(Map<Prob, Double> inputs) {
    inputs.entrySet().stream().forEach(e -> inputs.put(e.getKey(), e.getValue()));
    makeDepends();
  }

  private void makeDepends() {
    depends = new Depends();

    inputs.keySet().stream().forEach(p -> {
      p.given.stream().forEach(given -> {
        p.terms.forEach(term -> depends.add(given.name, term.name));
      });
    });
  }

  public Map<Prob, Known> getInputs() {
    return inputs;
  }

  public List<Rule> rules;
  private Map<Prob, Known> inputs;
  public Depends depends;
}
