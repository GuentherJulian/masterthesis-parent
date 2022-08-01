package io.github.guentherjulian.masterthesis.antlr4.templateparser.scalatemplate;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.antlr.parser.scala.ScalaLexer;
import org.antlr.parser.scala.ScalaParser;
import org.antlr.parser.scalavelocitytemplate.ScalaVelocityTemplateLexer;
import org.antlr.parser.scalavelocitytemplate.ScalaVelocityTemplateParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.antlr4.AbstractParserTest;
import io.github.guentherjulian.masterthesis.antlr4.java.JavaGrammarParserTest;
import io.github.guentherjulian.masterthesis.antlr4.parser.TemplateParser;

public class ScalaVelocityTemplateGrammarParserTest extends AbstractParserTest {

	private static Path testResourcesPath;

	private static Path grammar;

	@BeforeAll
	public static void prepare() throws URISyntaxException {
		testResourcesPath = Paths.get(ScalaVelocityTemplateGrammarParserTest.class.getProtectionDomain().getCodeSource()
				.getLocation().toURI()).getParent().getParent().resolve("src/test/resources");

		parserClass = ScalaVelocityTemplateParser.class;
		lexerClass = ScalaLexer.class;

		grammar = Paths.get(JavaGrammarParserTest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
				.getParent().resolve("classes").resolve("grammars/scalaVelocityTemplate/ScalaVelocityTemplate.g4");
	}

	@Test
	void scalaVelocitySimplePlaceholderTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/scala_velocity/SimplePlaceholder.scala");

		TemplateParser<ScalaVelocityTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void scalaVelocitySimpleIfConditionTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/scala_velocity/SimpleIfCondition.scala");

		TemplateParser<ScalaVelocityTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void scalaVelocitySimpleListTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/scala_velocity/SimpleList.scala");

		TemplateParser<ScalaVelocityTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}
	
	@Test
	void scalaTest1() throws Exception {
		Path inputFile = testResourcesPath.resolve("compilation-units/scala/Test.scala");

		TemplateParser<ScalaParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}
}
