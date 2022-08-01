package io.github.guentherjulian.masterthesis.antlr4;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;

import io.github.guentherjulian.masterthesis.antlr4.parser.TemplateParser;

public abstract class AbstractParserTest {

	protected static Class<? extends Parser> parserClass;

	protected static Class<? extends Lexer> lexerClass;

	public List<ParserRuleContext> parse(String parseRule, Path input, PredictionMode predictionMode, Path grammar)
			throws Exception {
		TemplateParser<Parser> templateParser = getTemplateParser(parseRule, input, grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(predictionMode);
		return trees;
	}

	public TemplateParser getTemplateParser(String parseRule, Path input, Path grammar) throws Exception {
		InputStream inputStream = new FileInputStream(input.toFile());
		CharStream charStream = CharStreams.fromStream(inputStream);

		Lexer lexer = lexerClass.getDeclaredConstructor(CharStream.class).newInstance(charStream);
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		Parser parser = parserClass.getDeclaredConstructor(TokenStream.class).newInstance(tokens);

		InputStream grammarInputStream = Files.newInputStream(grammar);
		TemplateParser<Parser> templateParser = new TemplateParser<Parser>(parser,
				parser.getClass().getMethod(parseRule), grammarInputStream);
		return templateParser;
	}
}
