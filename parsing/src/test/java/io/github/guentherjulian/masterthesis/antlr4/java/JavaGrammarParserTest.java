package io.github.guentherjulian.masterthesis.antlr4.java;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.antlr.parser.java.JavaLexer;
import org.antlr.parser.java.JavaParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.antlr4.AbstractParserTest;

public class JavaGrammarParserTest extends AbstractParserTest {

	private static Path testResourcesPath;

	private static Path grammar;

	@BeforeAll
	public static void prepare() throws URISyntaxException {
		testResourcesPath = Paths
				.get(JavaGrammarParserTest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
				.getParent().getParent().resolve("src/test/resources");

		parserClass = JavaParser.class;
		lexerClass = JavaLexer.class;

		grammar = Paths.get(JavaGrammarParserTest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
				.getParent().resolve("classes").resolve("grammars/java/JavaParser.g4");
	}

	@Test
	void java8GrammarTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/malte-thesis/java8_parse_field_snippet.ftl");

		List<ParserRuleContext> trees = parse("compilationUnit", inputFile, PredictionMode.LL, grammar);

		assertNotNull(trees);
	}
}
