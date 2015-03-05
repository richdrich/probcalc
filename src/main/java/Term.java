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

/**
 * // TODO class Javadoc
 *
 * @author richard.parratt
 */
public class Term {
  public Term(String name) {
    this.name = name;
    this.state = true;
  }

  public Term(String name, boolean state) {
    this.name = name;
    this.state = state;
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
}
