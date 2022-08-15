package io.github.guentherjulian.masterthesis.patterndetection.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import io.github.guentherjulian.masterthesis.patterndetection.engine.exception.NoMatchException;
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

public class JavaFreemarkerAimPatternDetectionEngineTest extends AbstractAimPatternDetectionEngineTest {

	private final String metaLangPrefix = "fm_";
	private MetaLanguagePattern metaLanguagePattern = new FreeMarkerMetaLanguagePattern();
	private MetaLanguageLexerRules metaLanguageLexerRules = new FreeMarkerLexerRuleNames();
	private MetaLanguageConfiguration metaLanguageConfiguration = new MetaLanguageConfiguration(
			this.metaLanguageLexerRules, this.metaLanguagePattern);
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
				metaLanguageConfiguration, objectLanguageProperties, this.placeholderResolver);

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
				metaLanguageConfiguration, objectLanguageProperties, this.placeholderResolver);

		List<TreeMatch> treeMatches = aimPatternDetectionEngine.detect();
		assertEquals(treeMatches.size(), 1);
		assertTrue(treeMatches.get(0).isMatch());
		assertEquals(treeMatches.get(0).getPlaceholderSubstitutions().size(), 4);
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("${a}"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("${a}").contains("a"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("${a}").contains("a.b"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("${b}"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("${b}").contains("b.c"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("${b}").contains("c"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("${e}"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("${e}").contains("e"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("${e}").contains("e.f"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("${f}"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("${f}").contains("f.g"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("${f}").contains("g"));

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
				metaLanguageConfiguration, objectLanguageProperties, this.placeholderResolver);

		List<TreeMatch> treeMatches = aimPatternDetectionEngine.detect();

		assertEquals(treeMatches.size(), 2);
		assertTrue(treeMatches.get(0).isMatch());
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("${name}"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("${name}").contains("A"));

		assertFalse(treeMatches.get(1).isMatch());
		assertTrue(treeMatches.get(1).getException() instanceof NoMatchException);
	}

	@Test
	void javaFreeMarkerImportOrderingTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(templatesPath.resolve("ImportOrderingTemplate.java"),
				"ImportOrderingTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("ImportOrdering.java"));
		compilationUnits.add(compilationUnitsPath.resolve("ImportOrderingInvalid.java"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, Java8FreemarkerTemplateParser.class, Java8FreemarkerTemplateLexer.class, grammarPath,
				metaLanguageConfiguration, objectLanguageProperties, this.placeholderResolver);

		List<TreeMatch> treeMatches = aimPatternDetectionEngine.detect();

		assertEquals(treeMatches.size(), 2);
		assertTrue(treeMatches.get(0).isMatch());

		assertFalse(treeMatches.get(1).isMatch());
		assertNotNull(treeMatches.get(1).getException());
		assertTrue(treeMatches.get(1).getException() instanceof NoMatchException);
	}

	@Test
	void javaFreeMarkerImportOrderingWithPlaceholdersTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(
				templatesPath.resolve("ImportOrderingWithPlaceholdersTemplate.java"), "ImportOrderingTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("ImportOrdering.java"));
		compilationUnits.add(compilationUnitsPath.resolve("ImportOrderingInvalid.java"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, Java8FreemarkerTemplateParser.class, Java8FreemarkerTemplateLexer.class, grammarPath,
				metaLanguageConfiguration, objectLanguageProperties, this.placeholderResolver);

		List<TreeMatch> treeMatches = aimPatternDetectionEngine.detect();

		assertEquals(treeMatches.size(), 2);
		assertTrue(treeMatches.get(0).isMatch());
		assertFalse(treeMatches.get(0).getPlaceholderSubstitutions().isEmpty());
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("${a}"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("${a}").contains("a"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("${b}"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("${b}").contains("foobar"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("${c}"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("${c}").contains("MyClass"));

		assertFalse(treeMatches.get(1).isMatch());
		assertNotNull(treeMatches.get(1).getException());
		assertTrue(treeMatches.get(1).getException() instanceof NoMatchException);
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
				metaLanguageConfiguration, objectLanguageProperties, this.placeholderResolver);

		List<TreeMatch> treeMatches = aimPatternDetectionEngine.detect();
		assertEquals(treeMatches.size(), 2);
		assertFalse(treeMatches.get(0).isMatch());
		assertTrue(treeMatches.get(1).isMatch());
	}

	@Test
	void javaFreeMarkerSimpleIfElseCondition() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(templatesPath.resolve("SimpleClassWithIfElseTemplate.java"),
				"SimpleClassWithIfElseTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithIf1.java"));
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithIf2.java"));
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithIf3.java"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, Java8FreemarkerTemplateParser.class, Java8FreemarkerTemplateLexer.class, grammarPath,
				metaLanguageConfiguration, objectLanguageProperties, this.placeholderResolver);

		List<TreeMatch> treeMatches = aimPatternDetectionEngine.detect();
		assertEquals(treeMatches.size(), 3);
		assertTrue(treeMatches.get(0).isMatch());
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("anything"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("anything").contains("true"));

		assertFalse(treeMatches.get(1).isMatch());

		assertTrue(treeMatches.get(2).isMatch());
	}

	@Test
	void javaFreeMarkerSimpleIfElseifCondition() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(templatesPath.resolve("SimpleClassWithIfElseifTemplate.java"),
				"SimpleClassWithIfElseifTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithIf1.java"));
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithIf2.java"));
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithIf3.java"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, Java8FreemarkerTemplateParser.class, Java8FreemarkerTemplateLexer.class, grammarPath,
				metaLanguageConfiguration, objectLanguageProperties, this.placeholderResolver);

		List<TreeMatch> treeMatches = aimPatternDetectionEngine.detect();
		assertEquals(treeMatches.size(), 3);

		assertTrue(treeMatches.get(0).isMatch());
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("anything"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("anything").contains("true"));

		assertTrue(treeMatches.get(1).isMatch());
		assertTrue(treeMatches.get(1).getPlaceholderSubstitutions().containsKey("somethingElse"));
		assertTrue(treeMatches.get(1).getPlaceholderSubstitutions().get("somethingElse").contains("true"));

		assertTrue(treeMatches.get(2).isMatch());
	}

	@Test
	void javaFreeMarkerSimpleIfElseifElseCondition() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates
				.add(new AimPatternTemplate(templatesPath.resolve("SimpleClassWithIfElseifElseTemplate.java"),
						"SimpleClassWithIfElseifElseTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithIf1.java"));
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithIf2.java"));
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithIf3.java"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, Java8FreemarkerTemplateParser.class, Java8FreemarkerTemplateLexer.class, grammarPath,
				metaLanguageConfiguration, objectLanguageProperties, this.placeholderResolver);

		List<TreeMatch> treeMatches = aimPatternDetectionEngine.detect();
		assertEquals(treeMatches.size(), 3);

		assertTrue(treeMatches.get(0).isMatch());
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("anything"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("anything").contains("true"));

		assertTrue(treeMatches.get(1).isMatch());
		assertTrue(treeMatches.get(1).getPlaceholderSubstitutions().containsKey("somethingElse"));
		assertTrue(treeMatches.get(1).getPlaceholderSubstitutions().get("somethingElse").contains("true"));

		assertTrue(treeMatches.get(2).isMatch());
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
				metaLanguageConfiguration, objectLanguageProperties, this.placeholderResolver);

		List<TreeMatch> treeMatches = aimPatternDetectionEngine.detect();
		assertTrue(false);
	}
}
