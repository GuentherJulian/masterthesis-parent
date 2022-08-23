package io.github.guentherjulian.masterthesis.patterndetection.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.antlr.parser.java8stringtemplatepreprocessedtemplate.Java8StringTemplatePreprocessedTemplateLexer;
import org.antlr.parser.java8stringtemplatepreprocessedtemplate.Java8StringTemplatePreprocessedTemplateParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.patterndetection.aimpattern.AimPattern;
import io.github.guentherjulian.masterthesis.patterndetection.aimpattern.AimPatternTemplate;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguageConfiguration;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguageLexerRules;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguagePattern;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.StringTemplateLexerRuleNames;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.StringTemplateMetaLanguagePattern;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.objectlanguage.JavaProperties;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.objectlanguage.ObjectLanguageProperties;
import io.github.guentherjulian.masterthesis.patterndetection.engine.matching.TreeMatch;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.PlaceholderResolver;
import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.StringTemplateTemplatePreprocessor;
import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.TemplatePreprocessor;
import io.github.guentherjulian.masterthesis.patterndetection.exception.NoMatchException;

public class JavaStringTemplateAimPatternDetectionEngineTest extends AbstractAimPatternDetectionEngineTest {

	private final String metaLangPrefix = "fm_";
	private MetaLanguagePattern metaLanguagePattern = new StringTemplateMetaLanguagePattern();
	private MetaLanguageLexerRules metaLanguageLexerRules = new StringTemplateLexerRuleNames();
	private MetaLanguageConfiguration metaLanguageConfiguration = new MetaLanguageConfiguration(
			this.metaLanguageLexerRules, this.metaLanguagePattern, this.metaLangPrefix);
	private ObjectLanguageProperties objectLanguageProperties = new JavaProperties(this.metaLangPrefix);
	private PlaceholderResolver placeholderResolver = null;
	private TemplatePreprocessor templatePreprocessor = new StringTemplateTemplatePreprocessor();

	@BeforeAll
	public static void setupTests() throws URISyntaxException {
		templatesPath = resourcesPath.resolve("templates").resolve("java_stringtemplate");
		compilationUnitsPath = resourcesPath.resolve("compilation-units").resolve("java");
		grammarPath = grammarPath.resolve("java8StringTemplateTemplate").resolve("Java8StringTemplateTemplate.g4");

		parserClass = Java8StringTemplatePreprocessedTemplateParser.class;
		lexerClass = Java8StringTemplatePreprocessedTemplateLexer.class;
	}

	@Test
	void javaStringTemplateSimplePackageDeclarationTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(templatesPath.resolve("SimplePackageDeclTemplate.java"),
				"SimplePackageDeclTemplate.java"));
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
	void javaStringTemplateComplexPackageDeclarationTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(templatesPath.resolve("ComplexPackageDeclTemplate.java"),
				"ComplexPackageDeclTemplate.java"));
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
	void javaStringTemplateCopyConstructorTest() throws Exception {

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
	void javaStringTemplateSimpleIfElseCondition() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();

		Path template = templatesPath.resolve("SimpleClassWithIfElseTemplate.java");
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
	void javaStringTemplateSimpleList() throws Exception {

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
