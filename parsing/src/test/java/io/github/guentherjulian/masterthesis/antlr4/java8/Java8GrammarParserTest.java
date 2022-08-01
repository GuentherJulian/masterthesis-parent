package io.github.guentherjulian.masterthesis.antlr4.java8;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.antlr.parser.java8.Java8Lexer;
import org.antlr.parser.java8.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.antlr4.AbstractParserTest;
import io.github.guentherjulian.masterthesis.antlr4.java.JavaGrammarParserTest;
import io.github.guentherjulian.masterthesis.antlr4.parser.TemplateParser;

public class Java8GrammarParserTest extends AbstractParserTest {

	private static Path testResourcesPath;

	private static Path grammar;

	@BeforeAll
	public static void prepare() throws URISyntaxException {
		testResourcesPath = Paths
				.get(Java8GrammarParserTest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
				.getParent().getParent().resolve("src/test/resources");

		parserClass = Java8Parser.class;
		lexerClass = Java8Lexer.class;

		grammar = Paths.get(JavaGrammarParserTest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
				.getParent().resolve("classes").resolve("grammars/java8/Java8Parser.g4");
	}

	@Test
	void java8GrammarTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/malte-thesis/java8_parse_field_snippet.ftl");

		List<ParserRuleContext> trees = parse("compilationUnit", inputFile, PredictionMode.LL, grammar);

		assertNotNull(trees);
		System.out.println(trees.get(0).toStringTree());
	}

	@Test
	void java8GrammarTest1() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/malte-thesis/java8_parse_field_snippet.ftl");

		TemplateParser<Java8Parser> templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		templateParser.showTree(templateParser.parse(PredictionMode.LL));
	}

	@Test
	void java8GrammarComplexPackageDeclTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/java_freemarker/ComplexPackageDeclTemplate.java");

		TemplateParser<Java8Parser> templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void java8SimpleListTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/java_freemarker/SimpleListTemplate.java");

		TemplateParser<Java8Parser> templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}
}
