import org.junit.Test;
import probcalc.Prob;
import probcalc.Term;

import java.util.Arrays;

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

    Prob pPrime = new Prob(new Term("A"), new Term("Q"));
    pPrime.prime = true;
    assertThat(pPrime.toString()).isEqualTo("P'(A|Q)");
  }

  @Test
  public void testParse() {
    assertThat(Prob.parse("P(D)").toString()).isEqualTo("P(D)");
    assertThat(Prob.parse("P(D|S)").toString()).isEqualTo("P(D|S)");
    assertThat(Prob.parse("P(D~E|S T)").toString()).isEqualTo("P(D ~E|S T)");
    assertThat(Prob.parse("P(~F)").toString()).isEqualTo("P(~F)");
    assertThat(Prob.parse("P(~F G)").toString()).isEqualTo("P(~F G)");
    assertThat(Prob.parse("P'(K |Ae)").toString()).isEqualTo("P'(K|Ae)");
  }
}
