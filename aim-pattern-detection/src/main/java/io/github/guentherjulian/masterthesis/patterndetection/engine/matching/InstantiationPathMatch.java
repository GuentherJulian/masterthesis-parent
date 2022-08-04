package io.github.guentherjulian.masterthesis.patterndetection.engine.matching;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstantiationPathMatch {

	private boolean isMatch;
	private Map<String, List<String>> placeholderSubstitutions;

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

	public Map<String, List<String>> getPlaceholderSubstitutions() {
		return placeholderSubstitutions;
	}

	public void setPlaceholderSubstitutions(Map<String, List<String>> placeholderSubstitutions) {
		this.placeholderSubstitutions = placeholderSubstitutions;
	}
}
