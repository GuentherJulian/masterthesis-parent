package io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution;

import java.util.Set;

public class PlaceholderResolutionResult {

	private String placeholder;
	private Set<String> substitutions;

	public String getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	public Set<String> getSubstitutions() {
		return substitutions;
	}

	public void setSubstitutions(Set<String> substitutions) {
		this.substitutions = substitutions;
	}
}
