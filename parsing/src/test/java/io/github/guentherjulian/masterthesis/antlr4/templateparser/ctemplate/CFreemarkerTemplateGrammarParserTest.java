package io.github.guentherjulian.masterthesis.antlr4.templateparser.ctemplate;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.antlr.parser.cfreemarkertemplate.CFreemarkerTemplateLexer;
import org.antlr.parser.cfreemarkertemplate.CFreemarkerTemplateParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.antlr4.AbstractParserTest;
import io.github.guentherjulian.masterthesis.antlr4.java.JavaGrammarParserTest;
import io.github.guentherjulian.masterthesis.antlr4.parser.TemplateParser;

public class CFreemarkerTemplateGrammarParserTest extends AbstractParserTest {

	private static Path testResourcesPath;

	private static Path grammar;

	@BeforeAll
	public static void prepare() throws URISyntaxException {
		testResourcesPath = Paths.get(
				CFreemarkerTemplateGrammarParserTest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
				.getParent().getParent().resolve("src/test/resources");

		parserClass = CFreemarkerTemplateParser.class;
		lexerClass = CFreemarkerTemplateLexer.class;

		grammar = Paths.get(JavaGrammarParserTest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
				.getParent().resolve("classes").resolve("grammars/cFreemarkerTemplate/CFreemarkerTemplate.g4");
	}

	@Test
	void cFreeMarkerSimplePlaceholderTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/c_freemarker/SimplePlaceholder.c");

		TemplateParser<CFreemarkerTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void cFreeMarkerSimpleIfConditionTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/c_freemarker/SimpleIfCondition.c");

		TemplateParser<CFreemarkerTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void cFreeMarkerSimpleListTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/c_freemarker/SimpleList.c");

		TemplateParser<CFreemarkerTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void cFreeMarkerComplexTemplateTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("templates/c_freemarker/ComplexTemplate.c");

		TemplateParser<CFreemarkerTemplateParser> templateParser = getTemplateParser("compilationUnit", inputFile,
				grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}
}
