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
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage.FreeMarkerLexerRuleNames;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage.FreeMarkerMetaLanguagePattern;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage.MetaLanguageConfiguration;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage.MetaLanguageLexerRules;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage.MetaLanguagePattern;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.objectlanguage.JavaLanguageConfiguration;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.objectlanguage.ObjectLanguageConfiguration;
import io.github.guentherjulian.masterthesis.patterndetection.engine.matching.TreeMatch;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.FreeMarkerPlaceholderResolver;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.PlaceholderResolver;
import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.FreeMarkerTemplatePreprocessor;
import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.TemplatePreprocessor;

public class JavaFreemarkerAimPatternDetectionEngineTest extends AbstractAimPatternDetectionEngineTest {

	private final String metaLangPrefix = "fm_";
	private MetaLanguagePattern metaLanguagePattern = new FreeMarkerMetaLanguagePattern();
	private MetaLanguageLexerRules metaLanguageLexerRules = new FreeMarkerLexerRuleNames();
	private MetaLanguageConfiguration metaLanguageConfiguration = new MetaLanguageConfiguration(
			this.metaLanguageLexerRules, this.metaLanguagePattern, this.metaLangPrefix);
	private ObjectLanguageConfiguration objectLanguageProperties = new JavaLanguageConfiguration(this.metaLangPrefix);
	private PlaceholderResolver placeholderResolver = new FreeMarkerPlaceholderResolver();
	private TemplatePreprocessor templatePreprocessor = new FreeMarkerTemplatePreprocessor();

	@BeforeAll
	public static void setupTests() throws URISyntaxException {
		templatesPath = resourcesPath.resolve("templates").resolve("java_freemarker");
		compilationUnitsPath = resourcesPath.resolve("compilation-units").resolve("java");
		grammarPath = grammarPath.resolve("java8FreemarkerTemplate").resolve("Java8FreemarkerTemplate.g4");

		parserClass = Java8FreemarkerTemplateParser.class;
		lexerClass = Java8FreemarkerTemplateLexer.class;
	}

