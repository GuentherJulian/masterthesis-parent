package io.github.guentherjulian.masterthesis.patterndetection.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.antlr.parser.java8freemarkertemplate.Java8FreemarkerTemplateLexer;
import org.antlr.parser.java8freemarkertemplate.Java8FreemarkerTemplateParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.patterndetection.aimpattern.AimPattern;
import io.github.guentherjulian.masterthesis.patterndetection.aimpattern.AimPatternTemplate;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage.CustomLexerRuleNames;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage.CustomMetaLanguagePattern;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage.MetaLanguageConfiguration;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.objectlanguage.JavaLanguageConfiguration;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.objectlanguage.ObjectLanguageConfiguration;
import io.github.guentherjulian.masterthesis.patterndetection.engine.matching.TreeMatch;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.FreeMarkerPlaceholderResolver;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.PlaceholderResolver;
import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.FreeMarkerTemplatePreprocessor;
import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.TemplatePreprocessor;
import io.github.guentherjulian.masterthesis.patterndetection.exception.InvalidMetalanguageConfigurationException;

public class CustomConfigurationPatternDetectionEngineTest extends AbstractAimPatternDetectionEngineTest {

	private final String metaLangPrefix = "fm_";
	private ObjectLanguageConfiguration objectLanguageProperties = new JavaLanguageConfiguration(this.metaLangPrefix);
	private PlaceholderResolver placeholderResolver = new FreeMarkerPlaceholderResolver();
	private MetaLanguageConfiguration metaLanguageConfiguration;
	private Path configurationPath;
	private TemplatePreprocessor templatePreprocessor = new FreeMarkerTemplatePreprocessor();

	@BeforeAll
	public static void setupTests() throws URISyntaxException, InvalidMetalanguageConfigurationException {
		templatesPath = resourcesPath.resolve("templates").resolve("java_freemarker");
		compilationUnitsPath = resourcesPath.resolve("compilation-units").resolve("java");
		grammarPath = grammarPath.resolve("java8FreemarkerTemplate").resolve("Java8FreemarkerTemplate.g4");
	}

	@Test
	void validConfigurationTest() {
		configurationPath = resourcesPath.resolve("configuration").resolve("customConfigurationTest")
				.resolve("customMetalanguageConfiguration.properties");

		Exception exception = null;
		try {
			metaLanguageConfiguration = new MetaLanguageConfiguration(configurationPath);
		} catch (InvalidMetalanguageConfigurationException e) {
			exception = e;
		}

		assertNull(exception);
		assertTrue(metaLanguageConfiguration.getMetaLanguageLexerRules() instanceof CustomLexerRuleNames);
		assertTrue(metaLanguageConfiguration.getMetaLanguagePattern() instanceof CustomMetaLanguagePattern);
	}

	@Test
	void invalidConfigurationTest() {
		configurationPath = resourcesPath.resolve("configuration").resolve("customConfigurationTest")
				.resolve("customMetalanguageConfigurationInvalid.properties");

		Exception exception = null;
		try {
			metaLanguageConfiguration = new MetaLanguageConfiguration(configurationPath);
		} catch (InvalidMetalanguageConfigurationException e) {
			exception = e;
		}

		assertNotNull(exception);
		assertTrue(exception instanceof InvalidMetalanguageConfigurationException);
	}

	@Test
	void simplePackageDeclarationTestWithCustomConfiguration() throws Exception {

		configurationPath = resourcesPath.resolve("configuration").resolve("customConfigurationTest")
				.resolve("customMetalanguageConfiguration.properties");
		metaLanguageConfiguration = new MetaLanguageConfiguration(configurationPath);

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(templatesPath.resolve("SimplePackageDeclTemplate.java"),
				"SimplePackageDeclTemplate.java"));

		AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("SimplePackageDecl.java"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPattern,
				compilationUnits, Java8FreemarkerTemplateParser.class, Java8FreemarkerTemplateLexer.class, grammarPath,
				metaLanguageConfiguration, objectLanguageProperties, this.placeholderResolver,
				this.templatePreprocessor);
		aimPatternDetectionEngine.setForceMatching(true);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();

		assertTrue(metaLanguageConfiguration.getMetaLanguageLexerRules() instanceof CustomLexerRuleNames);
		assertTrue(metaLanguageConfiguration.getMetaLanguagePattern() instanceof CustomMetaLanguagePattern);

		assertEquals(treeMatches.size(), 1);
		assertTrue(treeMatches.get(0).isMatch());
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("c"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("c").contains("c"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("e"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("e").contains("e"));
	}
}
