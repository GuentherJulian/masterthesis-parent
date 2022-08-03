package io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage;

public interface MetaLanguageLexerRules {

	String getMetaLanguagePrefix();

	String getPlaceholderTokenLexerRuleName();

	String getIfTokenLexerRuleName();

	String getElseTokenLexerRuleName();

	String getIfElseTokenLexerRuleName();

	String getIfCloseTokenLexerRuleName();

	String getListTokenLexerRuleName();

	String getListCloseTokenLexerRuleName();
}
