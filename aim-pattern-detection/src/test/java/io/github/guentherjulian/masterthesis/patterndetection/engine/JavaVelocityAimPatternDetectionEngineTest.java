package io.github.guentherjulian.masterthesis.patterndetection.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.antlr.parser.java8velocitytemplate.Java8VelocityTemplateLexer;
import org.antlr.parser.java8velocitytemplate.Java8VelocityTemplateParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.patterndetection.aimpattern.AimPattern;
import io.github.guentherjulian.masterthesis.patterndetection.aimpattern.AimPatternTemplate;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguageConfiguration;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguageLexerRules;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguagePattern;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.VelocityLexerRuleNames;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.VelocityMetaLanguagePattern;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.objectlanguage.JavaProperties;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.objectlanguage.ObjectLanguageProperties;
import io.github.guentherjulian.masterthesis.patterndetection.engine.matching.TreeMatch;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.PlaceholderResolver;
import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.TemplatePreprocessor;
import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.VelocityTemplatePreprocessor;
import io.github.guentherjulian.masterthesis.patterndetection.exception.NoMatchException;

public class JavaVelocityAimPatternDetectionEngineTest extends AbstractAimPatternDetectionEngineTest {

	private final String metaLangPrefix = "fm_";
	private MetaLanguagePattern metaLanguagePattern = new VelocityMetaLanguagePattern();
	private MetaLanguageLexerRules metaLanguageLexerRules = new VelocityLexerRuleNames();
	private MetaLanguageConfiguration metaLanguageConfiguration = new MetaLanguageConfiguration(
			this.metaLanguageLexerRules, this.metaLanguagePattern);
	private ObjectLanguageProperties objectLanguageProperties = new JavaProperties(this.metaLangPrefix);
	private PlaceholderResolver placeholderResolver = null;
	private TemplatePreprocessor templatePreprocessor = new VelocityTemplatePreprocessor();

	@BeforeAll
	public static void setupTests() throws URISyntaxException {
		templatesPath = resourcesPath.resolve("templates").resolve("java_velocity");
		compilationUnitsPath = resourcesPath.resolve("compilation-units").resolve("java");
		grammarPath = grammarPath.resolve("java8VelocityTemplate").resolve("Java8VelocityTemplate.g4");

		parserClass = Java8VelocityTemplateParser.class;
		lexerClass = Java8VelocityTemplateLexer.class;
	}

	@Test
	void javaVelocitySimplePackageDeclarationTest() throws Exception {

		Path template = templatesPath.resolve("SimplePackageDeclTemplate.java");
		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(template, "SimplePackageDeclTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates);
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
	void javaVelocityComplexPackageDeclarationTest() throws Exception {

		Path template = templatesPath.resolve("ComplexPackageDeclTemplate.java");
		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(template, "ComplexPackageDeclTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates);
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
	void javaVelocityCopyConstructorTest() throws Exception {

		Path template = templatesPath.resolve("SimpleClassWithCopyConstructorTemplate.java");
		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(template, "SimpleClassWithCopyConstructorTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates);
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
		assertTrue(treeMatches.get(1).getException() instanceof NoMatchException);
	}

	@Test
	void javaVelocitySimpleIfElseCondition() throws Exception {

		Path template = templatesPath.resolve("SimpleClassWithIfElseTemplate.java");
		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(template, "SimpleClassWithIfElseTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates);
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
	void javaVelocitySimpleList() throws Exception {

		Path template = templatesPath.resolve("SimpleListTemplate.java");
		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(template, "SimpleListTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates);
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
}
