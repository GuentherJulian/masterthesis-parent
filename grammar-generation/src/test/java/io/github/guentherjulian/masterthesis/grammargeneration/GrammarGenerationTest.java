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

        GrammarExtenderCore.extendGrammar(objectGrammarPath,
                destinationPath.resolve(targetPackage.replace(".", "/") + "/"), customTactic, metalanguageGrammarPath,
                newGrammarName, metaLangPrefix, placeHolderName, targetPackage, "ANY");
    }

    @Test
    public void extendCSharpGrammar() throws Exception {
        String newGrammarName = "CSharpTemplate";
        String placeHolderName = "PLACEHOLDER";
        String metaLangPrefix = "fm_";
        String targetPackage = packageNamePrefix + "csharp";
        Tactics customTactic = Tactics.ALL_PARSER_CUSTOM_LEXER;
        HashSet<String> tokenNames = new HashSet<>();
        tokenNames.add("Identifier");
        customTactic.addTokens(tokenNames);

        Path objectGrammarPathLexer = grammarPath.resolve("CSharpLexer.g4");
        Path objectGrammarPathParser = grammarPath.resolve("CSharpParser.g4");
        Path metalanguageGrammarPath = grammarPath.resolve("SimpleFreeMarker.g4");
        Path objectLanguageGrammarCombined = grammarPath.resolve("CSharpCombined.g4");

        GrammarExtenderCore.extendGrammar(objectLanguageGrammarCombined,
                destinationPath.resolve(targetPackage.replace(".", "/") + "/"), customTactic, metalanguageGrammarPath,
                newGrammarName, metaLangPrefix, placeHolderName, targetPackage, "ANY");
    }

    @Test
    public void extendRubyGrammar() throws Exception {
        String newGrammarName = "CorundumTemplate";
        String placeHolderName = "PLACEHOLDER";
        String metaLangPrefix = "fm_";
        String targetPackage = packageNamePrefix + "ruby";
        Tactics customTactic = Tactics.ALL_PARSER_CUSTOM_LEXER;
        HashSet<String> tokenNames = new HashSet<>();
        tokenNames.add("Identifier");
        customTactic.addTokens(tokenNames);

        Path objectGrammarPath = grammarPath.resolve("Corundum.g4");
        Path metalanguageGrammarPath = grammarPath.resolve("SimpleFreeMarker.g4");

        GrammarExtenderCore.extendGrammar(objectGrammarPath,
                destinationPath.resolve(targetPackage.replace(".", "/") + "/"), customTactic, metalanguageGrammarPath,
                newGrammarName, metaLangPrefix, placeHolderName, targetPackage, "ANY");
    }

    @Test
    public void extendCGrammar() throws Exception {
        String newGrammarName = "CTemplate";
        String placeHolderName = "PLACEHOLDER";
        String metaLangPrefix = "fm_";
        String targetPackage = packageNamePrefix + "c";
        Tactics customTactic = Tactics.ALL_PARSER_CUSTOM_LEXER;
        HashSet<String> tokenNames = new HashSet<>();
        tokenNames.add("Identifier");
        customTactic.addTokens(tokenNames);

        Path objectGrammarPath = grammarPath.resolve("C.g4");
        Path metalanguageGrammarPath = grammarPath.resolve("SimpleFreeMarker.g4");

        GrammarExtenderCore.extendGrammar(objectGrammarPath,
                destinationPath.resolve(targetPackage.replace(".", "/") + "/"), customTactic, metalanguageGrammarPath,
                newGrammarName, metaLangPrefix, placeHolderName, targetPackage, "ANY");
    }
}