	@Test
	void javaFreeMarkerSimplePackageDeclarationTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(templatesPath.resolve("SimplePackageDeclTemplate.java"),
				"SimplePackageDeclTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("SimplePackageDecl.java"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
				objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();

		assertEquals(treeMatches.size(), 1);
		assertTrue(treeMatches.get(0).isMatch());
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("c"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("c").contains("c"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("e"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("e").contains("e"));
	}

	@Test
	void javaFreeMarkerComplexPackageDeclarationTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(templatesPath.resolve("ComplexPackageDeclTemplate.java"),
				"ComplexPackageDeclTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("ComplexPackageDecl.java"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
				objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();
		assertEquals(treeMatches.size(), 1);
		assertTrue(treeMatches.get(0).isMatch());
		assertEquals(treeMatches.get(0).getPlaceholderSubstitutions().size(), 4);
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("a"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("a").contains("a"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("a").contains("a.b"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("b"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("b").contains("b.c"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("b").contains("c"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("e"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("e").contains("e"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("e").contains("e.f"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("f"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("f").contains("f.g"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("f").contains("g"));
	}

	@Test
	void javaFreeMarkerSimplePlaceholderTransformationTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates
				.add(new AimPatternTemplate(templatesPath.resolve("SimplePlaceholderTransformationTemplate.java"),
						"SimplePlaceholderTransformationTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("SimplePlaceholderTransformation.java"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
				objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();
		assertEquals(treeMatches.size(), 1);
		assertTrue(treeMatches.get(0).isMatch());
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("className"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("className").contains("Foo"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("className").contains("foo"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("var1"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("var1").contains("foofoo"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("var2?non_existing_function"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("var2?non_existing_function").contains("test"));
	}

	@Test
	void javaFreeMarkerCopyConstructorTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates
				.add(new AimPatternTemplate(templatesPath.resolve("SimpleClassWithCopyConstructorTemplate.java"),
						"SimpleClassWithCopyConstructorTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithCopyConstructorCorrect.java"));
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithCopyConstructorIncorrect.java"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
				objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();

		assertEquals(treeMatches.size(), 2);
		assertTrue(treeMatches.get(0).isMatch());
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("name"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("name").contains("A"));

		assertFalse(treeMatches.get(1).isMatch());
	}

	@Test
	void javaFreeMarkerImportOrderingTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(templatesPath.resolve("ImportOrderingTemplate.java"),
				"ImportOrderingTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("ImportOrdering.java"));
		compilationUnits.add(compilationUnitsPath.resolve("ImportOrderingInvalid.java"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
				objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();

		assertEquals(treeMatches.size(), 2);
		assertTrue(treeMatches.get(0).isMatch());

		assertFalse(treeMatches.get(1).isMatch());
	}

	@Test
	void javaFreeMarkerImportOrderingWithPlaceholdersTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(
				templatesPath.resolve("ImportOrderingWithPlaceholdersTemplate.java"), "ImportOrderingTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("ImportOrdering.java"));
		compilationUnits.add(compilationUnitsPath.resolve("ImportOrderingInvalid.java"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
				objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();

		assertEquals(treeMatches.size(), 2);
		assertTrue(treeMatches.get(0).isMatch());
		assertFalse(treeMatches.get(0).getPlaceholderSubstitutions().isEmpty());
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("a"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("a").contains("a"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("b"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("b").contains("foobar"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("c"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("c").contains("MyClass"));

		assertFalse(treeMatches.get(1).isMatch());
	}

	@Test
	void javaFreeMarkerFieldDeclarationPlaceholderTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates
				.add(new AimPatternTemplate(templatesPath.resolve("SimpleClassWithFieldDeclPlaceholderTemplate.java"),
						"SimpleClassWithFieldDeclPlaceholderTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithFieldDecl1.java"));
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithFieldDecl2.java"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
				objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();
		assertEquals(treeMatches.size(), 2);
		assertTrue(treeMatches.get(0).isMatch()); // match. placeholder not matched, but okay since it is optional
		assertTrue(treeMatches.get(1).isMatch());
	}

	@Test
	void javaFreeMarkerSimpleIfElseCondition() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(templatesPath.resolve("SimpleClassWithIfElseTemplate.java"),
				"SimpleClassWithIfElseTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithIf1.java"));
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithIf2.java"));
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithIf3.java"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
				objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();
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
		AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithIf1.java"));
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithIf2.java"));
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithIf3.java"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
				objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();
		assertEquals(treeMatches.size(), 3);

		assertTrue(treeMatches.get(0).isMatch());
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("anything"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("anything").contains("true"));

		assertTrue(treeMatches.get(1).isMatch());
		assertTrue(treeMatches.get(1).getPlaceholderSubstitutions().containsKey("somethingElse"));
		assertTrue(treeMatches.get(1).getPlaceholderSubstitutions().get("somethingElse").contains("true"));

		// True, since its optional
		assertTrue(treeMatches.get(2).isMatch());
	}

	@Test
	void javaFreeMarkerSimpleIfElseifElseCondition() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates
				.add(new AimPatternTemplate(templatesPath.resolve("SimpleClassWithIfElseifElseTemplate.java"),
						"SimpleClassWithIfElseifElseTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithIf1.java"));
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithIf2.java"));
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithIf3.java"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
				objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();
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
	void javaFreeMarkerSimpleList() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(
				new AimPatternTemplate(templatesPath.resolve("SimpleListTemplate.java"), "SimpleListTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("SimpleList.java"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
				objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();
		assertEquals(treeMatches.size(), 1);

		assertTrue(treeMatches.get(0).isMatch());
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("fields"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("fields").contains("a"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("fields").contains("b"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("fields").contains("c"));
	}

	@Test
	void javaFreeMarkerComplexFileTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates
				.add(new AimPatternTemplate(templatesPath.resolve("ComplexTemplate.java"), "ComplexTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("ComplexFile.java"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
				objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();
		assertEquals(treeMatches.size(), 1);
		assertTrue(treeMatches.get(0).isMatch());
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("className"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("className").contains("Foo"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("str"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("str").contains("str"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("getter"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("getter").contains("true"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().containsKey("anything"));
		assertTrue(treeMatches.get(0).getPlaceholderSubstitutions().get("anything").contains("false"));
	}

	@Test
	void javaFreeMarkerIfTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(templatesPath.resolve("If.java"), "If.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("SimpleClassWithIf2.java"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
				objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();
		assertEquals(treeMatches.size(), 1);
	}

	// Tests with real templates

	@Test
	void javaFreeMarkerEntityInterfaceTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(
				templatesPath.resolve("real_templates").resolve("${variables.entityName}.java.ftl"),
				"${variables.entityName}.java.ftl"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		// compilationUnits.add(compilationUnitsPath.resolve("real_compilation_units").resolve("Queue.java"));
		compilationUnits.add(compilationUnitsPath.resolve("real_compilation_units").resolve("Test.java"));

		this.templatePreprocessor.setTemplatesRootPath(templatesPath);
		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
				objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();
		assertEquals(treeMatches.size(), 1);
		assertTrue(treeMatches.get(0).isMatch());
	}

	@Test
	void javaFreeMarkerEntityImplementationTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(
				templatesPath.resolve("real_templates").resolve("${variables.entityName}Entity.java.ftl"),
				"${variables.entityName}Entity.java.ftl"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		// compilationUnits.add(compilationUnitsPath.resolve("real_compilation_units").resolve("QueueEntity.java"));
		compilationUnits.add(compilationUnitsPath.resolve("real_compilation_units").resolve("TestEntity.java"));

		this.templatePreprocessor.setTemplatesRootPath(templatesPath);
		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
				objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();
		assertEquals(treeMatches.size(), 1);
		assertTrue(treeMatches.get(0).isMatch());
	}

	@Test
	void javaFreeMarkerEtoTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(
				templatesPath.resolve("real_templates").resolve("${variables.entityName}Eto.java.ftl"),
				"${variables.entityName}Eto.java.ftl"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("real_compilation_units").resolve("QueueEto.java"));

		this.templatePreprocessor.setTemplatesRootPath(templatesPath);
		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
				objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();
		assertEquals(treeMatches.size(), 1);
		assertTrue(treeMatches.get(0).isMatch());
	}

	@Test
	void javaFreeMarkerRepositoryTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(
				templatesPath.resolve("real_templates").resolve("${variables.entityName}Repository.java.ftl"),
				"${variables.entityName}Repository.java.ftl"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("real_compilation_units").resolve("QueueRepository.java"));

		this.templatePreprocessor.setTemplatesRootPath(templatesPath);
		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
				objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();
		assertEquals(treeMatches.size(), 1);
		assertTrue(treeMatches.get(0).isMatch());
	}

	@Test
	void javaFreeMarkerRestServiceTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(
				templatesPath.resolve("real_templates").resolve("${variables.component#cap_first}RestService.java.ftl"),
				"${variables.component#cap_first}RestService.java.ftl"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		// compilationUnits
		// .add(compilationUnitsPath.resolve("real_compilation_units").resolve("QueuemanagementRestService.java"));
		compilationUnits
				.add(compilationUnitsPath.resolve("real_compilation_units").resolve("TestmanagementRestService.java"));

		this.templatePreprocessor.setTemplatesRootPath(templatesPath);
		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
				objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();
		assertEquals(treeMatches.size(), 1);
		assertTrue(treeMatches.get(0).isMatch());
	}

	@Test
	void javaFreeMarkerRestServiceImplTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(
				templatesPath.resolve("real_templates")
						.resolve("${variables.component#cap_first}RestServiceImpl.java.ftl"),
				"${variables.component#cap_first}RestService.java.ftl"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		// compilationUnits.add(
		// compilationUnitsPath.resolve("real_compilation_units").resolve("QueuemanagementRestServiceImpl.java"));
		compilationUnits.add(
				compilationUnitsPath.resolve("real_compilation_units").resolve("TestmanagementRestServiceImpl.java"));

		this.templatePreprocessor.setTemplatesRootPath(templatesPath);
		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
				objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();
		assertEquals(treeMatches.size(), 1);
		assertTrue(treeMatches.get(0).isMatch());
	}

	@Test
	void javaFreeMarkerSearchCriteriaToTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(
				templatesPath.resolve("real_templates").resolve("${variables.entityName}SearchCriteriaTo.java.ftl"),
				"${variables.entityName}SearchCriteriaTo.java.ftl"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits
				.add(compilationUnitsPath.resolve("real_compilation_units").resolve("QueueSearchCriteriaTo.java"));
		// compilationUnits
		// .add(compilationUnitsPath.resolve("real_compilation_units").resolve("TestSearchCriteriaTo.java"));

		this.templatePreprocessor.setTemplatesRootPath(templatesPath);
		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
				objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();
		assertEquals(treeMatches.size(), 1);
		assertTrue(treeMatches.get(0).isMatch());
	}

	@Test
	void javaFreeMarkerAbstractSearchCriteriaToTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(
				templatesPath.resolve("real_templates").resolve("AbstractSearchCriteriaTo.java.ftl"),
				"AbstractSearchCriteriaTo.java.ftl"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits
				.add(compilationUnitsPath.resolve("real_compilation_units").resolve("AbstractSearchCriteriaTo.java"));

		this.templatePreprocessor.setTemplatesRootPath(templatesPath);
		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
				objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();
		assertEquals(treeMatches.size(), 1);
		assertTrue(treeMatches.get(0).isMatch());
	}

	@Test
	void javaFreeMarkerApplicationEntityTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(
				new AimPatternTemplate(templatesPath.resolve("real_templates").resolve("ApplicationEntity.java.ftl"),
						"ApplicationEntity.java.ftl"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("real_compilation_units").resolve("ApplicationEntity.java"));

		this.templatePreprocessor.setTemplatesRootPath(templatesPath);
		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
				objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();
		assertEquals(treeMatches.size(), 1);
		assertTrue(treeMatches.get(0).isMatch());
	}

	@Test
	void javaFreeMarkerApplicationPersistencyEntityTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(
				templatesPath.resolve("real_templates").resolve("ApplicationPersistencyEntity.java.ftl"),
				"ApplicationPersistencyEntity.java.ftl"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates, templatesPath);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(
				compilationUnitsPath.resolve("real_compilation_units").resolve("ApplicationPersistencyEntity.java"));

		this.templatePreprocessor.setTemplatesRootPath(templatesPath);
		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, parserClass, lexerClass, grammarPath, metaLanguageConfiguration,
				objectLanguageProperties, this.placeholderResolver, this.templatePreprocessor);

		AimPatternDetectionResult patternDetectionResult = aimPatternDetectionEngine.detect();
		List<TreeMatch> treeMatches = patternDetectionResult.getTreeMatches();
		assertEquals(treeMatches.size(), 1);
		assertTrue(treeMatches.get(0).isMatch());
	}
}
