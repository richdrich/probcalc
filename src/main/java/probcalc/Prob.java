package probcalc;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * // TODO class Javadoc
 *
 * @author richard.parratt
 */
public class Prob {
  public Prob(Term term) {
    this.terms = new HashSet<>(Arrays.asList(new Term[]{term}));
    this.given = Collections.emptySet();
  }

  public Prob(Term term,  Collection<Term> given) {
    this.terms = new HashSet<>(Arrays.asList(new Term[]{term}));
    this.given = new HashSet<>(given);
  }

  public Prob(Term term, Term given) {
    this.terms = new HashSet<>(Arrays.asList(new Term[]{term}));
    this.given = new HashSet<>(Arrays.asList(new Term[]{given}));
  }

  public Prob(Collection<Term> terms, Term given) {
    this.terms = new HashSet<>(terms);
    this.given = new HashSet<>(Arrays.asList(new Term[]{given}));
  }

  public Prob(Collection<Term> terms) {
    this.terms = new HashSet<>(terms);
    this.given = Collections.emptySet();
  }

  public Prob(Collection<Term> terms, Collection<Term> given) {
    this.terms = new HashSet<>(terms);
    this.given = new HashSet<>(given);
  }

  public Prob(Prob prob) {
    this.terms = prob.terms;
    this.given = prob.given;
    this.prime = prob.prime;
  }

  public Prob swap() {
    return new Prob(given, terms);
  }

  public Prob abs() {
    return new Prob(terms);
  }

  public Prob dep() {
    return new Prob(given);
  }

  public List<Prob> allTermCombs() {
    List<String> termNames = new HashSet<>(terms).stream().map(w -> w.name).collect(Collectors.toList());

    List<Prob> res = new ArrayList<>();
    for(int n=0; n<(1 << termNames.size()); n++) {
      Set<Term> condTerms = new HashSet<>();
      for (int pv = 0; pv < termNames.size(); pv++) {
        boolean bitState = ((n >> pv) & 1) == 1;
        Term term = new Term(termNames.get(pv), bitState);
        condTerms.add(term);
      }

      res.add(new Prob(condTerms, given));
    }

    return res;
  }

  public static Prob parse(String probText) {
    Pattern pattern = Pattern.compile("P(')?\\s*\\(\\s*([~\\w\\s]+)(\\|[~\\w\\s]+)?\\)");
    Matcher matcher = pattern.matcher(probText);


    if(!matcher.matches() || matcher.group(2)==null) {
      throw new RuntimeException("Prob " + probText + " is not recognized");
    }

    Prob res = new Prob(extractTerms(matcher.group(2)));
    res.prime = matcher.group(1) != null;

    if(matcher.group(3) != null) {
      res.given = extractTerms(matcher.group(3));
    }

    return res;
  }

  private static Set<Term> extractTerms(String group) {

    String[] termStrings = group.trim().replace("|", "").replace("~", " ~").trim().split("\\s");
    return Arrays.asList(termStrings).stream().filter(s -> !s.trim().isEmpty()).map(s -> {
      s = s.trim();

      if(s.startsWith("~")) {
        return new Term(s.substring(1), false);
      }
      else {
        return new Term(s);
      }
    }).collect(Collectors.toSet());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Prob prob = (Prob) o;

    return prime == prob.prime && !(given != null ? !given.equals(prob.given) : prob.given != null)
            && !(terms != null ? !terms.equals(prob.terms) : prob.terms != null);

  }

  @Override
  public int hashCode() {
    int result = terms != null ? terms.hashCode() : 0;
    result = 31 * result + (given != null ? given.hashCode() : 0);
    result = 31 * result + (prime ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    StringBuilder res = new StringBuilder("P");

    if(prime) {
      res.append("'");
    }

    res.append("(");

    for(Term term : terms.stream().sorted().collect(Collectors.toList())) {
      if(!res.toString().endsWith("(")) {
        res.append(" ");
      }
      res.append(term.toString());
    }
    if(!given.isEmpty()) {
      res.append("|");
      for(Term term : given.stream().sorted().collect(Collectors.toList())) {
        if(!res.toString().endsWith("|")) {
          res.append(" ");
        }
        res.append(term.toString());
      }
    }
    res.append(")");
    return res.toString();
  }

  public Set<String> allTermNames() {
    Set<String> res = terms.stream().map(term -> term.name).collect(Collectors.toSet());
    for(Term term : given) {
      res.add(term.name);
    }
    return res;
  }

  public Set<Term> terms;
  public Set<Term> given;
  public boolean prime;

}
