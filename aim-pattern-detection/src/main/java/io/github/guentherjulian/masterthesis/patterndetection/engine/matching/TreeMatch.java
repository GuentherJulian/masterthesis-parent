package io.github.guentherjulian.masterthesis.patterndetection.engine.matching;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTree;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTreeElement;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTreePath;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTreePathList;

public class TreeMatch {

	private ParseTree templateParseTree;
	private ParseTree compilationUnitParseTree;
	private Map<String, Set<String>> placeholderSubstitutions;
	private boolean isMatch;
	private Exception exception;
	private PathMatch pathMatch;
	private List<ParseTreeElement> matchedTemplateElements;

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

	public PathMatch getPathMatch() {
		return this.pathMatch;
	}

	public void setPathMatch(PathMatch pathMatch) {
		this.pathMatch = pathMatch;
	}

	public List<ParseTreeElement> getMatchedTemplateElements() {
		return matchedTemplateElements;
	}

	public void setMatchedTemplateElements(List<ParseTreeElement> matchedTemplateElements) {
		this.matchedTemplateElements = matchedTemplateElements;
	}

	public double getMatchPercentage() {
		if (this.templateParseTree.getParseTreePathList() == null || this.matchedTemplateElements.isEmpty()) {
			return 0;
		}

		double numPathElements = countPathElements(this.templateParseTree.getParseTreePathList());
		double numMatchedElements = countPathElements(this.matchedTemplateElements);
		return (numMatchedElements / numPathElements) * 100d;
	}

	private int countPathElements(List<ParseTreeElement> parseElements) {
		int count = 0;
		for (ParseTreeElement parseElement : parseElements) {
			if (parseElement instanceof ParseTreePath) {
				count++;
			}
			if (parseElement instanceof ParseTreePathList) {
				count += countPathElements((List<ParseTreeElement>) parseElement);
			}
		}
		return count;
	}
}
