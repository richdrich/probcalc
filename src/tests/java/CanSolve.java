import org.junit.Before;
import org.junit.Test;
import probcalc.*;
import probcalc.identities.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.data.Offset.offset;

/**
 * // TODO class Javadoc
 *
 * @author richard.parratt
 */
public class CanSolve {
  public Solve solve;
  private Map<Prob, Double> inputs;

  @Before
  public void setup() {
    inputs = new HashMap<Prob, Double>();
    inputs.put(new Prob(new Term("A")), 0.75);

    inputs.put(new Prob(new Term("B"), new Term("A")), 0.005);
    inputs.put(new Prob(new Term("B"), new Term("A", false)), 0.02);

    initSolve();
  }

  private void initSolve() {
    solve = new Solve(inputs);
  }

  @Test
  public void definedInput() {
    Known a = solve.find(new Prob(new Term("A")));
    assertThat(a.input).isTrue();
    assertThat(a.value).isEqualTo(0.75);
  }

  @Test
  public void complement() {
    Known notA = solve.find(new Prob(new Term("A", false)));
    assertThat(notA).isNotNull();
    System.out.printf("complement:\n%s\n", notA.dump());

    assertThat(notA.input).isFalse();
    assertThat(notA.byRule.getClass()).isEqualTo((Class)Complement.class);
    assertThat(notA.value).isEqualTo(0.25);
  }

  @Test
  public void and() {
    inputs.put(new Prob(new Term("F")), 0.05);
    initSolve();

    Known aAndF = solve.find(new Prob(Arrays.asList(new Term[] {new Term("A"), new Term("F")})));
    assertThat(aAndF).isNotNull();
    System.out.printf("and:\n%s\n", aAndF.dump());

    assertThat(aAndF.input).isFalse();
    assertThat(aAndF.byRule.getClass()).isEqualTo((Class)And.class);
    assertThat(aAndF.value).isEqualTo(0.75 * 0.05);
  }

  @Test
  public void bayes() {
    Known aGivenB = solve.find(new Prob(new Term("A"), new Term("B")));
    assertThat(aGivenB).isNotNull();
    System.out.printf("bayes:\n%s\n", aGivenB.dump());
    assertThat(aGivenB.input).isFalse();
    assertThat(aGivenB.byRule.getClass()).isEqualTo((Class)Bayes.class);
    assertThat(aGivenB.value).isEqualTo((0.005 * 0.75) / 0.00875, offset(0.001)); // P(B|A) P(A) / P(B)

  }

  @Test
  public void bayesInverse() {
    Known aGivenNotB = solve.find(new Prob(new Term("A"), new Term("B", false)));
    assertThat(aGivenNotB).isNotNull();
    System.out.printf("bayes aGivenNotB:\n%s\n", aGivenNotB.dump());
    assertThat(aGivenNotB.input).isFalse();
    assertThat(aGivenNotB.byRule.getClass()).isEqualTo((Class) Bayes.class);
    assertThat(aGivenNotB.value).isEqualTo((0.995 * 0.75) / 0.99125, offset(0.001));  // P(~B|A) P(A) / P(~B)

    Known notAGivenB = solve.find(new Prob(new Term("A", false), new Term("B")));
    assertThat(notAGivenB).isNotNull();
    System.out.printf("bayes notAGivenB:\n%s\n", notAGivenB.dump());
    assertThat(notAGivenB.input).isFalse();
    assertThat(notAGivenB.byRule.getClass()).isEqualTo((Class) Bayes.class);
    assertThat(notAGivenB.value).isEqualTo((0.02 * 0.25) / 0.00875, offset(0.001));  // P(B|~A) P(~A) / P(B)

  }

  @Test
  public void multiplyAbsolute() {
    inputs.put(new Prob(new Term("C"), new Term("A")), 0.005);
    inputs.put(new Prob(new Term("C"), new Term("A", false)), 0.02);
    initSolve();

    Known c = solve.find(new Prob(new Term("C")));
    System.out.printf("multiplyAbsolute c:\n%s\n", c.dump());

    assertThat(c).isNotNull();
    assertThat(c.input).isFalse();
   // assertThat(c.byRule.getClass()).isEqualTo((Class) probcalc.identities.Bayes.class);
    assertThat(c.value).isEqualTo(0.00875, offset(0.001));

  }

  @Test
  public void multiplyAbsolute2() {
    inputs.put(new Prob(new Term("F")), 0.05);
    inputs.put(new Prob(new Term("C"), Arrays.asList(new Term[]{new Term("A"), new Term("F")})), 0.1);
    inputs.put(new Prob(new Term("C"), Arrays.asList(new Term[] {new Term("A", false), new Term("F")})), 0.05);
    inputs.put(new Prob(new Term("C"), Arrays.asList(new Term[] {new Term("A"), new Term("F", false)})), 0.08);
    inputs.put(new Prob(new Term("C"), Arrays.asList(new Term[] {new Term("A", false), new Term("F", false)})), 0.001);
    initSolve();

    Known c = solve.find(new Prob(new Term("C")));
    assertThat(c).isNotNull();
    System.out.printf("multiplyAbsolute2 c:\n%s\n", c.dump());
    assertThat(c.value).isEqualTo(0.2375 * 0.001 + 0.7125 * 0.08 + 0.0125 * 0.05 + 0.0375 * 0.1, offset(0.0001));
  }

  @Test
  public void joint() {
    Known aJointB = solve.find(Prob.parse("P(A B)"));
    assertThat(aJointB).isNotNull();
    System.out.printf("joint aJointB:\n%s\n", aJointB.dump());
    assertThat(aJointB.value).isEqualTo(0.005 * 0.75, offset(0.001));
  }

  @Test
  public void bayesPartial() {
    Known aGivenBprime = solve.find(Prob.parse("P'(A|B)"));
    assertThat(aGivenBprime).isNotNull();
    System.out.printf("bayesPartial aGivenBprime:\n%s\n", aGivenBprime.dump());
    assertThat(aGivenBprime.value).isEqualTo(0.005 * 0.75, offset(0.0001));
  }

  @Test
  public void bayesFromPartials() {
    solve.rules = Arrays.asList(new Rule[]{new Complement(solve), new And(solve),
            new BayesPartial(solve), new BayesFromPartials(solve)});

    Known aGivenB = solve.find(new Prob(new Term("A"), new Term("B")));
    assertThat(aGivenB).isNotNull();
    System.out.printf("bayesFromPartials:\n%s\n", aGivenB.dump());
    assertThat(aGivenB.input).isFalse();
    assertThat(aGivenB.byRule.getClass()).isEqualTo((Class) BayesFromPartials.class);
    assertThat(aGivenB.value).isEqualTo((0.005 * 0.75) / 0.00875, offset(0.001)); // P(B|A) P(A) / P(B)
  }

  @Test
  public void bayesPartial3() {
    inputs.put(Prob.parse("P(C|A)"), 0.02);
    inputs.put(Prob.parse("P(D|A)"), 0.03);
    initSolve();

    Known aGivenBCDprime = solve.find(Prob.parse("P'(A|B C D)"));
    assertThat(aGivenBCDprime).isNotNull();
    System.out.printf("bayesPartial aGivenBCDprime:\n%s\n", aGivenBCDprime.dump());
    assertThat(aGivenBCDprime.value).isEqualTo(0.75 * .005 * .02 * .03, offset(0.0001));
  }
}
