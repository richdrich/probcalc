import org.junit.Before;
import org.junit.Test;
import probcalc.Depends;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by richardparratt on 7/03/15.
 */
public class TestDepends {

	Depends depends = new Depends();

	@Before
	public void setup() {
		depends.add("A", "B");
		depends.add("B", "C");
		depends.add("B", "D");
		depends.add("C", "F");
		depends.add("C", "E");
		depends.add("D", "E");
	}

	@Test
	public void independent() {
		assertThat(depends.isIndependent("A"));
		assertThat(!depends.isIndependent("B"));
		assertThat(!depends.isIndependent("E"));
		assertThat(!depends.isIndependent("Z"));
	}

	@Test
	public void dependant() {
		assertThat(!depends.isDependent("B", "A"));
		assertThat(!depends.isDependent("A", "B"));

		assertThat(depends.isDependent("A", "C"));
		assertThat(depends.isDependent("A", "E"));
		assertThat(depends.isDependent("C", "F"));

		assertThat(!depends.isDependent("E", "F"));
		assertThat(!depends.isDependent("F", "E"));

	}
}
