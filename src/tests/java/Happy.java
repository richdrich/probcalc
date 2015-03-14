import org.fest.assertions.data.Offset;
import org.junit.Before;
import org.junit.Test;
import probcalc.Known;
import probcalc.Prob;
import probcalc.Solve;

import java.util.HashMap;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by richardparratt on 10/03/15.
 */
public class Happy {

	public Solve solve;
	private Map<Prob, Double> inputs;

	@Before
	public void setup() {
		inputs = new HashMap<Prob, Double>();
		inputs.put(Prob.parse("P(S)"), 0.7);
		inputs.put(Prob.parse("P(R)"), 0.01);

		inputs.put(Prob.parse("P(H|S R)"), 1.0);
		inputs.put(Prob.parse("P(H|~S R)"), 0.9);
		inputs.put(Prob.parse("P(H|S ~R)"), 0.7);
		inputs.put(Prob.parse("P(H|~S ~R)"), 0.1);

		initSolve();
	}

	private void initSolve() {
		solve = new Solve(inputs);
	}

	@Test
	public void independent() {
		Known rGivenS = solve.find(Prob.parse("P(R|S)"));
		assertThat(rGivenS).isNotNull();
		System.out.printf("independent rGivenS:\n%s\n", rGivenS.dump());
		assertThat(rGivenS.value).isEqualTo(0.01);
	}

	@Test
	public void explainAwayH() {
		Known h = solve.find(Prob.parse("P(H)"));
		assertThat(h).isNotNull();
		System.out.printf("explainAway h:\n%s\n", h.dump());
	}

	@Test
	public void explainAwayHgivenS() {
		Known hGivenS = solve.find(Prob.parse("P(H|S)"));
		assertThat(hGivenS).isNotNull();
		System.out.printf("explainAway hGivenS:\n%s\n", hGivenS.dump());
		assertThat(hGivenS.value).isEqualTo(0.703, Offset.offset(0.001));
	}

	@Test
	public void explainAwayHS() {
		Known hs = solve.find(Prob.parse("P(H S)"));
		assertThat(hs).isNotNull();
		System.out.printf("explainAway hs:\n%s\n", hs.dump());
		assertThat(hs.value).isEqualTo(0.492, Offset.offset(0.001));
	}

	@Test
	public void explainAwayRHS() {
		Known rHS = solve.find(Prob.parse("P(R H S)"));
		assertThat(rHS).isNotNull();
		System.out.printf("explainAway chain rHS:\n%s\n", rHS.dump());
		assertThat(rHS.value).isEqualTo(0.007, Offset.offset(0.001));
	}

	@Test
	public void explainAwayRgivenHS() {
		Known rGivenHS = solve.find(Prob.parse("P(R|H S)"));
		assertThat(rGivenHS).isNotNull();
		System.out.printf("explainAway rGivenHS:\n%s\n", rGivenHS.dump());
		assertThat(rGivenHS.value).isEqualTo(0.0142, Offset.offset(0.001));
	}

	@Test
	public void explainAwayRgivenH() {
		Known rGivenH = solve.find(Prob.parse("P(R|H)"));
		assertThat(rGivenH).isNotNull();
		System.out.printf("explainAway rGivenHS:\n%s\n", rGivenH.dump());
		assertThat(rGivenH.value).isEqualTo(0.0185, Offset.offset(0.0001));
	}

	@Test
	public void explainAwayRgivenHnotS() {
		Known rGivenHnotS = solve.find(Prob.parse("P(R|H ~S)"));
		assertThat(rGivenHnotS).isNotNull();
		System.out.printf("explainAway rGivenHnotS:\n%s\n", rGivenHnotS.dump());
		assertThat(rGivenHnotS.value).isEqualTo(0.08333, Offset.offset(0.0001));
	}
}
