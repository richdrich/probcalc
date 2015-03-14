package probcalc;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A probability term which can be true or false according to state
 *
 * @author richard.parratt
 */
public class Term implements Comparable<Term> {
  public Term(String name) {
    this.name = name;
    this.state = true;
  }

  public Term(String name, boolean state) {
    this.name = name;
    this.state = state;
  }

  public static Set<Term> nthTermCombination(Collection<String> termNames, int n) {
    Set<Term> givenTerms = new TreeSet<>();
    List<String> termNameList = new ArrayList<>(termNames);
    for(int pv=0; pv<termNameList.size(); pv++) {
	  boolean bitState = ((n >> pv) & 1) == 1;
	  Term term = new Term(termNameList.get(pv), bitState);
	  givenTerms.add(term);
	}
    return givenTerms;
  }

  public Term inverse() {
    return new Term(name, !state);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Term term = (Term) o;

    if (state != term.state) return false;
    if (name != null ? !name.equals(term.name) : term.name != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (state ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    return (state ? "" : "~") + name;
  }

  public String name;
  public boolean state;

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer
   */
  @Override
  public int compareTo(Term o) {
    return name.equals(o.name) ? new Boolean(state).compareTo(o.state) : name.compareTo(o.name);
  }

  public static Set<String> nameSet(Collection<Term> terms) {
    return terms.stream().map(t -> t.name).collect(Collectors.toSet());
  }

  public static Set<Set<Term>> combinations(Collection<String> termNames) {
    int numValues = 1 << termNames.size();
    Set<Set<Term>> res = new HashSet<>(numValues);

    for (int n = 0; n < numValues; n++) {
      res.add(Term.nthTermCombination(termNames, n));
    }
    return res;
  }
}
