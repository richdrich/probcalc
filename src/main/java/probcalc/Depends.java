package probcalc;

import java.util.*;
import java.util.stream.Collectors;

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

	public Set<String> directAncestors(String term) {
		if(!containsKey(term)) {
			return Collections.emptySet();
		}

		return get(term).dependsOn.keySet();
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

	public boolean allIndependent(Collection<String> terms) {
		for(String outer : terms) {
			for(String inner : terms) {
				if(inner != outer && (isDependent(inner, outer) || isDependent(outer, inner))) return false;
			}
		}

		return true;
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

	public Set<String> nearestCommonAncestors(String a, String b) {
		Set<String> res = new HashSet<>();

		LinkedHashMap<String, Integer> commonAncestorMap = commonAncestorMap(a, b);
		if(commonAncestorMap.isEmpty()) {
			return res;
		}

		Wrap<Integer> lowestDist = new Wrap<>(-1);
		commonAncestorMap.entrySet().stream().sorted(Entry.comparingByValue()).forEachOrdered(e -> {
			if (lowestDist.v < 0) {
				lowestDist.v = e.getValue();
			}
			if (lowestDist.v == e.getValue()) {
				res.add(e.getKey());
			}
		});

		return res;
	}

	public List<String> commonAncestors(String a, String b) {
		LinkedHashMap<String, Integer> commonAncestorMap = commonAncestorMap(a, b);

		return commonAncestorMap.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(Entry::getKey).collect(Collectors.toList());
	}

	private LinkedHashMap<String, Integer> commonAncestorMap(String a, String b) {
		Map<String, Integer> ancestorsA = ancestors(a, 1);
		Map<String, Integer> ancestorsB = ancestors(b, 1);

		LinkedHashMap<String, Integer> commonAncestorMap = new LinkedHashMap();
		ancestorsA.entrySet().stream().forEach(av -> {
			if(ancestorsB.containsKey(av.getKey())) {
				commonAncestorMap.put(av.getKey(), av.getValue() + ancestorsB.get(av.getKey()));
			}
		});
		return commonAncestorMap;
	}


	private Map<String, Integer> ancestors(String a, int depth) {
		Map<String, Integer> res = new HashMap<>();
		if(!containsKey(a)) return res;

		get(a).dependsOn.keySet().forEach(n -> {
			res.put(n, depth);
			res.putAll(ancestors(n, depth + 1));
		});

		return res;
	}
}
