/*
 * Copyright (c) 2015 EDMI NZ
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of EDMI. 
 * ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with EDMI.
 */

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

  public Prob swap() {
    return new Prob(given, terms);
  }

  public Prob abs() {
    return new Prob(terms);
  }

  public Prob dep() {
    return new Prob(given);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Prob prob = (Prob) o;

    if (given != null ? !given.equals(prob.given) : prob.given != null) return false;
    if (terms != null ? !terms.equals(prob.terms) : prob.terms != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = terms != null ? terms.hashCode() : 0;
    result = 31 * result + (given != null ? given.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    StringBuilder res = new StringBuilder("P(");
    for(Term term : terms) {
      res.append(term.toString());
    }
    if(!given.isEmpty()) {
      res.append("|");
      for(Term term : given) {
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


}
