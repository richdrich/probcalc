package probcalc;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by richardparratt on 7/03/15.
 */
public class Main {

	public static void main( String [] args) {

		Scanner sc = new Scanner(System.in);

		String prob = "(P'?\\([\\s\\w~|]+\\))";
		Pattern assign = Pattern.compile(prob + "\\s*=\\s*([0-9]*\\.[0-9]+|[0-9]+)");
		Pattern query = Pattern.compile(prob + "\\s*\\?");

		Map<Prob, Double> inputs = new HashMap<>();
		while (true) {
			System.out.print("% ");
			String s = sc.nextLine();
			if(s.startsWith("q")) {
				return;
			}

			Matcher assignMatcher = assign.matcher(s);
			if(assignMatcher.matches()) {
				try {
					Prob p = Prob.parse(assignMatcher.group(1));
					double v = Double.parseDouble(assignMatcher.group(2));
					inputs.put(p, v);
				}
				catch(Exception ex) {
					System.err.printf("%s thrown parsing input", ex);
				}
				continue;
			}
			Matcher queryMatcher = query.matcher(s);
			if(queryMatcher.matches()) {
				Known a =null;
				try {
					Prob p = Prob.parse(queryMatcher.group(1));

					Solve solve = new Solve(inputs);

					a = solve.find(p);
				}
				catch(Exception ex) {
					System.err.printf("%s thrown finding a solution", ex);
				}
				if(a==null) {
					System.err.print("No solution found\n");
				}
				else {
					System.out.print(a.dump());
				}
				continue;
			}
			if(s.equals("!")) {
				inputs = new HashMap<>();
				System.out.println("Cleared");
				continue;
			}
			if(s.equals("?")) {
				inputs.entrySet().stream().forEach(e -> System.out.printf("%s = %f", e.getKey(), e.getValue()));
				continue;
			}
			System.err.println("???");

		}
	}
}
