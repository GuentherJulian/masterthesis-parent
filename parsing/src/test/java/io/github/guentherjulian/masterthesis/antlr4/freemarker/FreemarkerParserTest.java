package io.github.guentherjulian.masterthesis.antlr4.freemarker;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.antlr.parser.freemarker.FreemarkerLexer;
import org.antlr.parser.freemarker.FreemarkerParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.antlr4.AbstractParserTest;
import io.github.guentherjulian.masterthesis.antlr4.java.JavaGrammarParserTest;
import io.github.guentherjulian.masterthesis.antlr4.java8.Java8GrammarParserTest;
import io.github.guentherjulian.masterthesis.antlr4.parser.TemplateParser;

public class FreemarkerParserTest extends AbstractParserTest {

	private static Path testResourcesPath;

	private static Path grammar;

	@BeforeAll
	public static void prepare() throws URISyntaxException {
		testResourcesPath = Paths
				.get(Java8GrammarParserTest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
				.getParent().getParent().resolve("src/test/resources");

		parserClass = FreemarkerParser.class;
		lexerClass = FreemarkerLexer.class;

		grammar = Paths.get(JavaGrammarParserTest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
				.getParent().resolve("classes").resolve("grammars/freemarker/FreemarkerParser.g4");
	}

	@Test
	void freemarkerParsingTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/freemarker/EntityInterface.java.ftl");

		List<ParserRuleContext> trees = parse("template", inputFile, PredictionMode.LL, grammar);

		assertNotNull(trees);
		System.out.println(trees.get(0).toStringTree());

		TemplateParser<FreemarkerParser> templateParser = getTemplateParser("template", inputFile, grammar);
		trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void freemarkerParsingTest2() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/freemarker/Test.ftl");

		List<ParserRuleContext> trees = parse("template", inputFile, PredictionMode.LL, grammar);

		assertNotNull(trees);
		System.out.println(trees.get(0).toStringTree());

		TemplateParser<FreemarkerParser> templateParser = getTemplateParser("template", inputFile, grammar);
		trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}
}
