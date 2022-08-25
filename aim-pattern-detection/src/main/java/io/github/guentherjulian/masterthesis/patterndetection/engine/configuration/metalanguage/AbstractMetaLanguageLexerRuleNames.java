package io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage;

abstract class AbstractMetaLanguageLexerRuleNames implements MetaLanguageLexerRules {

	protected String lexerRuleNamePrefix;

	protected String tokenPlaceholder;

	protected String tokenIf;

	protected String tokenElse;

	protected String tokenIfElse;

	protected String tokenIfClose;

	protected String tokenList;

	protected String tokenListClose;

	protected AbstractMetaLanguageLexerRuleNames(String lexerRuleNamePrefix) {
		this.lexerRuleNamePrefix = lexerRuleNamePrefix;
	}

	public void setLexerRuleNamePrefix(String lexerRuleNamePrefix) {
		this.lexerRuleNamePrefix = lexerRuleNamePrefix;
	}

	public void setTokenPlaceholder(String tokenPlaceholder) {
		this.tokenPlaceholder = tokenPlaceholder;
	}

	public void setTokenIf(String tokenIf) {
		this.tokenIf = tokenIf;
	}

	public void setTokenElse(String tokenElse) {
		this.tokenElse = tokenElse;
	}

	public void setTokenIfElse(String tokenIfElse) {
		this.tokenIfElse = tokenIfElse;
	}

	public void setTokenIfClose(String tokenIfClose) {
		this.tokenIfClose = tokenIfClose;
	}

	public void setTokenList(String tokenList) {
		this.tokenList = tokenList;
	}

	public void setTokenListClose(String tokenListClose) {
		this.tokenListClose = tokenListClose;
	}

	protected String getLexerRuleName(String tokenName) {
		String prefix = this.lexerRuleNamePrefix.toUpperCase();
		if (!prefix.endsWith("_")) {
			prefix = prefix + "_";
		}
		return prefix + tokenName.toUpperCase();
	}

	public String getMetaLanguagePrefix() {
		return this.lexerRuleNamePrefix;
	}

	public String getPlaceholderTokenLexerRuleName() {
		return this.getLexerRuleName(this.tokenPlaceholder);
	}

	public String getIfTokenLexerRuleName() {
		return this.getLexerRuleName(this.tokenIf);
	}

	public String getElseTokenLexerRuleName() {
		return this.getLexerRuleName(this.tokenElse);
	}

	public String getIfElseTokenLexerRuleName() {
		return this.getLexerRuleName(this.tokenIfElse);
	}

	public String getIfCloseTokenLexerRuleName() {
		return this.getLexerRuleName(this.tokenIfClose);
	}

	public String getListTokenLexerRuleName() {
		return this.getLexerRuleName(this.tokenList);
	}

	public String getListCloseTokenLexerRuleName() {
		return this.getLexerRuleName(this.tokenListClose);
	}
}
