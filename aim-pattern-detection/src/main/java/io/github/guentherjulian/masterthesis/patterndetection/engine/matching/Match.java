package io.github.guentherjulian.masterthesis.patterndetection.engine.matching;

import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTreePath;

public class Match {

	private ParseTreePath templateParseTreePath;
	private ParseTreePath compilationUnitParseTreePath;

	public Match(ParseTreePath templateParseTreePath, ParseTreePath compilationUnitParseTreePath) {
		super();
		this.templateParseTreePath = templateParseTreePath;
		this.compilationUnitParseTreePath = compilationUnitParseTreePath;
	}
}