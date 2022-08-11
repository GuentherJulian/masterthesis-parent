package io.github.guentherjulian.masterthesis.patterndetection.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import io.github.guentherjulian.masterthesis.patterndetection.engine.exception.PlaceholderClashException;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.FreeMarkerLexerRuleNames;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.FreeMarkerMetaLanguagePattern;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguageLexerRules;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguagePattern;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.objectlanguage.JavaProperties;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.objectlanguage.ObjectLanguageProperties;
import io.github.guentherjulian.masterthesis.patterndetection.engine.matching.TreeMatch;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.FreeMarkerPlaceholderResolver;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.PlaceholderResolver;

public class JavaFreemarkerAimPatternDetectionEngineTest extends AbstractAimPatternDetectionEngineTest {

	private final String metaLangPrefix = "fm_";
	private MetaLanguagePattern metaLanguagePattern = new FreeMarkerMetaLanguagePattern();
	private MetaLanguageLexerRules metaLanguageLexerRules = new FreeMarkerLexerRuleNames();
	private ObjectLanguageProperties objectLanguageProperties = new JavaProperties(this.metaLangPrefix);
	private PlaceholderResolver placeholderResolver = new FreeMarkerPlaceholderResolver();

	@BeforeAll
	public static void setupTests() throws URISyntaxException {
		templatesPath = resourcesPath.resolve("templates").resolve("java_freemarker");
		compilationUnitsPath = resourcesPath.resolve("compilation-units").resolve("java");
		grammarPath = grammarPath.resolve("java8FreemarkerTemplate").resolve("Java8FreemarkerTemplate.g4");
	}

	@Test
	void javaFreeMarkerSimplePackageDeclarationTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(templatesPath.resolve("SimplePackageDeclTemplate.java"),
				"SimplePackageDeclTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("SimplePackageDecl.java"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, Java8FreemarkerTemplateParser.class, Java8FreemarkerTemplateLexer.class, grammarPath,
				metaLanguagePattern, metaLanguageLexerRules, objectLanguageProperties, this.placeholderResolver);

		List<TreeMatch> treeMatches = aimPatternDetectionEngine.detect();

		assertEquals(treeMatches.size(), 1);
		assertTrue(treeMatches.get(0).isMatch());
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("${c}"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("${c}").contains("c"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("${e}"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("${e}").contains("e"));
	}

	@Test
	void javaFreeMarkerComplexPackageDeclarationTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(templatesPath.resolve("ComplexPackageDeclTemplate.java"),
				"ComplexPackageDeclTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("ComplexPackageDecl.java"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, Java8FreemarkerTemplateParser.class, Java8FreemarkerTemplateLexer.class, grammarPath,
				metaLanguagePattern, metaLanguageLexerRules, objectLanguageProperties, this.placeholderResolver);

		List<TreeMatch> treeMatches = aimPatternDetectionEngine.detect();
	}

	@Test
	void javaFreeMarkerCopyConstructorTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates
				.add(new AimPatternTemplate(templatesPath.resolve("SimpleClassWithCopyConstructorTemplate.java"),
						"SimpleClassWithCopyConstructorTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithCopyConstructorCorrect.java"));
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithCopyConstructorIncorrect.java"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, Java8FreemarkerTemplateParser.class, Java8FreemarkerTemplateLexer.class, grammarPath,
				metaLanguagePattern, metaLanguageLexerRules, objectLanguageProperties, this.placeholderResolver);

		List<TreeMatch> treeMatches = aimPatternDetectionEngine.detect();

		assertEquals(treeMatches.size(), 2);
		assertTrue(treeMatches.get(0).isMatch());
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("${name}"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("${name}").contains("A"));

		assertFalse(treeMatches.get(1).isMatch());
		assertTrue(treeMatches.get(1).getException() instanceof PlaceholderClashException);

		PlaceholderClashException exception = (PlaceholderClashException) treeMatches.get(1).getException();
		assertTrue(exception.getPlaceholder().equals("${name}"));
		assertTrue(exception.getClashSubstitution().equals("B"));
	}

	@Test
	void javaFreeMarkerFieldDeclarationPlaceholderTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates
				.add(new AimPatternTemplate(templatesPath.resolve("SimpleClassWithFieldDeclPlaceholderTemplate.java"),
						"SimpleClassWithFieldDeclPlaceholderTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithFieldDecl1.java"));
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithFieldDecl2.java"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, Java8FreemarkerTemplateParser.class, Java8FreemarkerTemplateLexer.class, grammarPath,
				metaLanguagePattern, metaLanguageLexerRules, objectLanguageProperties, this.placeholderResolver);

		List<TreeMatch> treeMatches = aimPatternDetectionEngine.detect();
	}

	@Test
	void javaFreeMarkerSimpleIfCondition() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(templatesPath.resolve("SimpleClassWithIfTemplate.java"),
				"SimpleClassWithIfTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		// TODO enable
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithIfCorrect1.java"));
		// compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithIfCorrect2.java"));
		// compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithIfIncorrect.java"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, Java8FreemarkerTemplateParser.class, Java8FreemarkerTemplateLexer.class, grammarPath,
				metaLanguagePattern, metaLanguageLexerRules, objectLanguageProperties, this.placeholderResolver);

		List<TreeMatch> treeMatches = aimPatternDetectionEngine.detect();
	}

	@Test
	void javaFreeMarkerComplexFileTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates
				.add(new AimPatternTemplate(templatesPath.resolve("ComplexTemplate.java"), "ComplexTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("ComplexFile.java"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, Java8FreemarkerTemplateParser.class, Java8FreemarkerTemplateLexer.class, grammarPath,
				metaLanguagePattern, metaLanguageLexerRules, objectLanguageProperties, this.placeholderResolver);

		List<TreeMatch> treeMatches = aimPatternDetectionEngine.detect();
	}
}
