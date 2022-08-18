package io.github.guentherjulian.masterthesis.patterndetection.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.antlr.parser.java8velocitytemplate.Java8VelocityTemplateLexer;
import org.antlr.parser.java8velocitytemplate.Java8VelocityTemplateParser;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.patterndetection.aimpattern.AimPattern;
import io.github.guentherjulian.masterthesis.patterndetection.aimpattern.AimPatternTemplate;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.FreeMarkerLexerRuleNames;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.FreeMarkerMetaLanguagePattern;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguageConfiguration;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguageLexerRules;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguagePattern;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.objectlanguage.JavaProperties;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.objectlanguage.ObjectLanguageProperties;
import io.github.guentherjulian.masterthesis.patterndetection.engine.matching.TreeMatch;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.FreeMarkerPlaceholderResolver;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.PlaceholderResolver;
import io.github.guentherjulian.masterthesis.patterndetection.engine.utils.PathUtil;

public class CompletePatternDetectionTest extends AbstractAimPatternDetectionEngineTest {

	@Test
	void completePatternDetectionTestJavaFreeMarker() throws Exception {
		Class<? extends Parser> parserClass = Java8VelocityTemplateParser.class;
		Class<? extends Lexer> lexerClass = Java8VelocityTemplateLexer.class;

		Path grammarPath = resourcesPath.resolve("grammars").resolve("java8FreemarkerTemplate")
				.resolve("Java8FreemarkerTemplate.g4");
		Path applicationCodeProjectPath = resourcesPath.resolve("completePatternDetectionTest")
				.resolve("applicationCode").resolve("java").resolve("testproject");
		Path templatesPath = resourcesPath.resolve("completePatternDetectionTest").resolve("templates")
				.resolve("java_freemarker");

		String metaLangPrefix = "fm_";
		MetaLanguagePattern metaLanguagePattern = new FreeMarkerMetaLanguagePattern();
		MetaLanguageLexerRules metaLanguageLexerRules = new FreeMarkerLexerRuleNames();
		MetaLanguageConfiguration metaLanguageConfiguration = new MetaLanguageConfiguration(metaLanguageLexerRules,
				metaLanguagePattern);
		ObjectLanguageProperties objectLanguageProperties = new JavaProperties(metaLangPrefix);
		PlaceholderResolver placeholderResolver = new FreeMarkerPlaceholderResolver();

		// the actual test
		List<AimPatternTemplate> aimPatternTemplates = PathUtil.getAimPatternTemplates(templatesPath);
		AimPattern aimPattern = new AimPattern(aimPatternTemplates);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = PathUtil.getAllFiles(applicationCodeProjectPath, ".+\\.java");

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
				objectLanguageProperties, placeholderResolver);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		long processingTime = patternDetectionResult.getProcessingTime();
		System.out
				.println(String.format("Pattern detection took %s ns, %s ms", processingTime, (processingTime / 1e6)));
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();

		assertEquals(treeMatches.size(), 9);
		assertTrue(false);
	}
}
