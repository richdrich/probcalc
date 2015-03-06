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

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * // TODO class Javadoc
 *
 * @author richard.parratt
 */
public class ProbConvert {

  @Test
  public void testToString() {
    assertThat(new Prob(new Term("A")).toString()).isEqualTo("P(A)");

    assertThat(new Prob(Arrays.asList(new Term[]{new Term("A"), new Term("B0")})).toString()).isEqualTo("P(A B0)");
    assertThat(new Prob(Arrays.asList(new Term[]{new Term("A"), new Term("B", false)})).toString()).isEqualTo("P(A ~B)");

    assertThat(new Prob(new Term("A"), new Term("Q")).toString()).isEqualTo("P(A|Q)");
    assertThat(new Prob(Arrays.asList(new Term[]{new Term("A"), new Term("B", false)}), new Term("Q")).toString()).isEqualTo("P(A ~B|Q)");

  }

  @Test
  public void testParse() {
    assertThat(Prob.parse("P(D)").toString()).isEqualTo("P(D)");
    assertThat(Prob.parse("P(D|S)").toString()).isEqualTo("P(D|S)");
    assertThat(Prob.parse("P(D~E|S T)").toString()).isEqualTo("P(D ~E|S T)");

  }
}
