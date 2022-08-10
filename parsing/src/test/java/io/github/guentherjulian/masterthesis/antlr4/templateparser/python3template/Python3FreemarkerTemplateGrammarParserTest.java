package io.github.guentherjulian.masterthesis.antlr4.templateparser.python3template;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.antlr.parser.cfreemarkertemplate.CFreemarkerTemplateParser;
import org.antlr.parser.python3.Python3FreemarkerTemplate;
import org.antlr.parser.python3.Python3Lexer;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.antlr4.AbstractParserTest;
import io.github.guentherjulian.masterthesis.antlr4.java.JavaGrammarParserTest;
import io.github.guentherjulian.masterthesis.antlr4.parser.TemplateParser;

public class Python3FreemarkerTemplateGrammarParserTest extends AbstractParserTest {

	private static Path testResourcesPath;

	private static Path grammar;

	@BeforeAll
	public static void prepare() throws URISyntaxException {
		testResourcesPath = Paths.get(Python3FreemarkerTemplateGrammarParserTest.class.getProtectionDomain()
				.getCodeSource().getLocation().toURI()).getParent().getParent().resolve("src/test/resources");

		parserClass = Python3FreemarkerTemplate.class;
		lexerClass = Python3Lexer.class;

		grammar = Paths.get(JavaGrammarParserTest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
				.getParent().resolve("classes").resolve("grammars/python3/Python3FreemarkerTemplate.g4");
	}

	@Test
	void python3FreeMarkerSimpleTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("compilation-units/python3/helloworld.py");

		TemplateParser<CFreemarkerTemplateParser> templateParser = getTemplateParser("file_input", inputFile, grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}
}
