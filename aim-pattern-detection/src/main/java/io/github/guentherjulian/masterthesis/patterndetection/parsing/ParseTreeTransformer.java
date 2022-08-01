package io.github.guentherjulian.masterthesis.patterndetection.parsing;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class ParseTreeTransformer {

	private Vocabulary parserVocabulary;

	public ParseTreeTransformer(Vocabulary parserVocabulary) {
		this.parserVocabulary = parserVocabulary;
	}

	public ParseTreeRepresentation transform(ParserRuleContext parseTree) {
		ParseTreeTransformationListener listener = new ParseTreeTransformationListener();

		ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
		parseTreeWalker.walk(listener, parseTree);

		return null;
	}
}
