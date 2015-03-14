import org.junit.Before;
import org.junit.Test;
import probcalc.Known;
import probcalc.Prob;
import probcalc.Solve;

import java.util.HashMap;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.data.Offset.offset;

/**
 * Created by richardparratt on 14/03/15.
 */
public class MultipleDescendants {

	public Solve solve;
	private Map<Prob, Double> inputs;

	@Before
	public void setup() {
		inputs = new HashMap<Prob, Double>();
		inputs.put(Prob.parse("P(A)"), 0.5);

		inputs.put(Prob.parse("P(X1|A)"), 0.2);
		inputs.put(Prob.parse("P(X1|~A)"), 0.6);
		inputs.put(Prob.parse("P(X2|A)"), 0.2);
		inputs.put(Prob.parse("P(X2|~A)"), 0.6);
		inputs.put(Prob.parse("P(X3|A)"), 0.2);
		inputs.put(Prob.parse("P(X3|~A)"), 0.6);

		initSolve();
	}

	private void initSolve() {
		solve = new Solve(inputs);
	}

	@Test
	public void x1x2() {
		Known x1x2 = solve.find(Prob.parse("P(X1 X2)"));
		assertThat(x1x2).isNotNull();
		System.out.printf(" x1x2:\n%s\n", x1x2.dump());
		assertThat(x1x2.value).isEqualTo(0.4 * 0.4, offset(0.001));
	}

	@Test
	public void x1x2x3() {
		Known x1x2x3 = solve.find(Prob.parse("P(X1 X2 X3)"));
		assertThat(x1x2x3).isNotNull();
		System.out.printf(" x1x2x3:\n%s\n", x1x2x3.dump());
		assertThat(x1x2x3.value).isEqualTo(0.4 * 0.4 *.4, offset(0.001));
	}

	@Test
	public void ax1x2nx3() {
		Known a = solve.find(Prob.parse("P(X1 X2 ~X3|A)"));
		assertThat(a).isNotNull();
		System.out.printf(" a:\n%s\n", a.dump());
		assertThat(a.value).isEqualTo(0.8 * 0.2 *.2, offset(0.001));
	}

	@Test
	public void aGivenx1x2nx3() {
		Known a = solve.find(Prob.parse("P(A|X1 X2 ~X3)"));
		assertThat(a).isNotNull();
		System.out.printf(" a:\n%s\n", a.dump());
		assertThat(a.value).isEqualTo(0.32 * .5 /.088, offset(0.001));
	}

	@Test
	public void x3givenx1() {
		Known a = solve.find(Prob.parse("P(X3|X1)"));
		assertThat(a).isNotNull();
		System.out.printf(" a:\n%s\n", a.dump());
		//assertThat(a.value).isEqualTo(0.32 * .5 /.088, offset(0.001));
	}
}
