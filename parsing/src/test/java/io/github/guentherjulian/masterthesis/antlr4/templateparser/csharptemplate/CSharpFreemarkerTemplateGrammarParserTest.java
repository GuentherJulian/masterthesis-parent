package io.github.guentherjulian.masterthesis.antlr4.templateparser.csharptemplate;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.antlr.parser.csharp.CSharpParser;
import org.antlr.parser.csharpcombined.CSharpLexerCombined;
import org.antlr.parser.csharpcombined.CSharpParserCombined;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.antlr4.AbstractParserTest;
import io.github.guentherjulian.masterthesis.antlr4.java.JavaGrammarParserTest;
import io.github.guentherjulian.masterthesis.antlr4.parser.TemplateParser;

public class CSharpFreemarkerTemplateGrammarParserTest extends AbstractParserTest {

	private static Path testResourcesPath;

	private static Path grammar;

	@BeforeAll
	public static void prepare() throws URISyntaxException {
		testResourcesPath = Paths.get(CSharpFreemarkerTemplateGrammarParserTest.class.getProtectionDomain()
				.getCodeSource().getLocation().toURI()).getParent().getParent().resolve("src/test/resources");

		parserClass = CSharpParserCombined.class;
		lexerClass = CSharpLexerCombined.class;

		grammar = Paths.get(JavaGrammarParserTest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
				.getParent().resolve("classes").resolve("grammars/csharp/CSharpParser.g4");
	}

	@Test
	void csharpWithoutMetalanguageTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("compilation-units/csharp/HelloWorld.cs");

		TemplateParser<CSharpParser> templateParser = getTemplateParser("compilation_unit", inputFile, grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void csharpFreeMarkerSimpleTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("compilation-units/csharp/HelloWorld.cs");

		TemplateParser<CSharpParserCombined> templateParser = getTemplateParser("compilation_unit", inputFile, grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}
}
