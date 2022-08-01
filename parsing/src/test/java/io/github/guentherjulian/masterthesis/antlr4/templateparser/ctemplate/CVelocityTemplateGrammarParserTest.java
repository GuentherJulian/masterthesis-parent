package io.github.guentherjulian.masterthesis.antlr4.templateparser.ctemplate;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.antlr.parser.cvelocitytemplate.CVelocityTemplateLexer;
import org.antlr.parser.cvelocitytemplate.CVelocityTemplateParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.antlr4.AbstractParserTest;
import io.github.guentherjulian.masterthesis.antlr4.java.JavaGrammarParserTest;
import io.github.guentherjulian.masterthesis.antlr4.parser.TemplateParser;

public class CVelocityTemplateGrammarParserTest extends AbstractParserTest {

	private static Path testResourcesPath;

	private static Path grammar;

	@BeforeAll
	public static void prepare() throws URISyntaxException {
		testResourcesPath = Paths.get(
				CVelocityTemplateGrammarParserTest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
				.getParent().getParent().resolve("src/test/resources");

		parserClass = CVelocityTemplateParser.class;
		lexerClass = CVelocityTemplateLexer.class;

		grammar = Paths.get(JavaGrammarParserTest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
				.getParent().resolve("classes").resolve("grammars/cVelocityTemplate/CVelocityTemplate.g4");
	}

	@Test
	void cVelocitySimplePlaceholderTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/c_velocity/SimplePlaceholder.c");

		TemplateParser<CVelocityTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void cVelocitySimpleIfConditionTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/c_velocity/SimpleIfCondition.c");

		TemplateParser<CVelocityTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void cVelocitySimpleListTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/c_velocity/SimpleList.c");

		TemplateParser<CVelocityTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}
}
