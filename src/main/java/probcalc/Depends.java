package probcalc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by richardparratt on 7/03/15.
 */
public class Depends extends HashMap<String, Depends.Node> {
	
	public class Node {
		String term;
		Map<String, Node> dependsOn;

		Node(String term) {
			this.term = term;
			this.dependsOn = new HashMap<>();
		}
	}
	
	public void add(String from, String to) {
		Node fromNode = findOrCreateNode(from);
		findOrCreateNode(to).dependsOn.put(from, fromNode);
	}

	private Node findOrCreateNode(String term) {
		Node node;
		if(containsKey(term)) {
			return get(term);
		}
		
		node = new Node(term);
		put(term, node);
		return node;
	}

	public boolean isIndependent(String term) {
		return !containsKey(term) || get(term).dependsOn.isEmpty();
	}

	public boolean anyDependent(Set<String> from, String to) {
		return from.stream().anyMatch(f -> isDependent(f, to));
	}

	public boolean isDependent(String from, String to) {
		if(isIndependent(to)) {
			return false;
		}
		
		Node toNode = get(to);
		if(toNode.dependsOn.containsKey(from)) {
			return true; 	// Direct dependency
		}

		return toNode.dependsOn.values().stream().anyMatch(d -> isDependent(from, d.term));
	}
}
