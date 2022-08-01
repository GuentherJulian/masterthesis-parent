package io.github.guentherjulian.masterthesis.antlr4.templateparser.java8template;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.antlr.parser.java8.Java8Parser;
import org.antlr.parser.java8freemarkertemplate.Java8FreemarkerTemplateLexer;
import org.antlr.parser.java8freemarkertemplate.Java8FreemarkerTemplateParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.antlr4.AbstractParserTest;
import io.github.guentherjulian.masterthesis.antlr4.java.JavaGrammarParserTest;
import io.github.guentherjulian.masterthesis.antlr4.parser.TemplateParser;

public class Java8FreemarkerTemplateGrammarParserTest extends AbstractParserTest {

	private static Path testResourcesPath;

	private static Path grammar;

	@BeforeAll
	public static void prepare() throws URISyntaxException {
		testResourcesPath = Paths.get(Java8FreemarkerTemplateGrammarParserTest.class.getProtectionDomain()
				.getCodeSource().getLocation().toURI()).getParent().getParent().resolve("src/test/resources");

		parserClass = Java8FreemarkerTemplateParser.class;
		lexerClass = Java8FreemarkerTemplateLexer.class;

		grammar = Paths.get(JavaGrammarParserTest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
				.getParent().resolve("classes").resolve("grammars/java8FreemarkerTemplate/Java8FreemarkerTemplate.g4");
	}

	@Test
	@Disabled
	void java8GrammarTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/malte-thesis/java8_parse_field_snippet.ftl");

		List<ParserRuleContext> trees = parse("compilationUnit", inputFile, PredictionMode.LL, grammar);

		assertNotNull(trees);
		System.out.println(trees.get(0).toStringTree());
	}

	@Test
	void java8GrammarTest3() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/java_freemarker/SimplePackageDeclTemplate.java");

		TemplateParser<Java8FreemarkerTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void java8GrammarTest4() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/java_freemarker/ComplexPackageDeclTemplate.java");

		TemplateParser<Java8FreemarkerTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}

		trees = templateParser.parseAmbiguties(PredictionMode.SLL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}

		trees = templateParser.parseAmbiguties(PredictionMode.LL_EXACT_AMBIG_DETECTION);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void java8GrammarTest5() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/java_freemarker/SimpleClassWithFieldTemplate.java");

		TemplateParser<Java8FreemarkerTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void java8GrammarTest6() throws Exception {
		Path inputFile = testResourcesPath.resolve("compilation-units/java/SimpleClassWithField1.java");

		TemplateParser<Java8FreemarkerTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void java8GrammarTest7() throws Exception {
		Path inputFile = testResourcesPath.resolve("compilation-units/java/SimpleClassWithField2.java");

		TemplateParser<Java8FreemarkerTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void java8GrammarTest8() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/java_freemarker/SimpleClassWithPlaceholderTemplate.java");

		TemplateParser<Java8FreemarkerTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void java8GrammarTest9() throws Exception {
		Path inputFile = testResourcesPath.resolve("compilation-units/java/SimpleClassWithPlaceholder1.java");

		TemplateParser<Java8FreemarkerTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void java8GrammarTest10() throws Exception {
		Path inputFile = testResourcesPath.resolve("compilation-units/java/SimpleClassWithPlaceholder2.java");

		TemplateParser<Java8FreemarkerTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void java8GrammarTesT11() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/java_freemarker/SimpleClassWithIfAndListTemplate.java");

		TemplateParser<Java8FreemarkerTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void java8GrammarTest12() throws Exception {
		Path inputFile = testResourcesPath.resolve("compilation-units/java/SimpleClassWithIfAndList1.java");

		TemplateParser<Java8FreemarkerTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void java8GrammarTest13() throws Exception {
		Path inputFile = testResourcesPath.resolve("compilation-units/java/SimpleClassWithIfAndList2.java");

		TemplateParser<Java8FreemarkerTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void java8GrammarTest14() throws Exception {
		Path inputFile = testResourcesPath.resolve("compilation-units/java/SimpleClassWithIfAndList3.java");

		TemplateParser<Java8FreemarkerTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void java8GrammarTest15() throws Exception {
		Path inputFile = testResourcesPath.resolve("compilation-units/java/TestClassWithModifier.java");

		TemplateParser<Java8FreemarkerTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
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
