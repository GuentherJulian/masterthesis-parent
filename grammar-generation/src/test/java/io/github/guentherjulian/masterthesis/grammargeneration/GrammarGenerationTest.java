package io.github.guentherjulian.masterthesis.grammargeneration;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.grammargeneration.generator.GrammarExtenderCore;
import io.github.guentherjulian.masterthesis.grammargeneration.generator.Tactics;

public class GrammarGenerationTest {

	public static Path resourcesPath;
	public static Path grammarPath;
	public static Path destinationPath;
	public static String packageNamePrefix;

	@BeforeAll
	public static void setup() throws URISyntaxException {
		resourcesPath = Paths
				.get(GrammarGenerationTest.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		grammarPath = resourcesPath.resolve("grammars");
		packageNamePrefix = "io.github.guentherjulian.masterthesis.antlr4.templateparser.";
		destinationPath = resourcesPath.getParent().resolve("generated-test-sources/antlr4/");
	}

	@Test
	public void extendJava8Grammar() throws Exception {
		String newGrammarName = "Java8Template";
		String placeHolderName = "PLACEHOLDER";
		String metaLangPrefix = "fm_";
		String targetPackage = packageNamePrefix + "java8";
		Tactics customTactic = Tactics.ALL_PARSER_CUSTOM_LEXER;
		HashSet<String> tokenNames = new HashSet<>();
		tokenNames.add("Identifier");
		customTactic.addTokens(tokenNames);

		Path objectGrammarPath = grammarPath.resolve("Java8.g4");
		Path metalanguageGrammarPath = grammarPath.resolve("SimpleFreeMarker.g4");

		GrammarExtenderCore.extendGrammarAndGenerateParser(objectGrammarPath,
				destinationPath.resolve(targetPackage.replace(".", "/") + "/"), customTactic, metalanguageGrammarPath,
				newGrammarName, metaLangPrefix, placeHolderName, targetPackage, "ANY");
	}
}
