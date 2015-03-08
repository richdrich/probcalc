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
    this.terms = Collections.singletonList(term);
    this.given = Collections.emptyList();
  }

  public Prob(Term term,  List<Term> given) {
    this.terms = Collections.singletonList(term);
    this.given = given;
  }

  public Prob(Term term, Term given) {
    this.terms = Collections.singletonList(term);
    this.given = Collections.singletonList(given);
  }

  public Prob(List<Term> terms, Term given) {
    this.terms = terms;
    this.given = Collections.singletonList(given);
  }

  public Prob(List<Term> terms) {
    this.terms = terms;
    this.given = Collections.emptyList();
  }

  public Prob(List<Term> terms, List<Term> given) {
    this.terms = terms;
    this.given = given;
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
    List<String> termNames = terms.stream().map(w -> w.name).collect(Collectors.toList());

    List<Prob> res = new ArrayList<>();
    for(int n=0; n<(1 << termNames.size()); n++) {
      List<Term> condTerms = new ArrayList<>();
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

  private static List<Term> extractTerms(String group) {

    String[] termStrings = group.trim().replace("|", "").replace("~", " ~").trim().split("\\s");
    return Arrays.<String>asList(termStrings).stream().filter(s -> !s.trim().isEmpty()).map(s -> {
      s = s.trim();

      if(s.startsWith("~")) {
        return new Term(s.substring(1), false);
      }
      else {
        return new Term(s);
      }
    }).collect(Collectors.toList());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Prob prob = (Prob) o;

    if (prime != prob.prime) return false;
    if (given != null ? !given.equals(prob.given) : prob.given != null) return false;
    if (terms != null ? !terms.equals(prob.terms) : prob.terms != null) return false;

    return true;
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

    for(Term term : terms) {
      if(!res.toString().endsWith("(")) {
        res.append(" ");
      }
      res.append(term.toString());
    }
    if(!given.isEmpty()) {
      res.append("|");
      for(Term term : given) {
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
    Set<String> res = new HashSet<>();
    for(Term term : terms) {
      res.add(term.name);
    }
    for(Term term : given) {
      res.add(term.name);
    }
    return res;
  }

  public List<Term> terms;
  public List<Term> given;
  public boolean prime;

}
