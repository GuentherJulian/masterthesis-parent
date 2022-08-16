package io.github.guentherjulian.masterthesis.patterndetection.engine.exception;

import java.util.Set;

public class PlaceholderClashException extends PlaceholderResolutionException {

	private static final long serialVersionUID = 1L;

	private String placeholder;
	private Set<String> currentSubstitutions;
	private Set<String> clashSubstitutions;

	public PlaceholderClashException(String placeholder, Set<String> currentSubstitutions,
			Set<String> clashSubstitutions, String message) {
		super(message);
		this.placeholder = placeholder;
		this.currentSubstitutions = currentSubstitutions;
		this.clashSubstitutions = clashSubstitutions;
	}

	public String getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	public Set<String> getClashSubstitution() {
		return currentSubstitutions;
	}

	public void setClashSubstitution(Set<String> currentSubstitutions) {
		this.currentSubstitutions = currentSubstitutions;
	}

	public Set<String> getClashSubstitutions() {
		return clashSubstitutions;
	}

	public void setClashSubstitutions(Set<String> clashSubstitutions) {
		this.clashSubstitutions = clashSubstitutions;
	}
}