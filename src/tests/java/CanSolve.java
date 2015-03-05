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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.data.Offset.offset;

/**
 * // TODO class Javadoc
 *
 * @author richard.parratt
 */
public class CanSolve {

  public Solve solve;

  @Before
  public void setup() {
    Map<Prob, Double> inputs = new HashMap<>();
    inputs.put(new Prob(new Term("A")), 0.75);
    inputs.put(new Prob(new Term("B")), 0.05);
    inputs.put(new Prob(new Term("B"), new Term("A")), 0.005);
    inputs.put(new Prob(new Term("B"), new Term("A", false)), 0.02);

    solve = new Solve(inputs);
    solve.rules = Arrays.asList(new Rule[] {new Complement(solve), new Bayes(solve)});
  }

  @Test
  public void definedInput() {
    Known a = solve.find(new Prob(new Term("A")), 0);
    assertThat(a.input).isTrue();
    assertThat(a.value).isEqualTo(0.75);
  }

  @Test
  public void complement() {
    Known notA = solve.find(new Prob(new Term("A", false)), 0);
    assertThat(notA.input).isFalse();
    assertThat(notA.byRule.getClass()).isEqualTo((Class)Complement.class);
    assertThat(notA.value).isEqualTo(0.25);
  }

  @Test
  public void bayes() {
    Known aGivenB = solve.find(new Prob(new Term("A"), new Term("B")), 0);
    assertThat(aGivenB).isNotNull();
    assertThat(aGivenB.input).isFalse();
    assertThat(aGivenB.byRule.getClass()).isEqualTo((Class)Bayes.class);
    assertThat(aGivenB.value).isEqualTo((0.005 * 0.75) / 0.05, offset(0.001)); // P(B|A) P(A) / P(B)

    System.out.printf("bayes:\n%s\n", aGivenB.dump());
  }

  @Test
  public void bayesInverse() {
    Known aGivenNotB = solve.find(new Prob(new Term("A"), new Term("B", false)), 0);
    assertThat(aGivenNotB).isNotNull();
    assertThat(aGivenNotB.input).isFalse();
    assertThat(aGivenNotB.byRule.getClass()).isEqualTo((Class) Bayes.class);
    assertThat(aGivenNotB.value).isEqualTo((0.995 * 0.75) / 0.95, offset(0.001));  // P(~B|A) P(A) / P(~B)
    System.out.printf("bayes aGivenNotB:\n%s\n", aGivenNotB.dump());

    Known notAGivenB = solve.find(new Prob(new Term("A", false), new Term("B")), 0);
    assertThat(notAGivenB).isNotNull();
    assertThat(notAGivenB.input).isFalse();
    assertThat(notAGivenB.byRule.getClass()).isEqualTo((Class) Bayes.class);
    assertThat(notAGivenB.value).isEqualTo((0.02 * 0.25) / 0.05, offset(0.001));  // P(B|~A) P(~A) / P(B)
    System.out.printf("bayes notAGivenB:\n%s\n", notAGivenB.dump());

  }
}
