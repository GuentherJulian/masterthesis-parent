package io.github.guentherjulian.masterthesis.patterndetection.engine.matching;

import java.util.List;
import java.util.Map;

import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTree;

public class TreeMatch {

	private ParseTree templateParseTree;
	private ParseTree compilationUnitParseTree;
	private Map<String, List<String>> placeholderSubstitutions;
	private boolean isMatch;

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

	public Map<String, List<String>> getPlaceholderSubstitutions() {
		return placeholderSubstitutions;
	}

	public void setPlaceholderSubstitutions(Map<String, List<String>> placeholderSubstitutions) {
		this.placeholderSubstitutions = placeholderSubstitutions;
	}

	public boolean isMatch() {
		return isMatch;
	}

	public void setMatch(boolean isMatch) {
		this.isMatch = isMatch;
	}

}
