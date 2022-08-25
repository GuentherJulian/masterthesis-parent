package io.github.guentherjulian.masterthesis.patterndetection.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.antlr.parser.cfreemarkertemplate.CFreemarkerTemplateLexer;
import org.antlr.parser.cfreemarkertemplate.CFreemarkerTemplateParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.patterndetection.aimpattern.AimPattern;
import io.github.guentherjulian.masterthesis.patterndetection.aimpattern.AimPatternTemplate;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage.FreeMarkerLexerRuleNames;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage.FreeMarkerMetaLanguagePattern;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage.MetaLanguageConfiguration;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage.MetaLanguageLexerRules;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage.MetaLanguagePattern;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.objectlanguage.CLanguageConfiguration;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.objectlanguage.ObjectLanguageConfiguration;
import io.github.guentherjulian.masterthesis.patterndetection.engine.matching.TreeMatch;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.FreeMarkerPlaceholderResolver;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.PlaceholderResolver;
import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.FreeMarkerTemplatePreprocessor;
import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.TemplatePreprocessor;

public class CFreemarkerAimPatternDetectionEngineTest extends AbstractAimPatternDetectionEngineTest {

	private final String metaLangPrefix = "fm_";
	private MetaLanguagePattern metaLanguagePattern = new FreeMarkerMetaLanguagePattern();
	private MetaLanguageLexerRules metaLanguageLexerRules = new FreeMarkerLexerRuleNames();
	private MetaLanguageConfiguration metaLanguageConfiguration = new MetaLanguageConfiguration(
			this.metaLanguageLexerRules, this.metaLanguagePattern, metaLangPrefix);
	private ObjectLanguageConfiguration objectLanguageProperties = new CLanguageConfiguration(this.metaLangPrefix);
	private PlaceholderResolver placeholderResolver = new FreeMarkerPlaceholderResolver();
	private TemplatePreprocessor templatePreprocessor = new FreeMarkerTemplatePreprocessor();

	@BeforeAll
	public static void setupTests() throws URISyntaxException {
		templatesPath = resourcesPath.resolve("templates").resolve("c_freemarker");
		compilationUnitsPath = resourcesPath.resolve("compilation-units").resolve("c");
		grammarPath = grammarPath.resolve("cFreemarkerTemplate").resolve("CFreemarkerTemplate.g4");

		parserClass = CFreemarkerTemplateParser.class;
		lexerClass = CFreemarkerTemplateLexer.class;
	}

	@Test
	void cFreeMarkerSimplePlaceholderTransformationTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates
				.add(new AimPatternTemplate(templatesPath.resolve("SimplePlaceholder.c"), "SimplePlaceholder.c"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("CProgramForPlaceholder.c"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
				objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();
		assertEquals(treeMatches.size(), 1);
		assertTrue(treeMatches.get(0).isMatch());
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("var"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("var").contains("foo"));

		long processingTime = patternDetectionResult.getProcessingTime();
		System.out
				.println(String.format("Pattern detection took %s ns, %s ms", processingTime, (processingTime / 1e6)));
	}

	@Test
	void cFreeMarkerSimpleIfConditionTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates
				.add(new AimPatternTemplate(templatesPath.resolve("SimpleIfCondition.c"), "SimpleIfCondition.c"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("CProgramForIfCondition.c"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
				objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();
		assertEquals(treeMatches.size(), 1);
		assertTrue(treeMatches.get(0).isMatch());
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("anything"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("anything").contains("true"));

		long processingTime = patternDetectionResult.getProcessingTime();
		System.out
				.println(String.format("Pattern detection took %s ns, %s ms", processingTime, (processingTime / 1e6)));
	}
}
