package io.github.guentherjulian.masterthesis.patterndetection.engine.matching;

import java.util.ArrayList;
import java.util.List;

import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTreeElement;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTreePathList;

public class PathMatch {

	private List<ParseTreeElement> templateParseTreePath;
	private List<ParseTreeElement> compilationUnitParseTreePath;
	private boolean isMatch;

	public PathMatch(List<ParseTreeElement> templateParseTreePath,
			List<ParseTreeElement> compilationUnitParseTreePath) {
		this.templateParseTreePath = templateParseTreePath;
		this.compilationUnitParseTreePath = compilationUnitParseTreePath;
		this.isMatch = false;
	}

	public PathMatch(ParseTreePathList templateParseTreePathList, ParseTreePathList compilationUnitParseTreePathList) {
		this.templateParseTreePath = templateParseTreePathList;
		this.compilationUnitParseTreePath = compilationUnitParseTreePathList;
		this.isMatch = false;
	}

	public PathMatch(ParseTreeElement templatePathElement, ParseTreeElement compilationUnitPathElement) {
		this.templateParseTreePath = new ArrayList<>();
		this.templateParseTreePath.add(templatePathElement);
		this.compilationUnitParseTreePath = new ArrayList<>();
		this.compilationUnitParseTreePath.add(compilationUnitPathElement);
		this.isMatch = false;
	}

	public List<ParseTreeElement> getTemplateParseTreePath() {
		return templateParseTreePath;
	}

	public void setTemplateParseTreePath(List<ParseTreeElement> templateParseTreePath) {
		this.templateParseTreePath = templateParseTreePath;
	}

	public List<ParseTreeElement> getCompilationUnitParseTreePath() {
		return compilationUnitParseTreePath;
	}

	public void setCompilationUnitParseTreePath(List<ParseTreeElement> compilationUnitParseTreePath) {
		this.compilationUnitParseTreePath = compilationUnitParseTreePath;
	}

	public boolean isMatch() {
		return isMatch;
	}

	public void setMatch(boolean isMatch) {
		this.isMatch = isMatch;
	}
}