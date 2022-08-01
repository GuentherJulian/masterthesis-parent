package io.github.guentherjulian.masterthesis.antlr4.templateparser.java8template;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.antlr.parser.java8velocitytemplate.Java8VelocityTemplateLexer;
import org.antlr.parser.java8velocitytemplate.Java8VelocityTemplateParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.antlr4.AbstractParserTest;
import io.github.guentherjulian.masterthesis.antlr4.java.JavaGrammarParserTest;
import io.github.guentherjulian.masterthesis.antlr4.parser.TemplateParser;

public class Java8VelocityTemplateGrammarParserTest extends AbstractParserTest {

	private static Path testResourcesPath;

	private static Path grammar;

	@BeforeAll
	public static void prepare() throws URISyntaxException {
		testResourcesPath = Paths.get(Java8VelocityTemplateGrammarParserTest.class.getProtectionDomain().getCodeSource()
				.getLocation().toURI()).getParent().getParent().resolve("src/test/resources");

		parserClass = Java8VelocityTemplateParser.class;
		lexerClass = Java8VelocityTemplateLexer.class;

		grammar = Paths.get(JavaGrammarParserTest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
				.getParent().resolve("classes").resolve("grammars/java8VelocityTemplate/Java8VelocityTemplate.g4");
	}

	@Test
	void java8VelocitySimplePackageDeclaration() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/java_velocity/SimplePackageDeclTemplate.java");

		TemplateParser<Java8VelocityTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void java8VelocityComplexPackageDeclaration() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/java_velocity/ComplexPackageDeclTemplate.java");

		TemplateParser<Java8VelocityTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void java8VelocitySimpleClassWithField() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/java_velocity/SimpleClassWithFieldTemplate.java");

		TemplateParser<Java8VelocityTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void java8VelocityCopyConstructor() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/java_velocity/CopyConstructorTemplate.java");

		TemplateParser<Java8VelocityTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void java8VelocitySimpleIf() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/java_velocity/SimpleClassWithIfTemplate.java");

		TemplateParser<Java8VelocityTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void java8VelocitySimpleList() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/java_velocity/SimpleListTemplate.java");

		TemplateParser<Java8VelocityTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}
}
