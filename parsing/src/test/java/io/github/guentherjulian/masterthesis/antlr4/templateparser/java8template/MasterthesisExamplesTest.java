package io.github.guentherjulian.masterthesis.antlr4.templateparser.java8template;

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
import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.antlr4.AbstractParserTest;
import io.github.guentherjulian.masterthesis.antlr4.java.JavaGrammarParserTest;
import io.github.guentherjulian.masterthesis.antlr4.parser.TemplateParser;

public class MasterthesisExamplesTest extends AbstractParserTest {

	private static Path testResourcesPath;

	private static Path grammar;

	@BeforeAll
	public static void prepare() throws URISyntaxException {
		testResourcesPath = Paths
				.get(MasterthesisExamplesTest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
				.getParent().getParent().resolve("src/test/resources");

		parserClass = Java8FreemarkerTemplateParser.class;
		lexerClass = Java8FreemarkerTemplateLexer.class;

		grammar = Paths.get(JavaGrammarParserTest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
				.getParent().resolve("classes").resolve("grammars/java8FreeMarkerTemplate/Java8FreeMarkerTemplate.g4");
	}

	// Introduction

	@Test
	void introductionSimplePlaceholderTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("masterthesis/IntroductionSimplePlaceholderTemplate.java");

		TemplateParser<Java8Parser> templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void introductionConditionalStatementTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("masterthesis/IntroductionConditionalStatementTemplate.java");

		TemplateParser<Java8Parser> templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void introductionLoopStatementTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("masterthesis/IntroductionLoopStatementTemplate.java");

		TemplateParser<Java8Parser> templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void introductionBuiltInFunctionsTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("masterthesis/IntroductionBuiltInFunctionsTemplate.java");

		TemplateParser<Java8Parser> templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void introductionsimpleAmbiguityTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("masterthesis/IntroductionSimpleAmbiguityTemplate.java");

		TemplateParser<Java8Parser> templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}

		inputFile = testResourcesPath.resolve("masterthesis/IntroductionSimpleAmbiguity.java");
		templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	// Challenges

	@Test
	void challengesPlaceholderSubstitutionsTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("masterthesis/ChallengesPlaceholderSubstitutionsTemplate.java");

		TemplateParser<Java8Parser> templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}

		inputFile = testResourcesPath.resolve("masterthesis/ChallengesPlaceholderSubstitutions.java");
		templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void challengesPlaceholderSubstitutionsComplexTest() throws Exception {
		Path inputFile = testResourcesPath
				.resolve("masterthesis/ChallengesPlaceholderSubstitutionsComplexTemplate.java");

		TemplateParser<Java8Parser> templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}

		inputFile = testResourcesPath.resolve("masterthesis/ChallengesPlaceholderSubstitutionsComplex.java");
		templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void challengesPlaceholderSemanticsTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("masterthesis/ChallengesPlaceholderSemanticsTemplate.java");

		TemplateParser<Java8Parser> templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}

		inputFile = testResourcesPath.resolve("masterthesis/ChallengesPlaceholderSemantics.java");
		templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void challengesPlaceholderStringConcatenationsTest() throws Exception {
		Path inputFile = testResourcesPath
				.resolve("masterthesis/ChallengesPlaceholderStringConcatenationsTemplateCorrect.java");

		TemplateParser<Java8Parser> templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}

		inputFile = testResourcesPath
				.resolve("masterthesis/ChallengesPlaceholderStringConcatenationsTemplateIncorrect.java");
		templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}

		inputFile = testResourcesPath
				.resolve("masterthesis/ChallengesPlaceholderStringConcatenationsTemplateIncorrect2.java");
		templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void challengesSemanticallyEquivalentOrderingTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("masterthesis/ChallengesSemanticEquivalentOrderingTemplate.java");

		TemplateParser<Java8Parser> templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}

		inputFile = testResourcesPath.resolve("masterthesis/ChallengesSemanticEquivalentOrdering.java");
		templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void challengesAlternativesTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("masterthesis/ChallengesAlternativesTemplate.java");

		TemplateParser<Java8Parser> templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}

		inputFile = testResourcesPath.resolve("masterthesis/ChallengesAlternatives1.java");
		templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}

		inputFile = testResourcesPath.resolve("masterthesis/ChallengesAlternatives2.java");
		templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}

		inputFile = testResourcesPath.resolve("masterthesis/ChallengesAlternatives3.java");
		templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void challengesLoopsTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("masterthesis/ChallengesLoopsTemplate.java");

		TemplateParser<Java8Parser> templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}

		inputFile = testResourcesPath.resolve("masterthesis/ChallengesLoops.java");
		templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}

	@Test
	void challengesEfficiencyTest() throws Exception {
		Path inputFile = testResourcesPath.resolve("masterthesis/ChallengesEfficiencyTemplate1.java");

		// 4 parse trees
		TemplateParser<Java8Parser> templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		List<ParserRuleContext> trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}

		// 12 parse trees
		inputFile = testResourcesPath.resolve("masterthesis/ChallengesEfficiencyTemplate2.java");
		templateParser = getTemplateParser("compilationUnit", inputFile, grammar);
		trees = templateParser.parseAmbiguties(PredictionMode.LL);
		for (ParserRuleContext tree : trees) {
			templateParser.showTree(tree);
		}
	}
}
