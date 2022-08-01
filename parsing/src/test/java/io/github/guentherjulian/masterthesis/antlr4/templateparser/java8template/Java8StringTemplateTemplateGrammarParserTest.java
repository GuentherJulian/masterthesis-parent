package io.github.guentherjulian.masterthesis.antlr4.templateparser.java8template;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.antlr.parser.java8stringtemplatetemplate.Java8StringTemplateTemplateLexer;
import org.antlr.parser.java8stringtemplatetemplate.Java8StringTemplateTemplateParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.antlr4.AbstractParserTest;
import io.github.guentherjulian.masterthesis.antlr4.java.JavaGrammarParserTest;
import io.github.guentherjulian.masterthesis.antlr4.parser.TemplateParser;

public class Java8StringTemplateTemplateGrammarParserTest extends AbstractParserTest {

	private static Path testResourcesPath;

	private static Path grammar;

	@BeforeAll
	public static void prepare() throws URISyntaxException {
		testResourcesPath = Paths.get(Java8StringTemplateTemplateGrammarParserTest.class.getProtectionDomain()
				.getCodeSource().getLocation().toURI()).getParent().getParent().resolve("src/test/resources");

		parserClass = Java8StringTemplateTemplateParser.class;
		lexerClass = Java8StringTemplateTemplateLexer.class;

		grammar = Paths.get(JavaGrammarParserTest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
				.getParent().resolve("classes")
				.resolve("grammars/java8StringTemplateTemplate/Java8StringTemplateTemplate.g4");
	}

	@Test
	void java8VelocitySimplePackageDeclaration() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/java_stringtemplate/SimplePackageDeclTemplate.java");

		TemplateParser<Java8StringTemplateTemplateParser> templateParser = getTemplateParser("compilationUnit",
				inputFile, grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void java8VelocityComplexPackageDeclaration() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/java_stringtemplate/ComplexPackageDeclTemplate.java");

		TemplateParser<Java8StringTemplateTemplateParser> templateParser = getTemplateParser("compilationUnit",
				inputFile, grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void java8VelocitySimpleClassWithField() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/java_stringtemplate/SimpleClassWithFieldTemplate.java");

		TemplateParser<Java8StringTemplateTemplateParser> templateParser = getTemplateParser("compilationUnit",
				inputFile, grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void java8VelocityCopyConstructor() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/java_stringtemplate/CopyConstructorTemplate.java");

		TemplateParser<Java8StringTemplateTemplateParser> templateParser = getTemplateParser("compilationUnit",
				inputFile, grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void java8VelocitySimpleIf() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/java_stringtemplate/SimpleClassWithIfTemplate.java");

		TemplateParser<Java8StringTemplateTemplateParser> templateParser = getTemplateParser("compilationUnit",
				inputFile, grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void java8VelocitySimpleList() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/java_stringtemplate/SimpleListTemplate.java");

		TemplateParser<Java8StringTemplateTemplateParser> templateParser = getTemplateParser("compilationUnit",
				inputFile, grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}
}
