package io.github.guentherjulian.masterthesis.patterndetection.parsing;

import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguageLexerRules;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguagePattern;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.objectlanguage.ObjectLanguageProperties;

public class ParseTreeTransformer {

	private Vocabulary parserVocabulary;
	private Map<String, List<String>> listPatterns;
	private MetaLanguagePattern metaLanguagePattern;
	private MetaLanguageLexerRules metaLanguageLexerRules;
	private ObjectLanguageProperties objectLanguageProperties;

	public ParseTreeTransformer(Vocabulary parserVocabulary, Map<String, List<String>> listPatterns,
			MetaLanguagePattern metaLanguagePattern, MetaLanguageLexerRules metaLanguageLexerRules,
			ObjectLanguageProperties objectLanguageProperties) {
		this.parserVocabulary = parserVocabulary;
		this.listPatterns = listPatterns;
		this.metaLanguagePattern = metaLanguagePattern;
		this.metaLanguageLexerRules = metaLanguageLexerRules;
		this.objectLanguageProperties = objectLanguageProperties;
	}

	public ParseTree transform(ParserRuleContext parseTree) {
		ParseTreeTransformationListener listener = new ParseTreeTransformationListener(this.parserVocabulary,
				this.listPatterns, this.metaLanguagePattern, this.metaLanguageLexerRules,
				this.objectLanguageProperties);

		ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
		parseTreeWalker.walk(listener, parseTree);

		return listener.getParseTree();
	}
}
