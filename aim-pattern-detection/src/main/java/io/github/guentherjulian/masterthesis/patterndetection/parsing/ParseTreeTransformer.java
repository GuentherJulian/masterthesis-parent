package io.github.guentherjulian.masterthesis.patterndetection.parsing;

import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage.MetaLanguageLexerRules;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.objectlanguage.ObjectLanguageConfiguration;

public class ParseTreeTransformer {

	private Vocabulary parserVocabulary;
	private Map<String, List<String>> listPatterns;
	private MetaLanguageLexerRules metaLanguageLexerRules;
	private ObjectLanguageConfiguration objectLanguageProperties;

	public ParseTreeTransformer(Vocabulary parserVocabulary, Map<String, List<String>> listPatterns,
			MetaLanguageLexerRules metaLanguageLexerRules, ObjectLanguageConfiguration objectLanguageProperties) {
		this.parserVocabulary = parserVocabulary;
		this.listPatterns = listPatterns;
		this.metaLanguageLexerRules = metaLanguageLexerRules;
		this.objectLanguageProperties = objectLanguageProperties;
	}

	public ParseTree transform(ParserRuleContext parseTree) {
		ParseTreeTransformationListener listener = new ParseTreeTransformationListener(this.parserVocabulary,
				this.listPatterns, this.metaLanguageLexerRules, this.objectLanguageProperties);

		ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
		parseTreeWalker.walk(listener, parseTree);

		return listener.getParseTree();
	}
}
