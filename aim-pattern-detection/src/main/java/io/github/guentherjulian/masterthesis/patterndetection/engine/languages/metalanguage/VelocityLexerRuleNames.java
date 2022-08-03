package io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage;

public class VelocityLexerRuleNames extends AbstractMetaLanguageLexerRuleNames {

	public VelocityLexerRuleNames() {
		this("fm_");
	}

	public VelocityLexerRuleNames(String lexerRuleNamePrefix) {
		super(lexerRuleNamePrefix);

		this.tokenPlaceholder = "PLACEHOLDER";
		this.tokenIf = "IF";
		this.tokenElse = "ELSE";
		this.tokenIfElse = "ELSE_IF";
		this.tokenIfClose = "IF_LIST_CLOSE";
		this.tokenList = "LIST";
		this.tokenListClose = "IF_LIST_CLOSE";
	}
}
