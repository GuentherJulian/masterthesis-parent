package io.github.guentherjulian.masterthesis.patterndetection.engine.exception;

import java.util.Set;

public class PlaceholderClashException extends NoMatchException {

	private String placeholder;
	private String clashSubstitution;
	private Set<String> clashSubstitutions;

	public PlaceholderClashException(String placeholder, String substitution, Set<String> clashSubstitutions,
			String message) {
		super(message);
		this.placeholder = placeholder;
		this.clashSubstitution = substitution;
		this.clashSubstitutions = clashSubstitutions;
	}

	public String getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	public String getClashSubstitution() {
		return clashSubstitution;
	}

	public void setClashSubstitution(String clashSubstitution) {
		this.clashSubstitution = clashSubstitution;
	}

	public Set<String> getClashSubstitutions() {
		return clashSubstitutions;
	}

	public void setClashSubstitutions(Set<String> clashSubstitutions) {
		this.clashSubstitutions = clashSubstitutions;
	}
}