package io.github.guentherjulian.masterthesis.patterndetection.engine.matching;

import java.util.Map;

import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTreeElement;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTreePathList;

public class AtomarMatch {

	private ParseTreeElement template;

	private ParseTreeElement appCode;

	private Map<String, String> variableSubstitutions;

	private boolean containsPh;

	public AtomarMatch(ParseTreePathList parseTreePathListTemplate, ParseTreePathList parseTreePathListCompilationUnit,
			Map<String, String> variableSubstitutions) {
		this.template = parseTreePathListTemplate;
		this.appCode = parseTreePathListCompilationUnit;
		this.variableSubstitutions = variableSubstitutions;
		containsPh = !this.variableSubstitutions.isEmpty();
	}

	public ParseTreeElement getTemplate() {
		return template;
	}

	public ParseTreeElement getAppCode() {
		return appCode;
	}

	public Map<String, String> resolveVariableSubstitutions() {
		return variableSubstitutions;
	}

	public boolean containsPh() {
		return containsPh;
	}

	@Override
	public String toString() {
		return template.toString() + " -> " + appCode.toString();
	}

}
