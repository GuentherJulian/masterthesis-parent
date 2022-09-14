package io.github.guentherjulian.masterthesis.patterndetection.engine.matching;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTree;

public class TreeMatch {

	private ParseTree templateParseTree;
	private ParseTree compilationUnitParseTree;
	private Map<String, Set<String>> placeholderSubstitutions;
	private boolean isMatch;
	private Exception exception;
	private List<PathMatch> matches;

	public TreeMatch() {
		this.templateParseTree = null;
		this.compilationUnitParseTree = null;
		this.placeholderSubstitutions = null;
		this.isMatch = false;
	}

	public ParseTree getTemplateParseTree() {
		return templateParseTree;
	}

	public void setTemplateParseTree(ParseTree templateParseTree) {
		this.templateParseTree = templateParseTree;
	}

	public ParseTree getCompilationUnitParseTree() {
		return compilationUnitParseTree;
	}

	public void setCompilationUnitParseTree(ParseTree compilationUnitParseTree) {
		this.compilationUnitParseTree = compilationUnitParseTree;
	}

	public Map<String, Set<String>> getPlaceholderSubstitutions() {
		return placeholderSubstitutions;
	}

	public void setPlaceholderSubstitutions(Map<String, Set<String>> placeholderSubstitutions) {
		this.placeholderSubstitutions = placeholderSubstitutions;
	}

	public boolean isMatch() {
		return isMatch;
	}

	public void setMatch(boolean isMatch) {
		this.isMatch = isMatch;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public List<PathMatch> getMatches() {
		return matches;
	}

	public void setMatches(List<PathMatch> matches) {
		this.matches = matches;
	}

}
