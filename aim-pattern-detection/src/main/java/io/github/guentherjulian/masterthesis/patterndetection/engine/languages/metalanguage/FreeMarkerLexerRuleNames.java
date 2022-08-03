package io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage;

public class FreeMarkerLexerRuleNames extends AbstractMetaLanguageLexerRuleNames {

	public FreeMarkerLexerRuleNames() {
		this("fm_");
	}

	public FreeMarkerLexerRuleNames(String lexerRuleNamePrefix) {
		super(lexerRuleNamePrefix);

		this.tokenPlaceholder = "PLACEHOLDER";
		this.tokenIf = "IF";
		this.tokenElse = "ELSE";
		this.tokenIfElse = "ELSE_IF";
		this.tokenIfClose = "IF_CLOSE";
		this.tokenList = "LIST";
		this.tokenListClose = "LIST_CLOSE";
	}
}
