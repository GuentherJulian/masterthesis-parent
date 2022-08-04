package io.github.guentherjulian.masterthesis.patterndetection.engine.matching;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InstantiationPathMatch {

	private boolean isMatch;
	private Map<String, Set<String>> placeholderSubstitutions;

	public InstantiationPathMatch() {
		this.isMatch = false;
		this.placeholderSubstitutions = new HashMap<>();
	}

	public boolean isMatch() {
		return isMatch;
	}

	public void setMatch(boolean isMatch) {
		this.isMatch = isMatch;
	}

	public Map<String, Set<String>> getPlaceholderSubstitutions() {
		return placeholderSubstitutions;
	}

	public void setPlaceholderSubstitutions(Map<String, Set<String>> placeholderSubstitutions) {
		this.placeholderSubstitutions = placeholderSubstitutions;
	}
}
