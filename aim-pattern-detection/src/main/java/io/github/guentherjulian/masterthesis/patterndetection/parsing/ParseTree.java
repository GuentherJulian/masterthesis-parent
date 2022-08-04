package io.github.guentherjulian.masterthesis.patterndetection.parsing;

public class ParseTree {

	private ParseTreePathList parseTreePathList;

	public ParseTree(ParseTreePathList parseTreePathList) {
		this.parseTreePathList = parseTreePathList;
	}

	public ParseTreePathList getParseTreePathList() {
		return parseTreePathList;
	}
}