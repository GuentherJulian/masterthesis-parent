package io.github.guentherjulian.masterthesis.antlr4.templateparser.scalatemplate;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.antlr.parser.scalafreemarkertemplate.ScalaFreemarkerTemplateLexer;
import org.antlr.parser.scalafreemarkertemplate.ScalaFreemarkerTemplateParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.antlr4.AbstractParserTest;
import io.github.guentherjulian.masterthesis.antlr4.java.JavaGrammarParserTest;
import io.github.guentherjulian.masterthesis.antlr4.parser.TemplateParser;

public class ScalaFreemarkerTemplateGrammarParserTest extends AbstractParserTest {

	private static Path testResourcesPath;

	private static Path grammar;

	@BeforeAll
	public static void prepare() throws URISyntaxException {
		testResourcesPath = Paths.get(ScalaFreemarkerTemplateGrammarParserTest.class.getProtectionDomain()
				.getCodeSource().getLocation().toURI()).getParent().getParent().resolve("src/test/resources");

		parserClass = ScalaFreemarkerTemplateParser.class;
		lexerClass = ScalaFreemarkerTemplateLexer.class;

		grammar = Paths.get(JavaGrammarParserTest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
				.getParent().resolve("classes").resolve("grammars/scalaFreemarkerTemplate/ScalaFreemarkerTemplate.g4");
	}

	@Test
	void scalaFreeMarkerSimplePlaceholderTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/scala_freemarker/SimplePlaceholder.scala");

		TemplateParser<ScalaFreemarkerTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void scalaFreeMarkerSimpleIfConditionTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/scala_freemarker/SimpleIfCondition.scala");

		TemplateParser<ScalaFreemarkerTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void scalaFreeMarkerSimpleListTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/scala_freemarker/SimpleList.scala");

		TemplateParser<ScalaFreemarkerTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}
}
