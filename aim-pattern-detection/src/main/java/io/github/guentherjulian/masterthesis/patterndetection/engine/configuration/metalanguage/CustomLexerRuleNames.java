package io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage;

public class CustomLexerRuleNames extends AbstractMetaLanguageLexerRuleNames {

	public CustomLexerRuleNames(String lexerRuleNamePrefix, String lexerRuleNameIf, String lexerRuleNameIfElse,
			String lexerRuleNameElse, String lexerRuleNameIfClose, String lexerRuleNameList,
			String lexerRuleNameListClose, String tokenPlaceholder) {
		super(lexerRuleNamePrefix);
		this.tokenIf = lexerRuleNameIf;
		this.tokenIfElse = lexerRuleNameIfElse;
		this.tokenElse = lexerRuleNameElse;
		this.tokenIfClose = lexerRuleNameIfClose;
		this.tokenList = lexerRuleNameList;
		this.tokenListClose = lexerRuleNameListClose;
		this.tokenPlaceholder = tokenPlaceholder;
	}
}
