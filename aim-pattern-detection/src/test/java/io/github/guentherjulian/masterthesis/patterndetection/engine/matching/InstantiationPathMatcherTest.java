package io.github.guentherjulian.masterthesis.patterndetection.engine.matching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage.FreeMarkerMetaLanguagePattern;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage.MetaLanguagePattern;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.FreeMarkerPlaceholderResolver;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.PlaceholderResolver;

public class InstantiationPathMatcherTest {

	private final MetaLanguagePattern metaLanguagePattern = new FreeMarkerMetaLanguagePattern();
	private final PlaceholderResolver placeholderResolver = new FreeMarkerPlaceholderResolver();

	@Test
	void testInstantiationPathWithoutPlaceholderSameFileExtension() {
		String templateInstantiationPath = "src\\main\\java\\org\\domain\\package\\Foo.java";
		String compilationUnitPath = "directory\\project\\subproject\\src\\main\\java\\org\\domain\\package\\Foo.java";

		InstantiationPathMatch instantiationPathMatch = InstantiationPathMatcher.match(compilationUnitPath,
				templateInstantiationPath, this.metaLanguagePattern, this.placeholderResolver);

		assertTrue(instantiationPathMatch.isMatch());
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().isEmpty());
	}

	@Test
	void testInstantiationPathWithoutPlaceholderDifferentFileExtension() {
		String templateInstantiationPath = "src\\main\\java\\org\\domain\\package\\Foo.java.ftl";
		String compilationUnitPath = "directory\\project\\subproject\\src\\main\\java\\org\\domain\\package\\Foo.java";

		InstantiationPathMatch instantiationPathMatch = InstantiationPathMatcher.match(compilationUnitPath,
				templateInstantiationPath, this.metaLanguagePattern, this.placeholderResolver);

		assertTrue(instantiationPathMatch.isMatch());
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().isEmpty());
	}

	@Test
	void testInstantiationPathWithoutPlaceholderIncorrect1() {
		String templateInstantiationPath = "src\\main\\java\\org\\domain\\package\\anotherpackage\\Foo.java";
		String compilationUnitPath = "directory\\project\\subproject\\src\\main\\java\\org\\domain\\package\\Foo.java";

		InstantiationPathMatch instantiationPathMatch = InstantiationPathMatcher.match(compilationUnitPath,
				templateInstantiationPath, this.metaLanguagePattern, this.placeholderResolver);

		assertFalse(instantiationPathMatch.isMatch());
	}

	@Test
	void testInstantiationPathWithoutPlaceholderIncorrect2() {
		String templateInstantiationPath = "src\\main\\java\\org\\domain\\package\\anotherpackage\\Foo.java";
		String compilationUnitPath = "src\\main\\java\\org\\domain\\package\\package\\Foo.java";

		InstantiationPathMatch instantiationPathMatch = InstantiationPathMatcher.match(compilationUnitPath,
				templateInstantiationPath, this.metaLanguagePattern, this.placeholderResolver);

		assertFalse(instantiationPathMatch.isMatch());
	}

	@Test
	void testInstantiationPathWithPlaceholderInFilepath() {
		String templateInstantiationPath = "src\\main\\java\\org\\domain\\${a}\\Foo.java";
		String compilationUnitPath = "directory\\project\\subproject\\src\\main\\java\\org\\domain\\package\\Foo.java";

		InstantiationPathMatch instantiationPathMatch = InstantiationPathMatcher.match(compilationUnitPath,
				templateInstantiationPath, this.metaLanguagePattern, this.placeholderResolver);

		assertTrue(instantiationPathMatch.isMatch());
		assertFalse(instantiationPathMatch.getPlaceholderSubstitutions().isEmpty());
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().containsKey("a"));
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().get("a").contains("package"));
	}

	@Test
	void testInstantiationPathWithPlaceholderInFilename() {
		String templateInstantiationPath = "src\\main\\java\\org\\domain\\package\\${a}.java";
		String compilationUnitPath = "directory\\project\\subproject\\src\\main\\java\\org\\domain\\package\\Foo.java";

		InstantiationPathMatch instantiationPathMatch = InstantiationPathMatcher.match(compilationUnitPath,
				templateInstantiationPath, this.metaLanguagePattern, this.placeholderResolver);

		assertTrue(instantiationPathMatch.isMatch());
		assertFalse(instantiationPathMatch.getPlaceholderSubstitutions().isEmpty());
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().containsKey("a"));
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().get("a").contains("Foo"));
	}

	@Test
	void testInstantiationPathWithPlaceholderInFilenameAndMetaLangExt() {
		String templateInstantiationPath = "src\\main\\java\\org\\domain\\package\\${a}.java.ftl";
		String compilationUnitPath = "directory\\project\\subproject\\src\\main\\java\\org\\domain\\package\\Foo.java";

		InstantiationPathMatch instantiationPathMatch = InstantiationPathMatcher.match(compilationUnitPath,
				templateInstantiationPath, this.metaLanguagePattern, this.placeholderResolver);

		assertTrue(instantiationPathMatch.isMatch());
		assertFalse(instantiationPathMatch.getPlaceholderSubstitutions().isEmpty());
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().containsKey("a"));
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().get("a").contains("Foo"));
	}

	@Test
	void testInstantiationPathWithPlaceholderMultipleOccurencesIncorrect() {
		String templateInstantiationPath = "src\\main\\java\\org\\${a}\\${a}\\Foo.java";
		String compilationUnitPath = "directory\\project\\subproject\\src\\main\\java\\org\\domain\\package\\Foo.java";

		InstantiationPathMatch instantiationPathMatch = InstantiationPathMatcher.match(compilationUnitPath,
				templateInstantiationPath, this.metaLanguagePattern, this.placeholderResolver);

		assertFalse(instantiationPathMatch.isMatch());
	}

	@Test
	void testInstantiationPathWithPlaceholderAsPrefixInFilename() {
		String templateInstantiationPath = "src\\main\\java\\org\\domain\\${a}\\${entityName}Entity.java";
		String compilationUnitPath = "directory\\project\\subproject\\src\\main\\java\\org\\domain\\package\\FooEntity.java";

		InstantiationPathMatch instantiationPathMatch = InstantiationPathMatcher.match(compilationUnitPath,
				templateInstantiationPath, this.metaLanguagePattern, this.placeholderResolver);

		assertTrue(instantiationPathMatch.isMatch());
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().containsKey("a"));
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().get("a").contains("package"));
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().containsKey("entityName"));
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().get("entityName").contains("Foo"));
	}

	@Test
	void testInstantiationPathWithPlaceholderAsPrefixInFilepath() {
		String templateInstantiationPath = "src\\app\\pages\\${etoName}-detail\\${etoName}-detail.page.html.ftl";
		String compilationUnitPath = "directory\\project\\subproject\\src\\app\\pages\\employee-detail\\employee-detail.page.html";
		InstantiationPathMatch instantiationPathMatch = InstantiationPathMatcher.match(compilationUnitPath,
				templateInstantiationPath, this.metaLanguagePattern, this.placeholderResolver);

		assertTrue(instantiationPathMatch.isMatch());
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().containsKey("etoName"));
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().get("etoName").contains("employee"));
	}

	@Test
	void testInstantiationPathWithPlaceholderAsPostfixInFilename() {
		String templateInstantiationPath = "src\\main\\java\\org\\domain\\${a}\\Employee${postfix}.java";
		String compilationUnitPath = "directory\\project\\subproject\\src\\main\\java\\org\\domain\\package\\EmployeeRestService.java";

		InstantiationPathMatch instantiationPathMatch = InstantiationPathMatcher.match(compilationUnitPath,
				templateInstantiationPath, this.metaLanguagePattern, this.placeholderResolver);

		assertTrue(instantiationPathMatch.isMatch());
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().containsKey("a"));
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().get("a").contains("package"));
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().containsKey("postfix"));
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().get("postfix").contains("RestService"));
	}

	@Test
	void testInstantiationPathWithPlaceholderAsPostfixInFilepath() {
		String templateInstantiationPath = "src\\app\\pages\\employee-${postfix}\\employee-detail.page.html.ftl";
		String compilationUnitPath = "directory\\project\\subproject\\src\\app\\pages\\employee-component\\employee-detail.page.html";
		InstantiationPathMatch instantiationPathMatch = InstantiationPathMatcher.match(compilationUnitPath,
				templateInstantiationPath, this.metaLanguagePattern, this.placeholderResolver);

		assertTrue(instantiationPathMatch.isMatch());
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().containsKey("postfix"));
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().get("postfix").contains("component"));
	}

	@Test
	void testInstantiationPathWithPlaceholderAsInfixInFilename() {
		String templateInstantiationPath = "src\\main\\java\\org\\domain\\package\\My${component}RestService.java";
		String compilationUnitPath = "directory\\project\\subproject\\src\\main\\java\\org\\domain\\package\\MyEmployeeRestService.java";

		InstantiationPathMatch instantiationPathMatch = InstantiationPathMatcher.match(compilationUnitPath,
				templateInstantiationPath, this.metaLanguagePattern, this.placeholderResolver);

		assertTrue(instantiationPathMatch.isMatch());
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().containsKey("component"));
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().get("component").contains("Employee"));
	}

	@Test
	void testInstantiationPathWithPlaceholderAsInfixInFilepath() {
		String templateInstantiationPath = "src\\app\\pages\\my-${component}-service\\employee-detail.page.html.ftl";
		String compilationUnitPath = "directory\\project\\subproject\\src\\app\\pages\\my-employee-service\\employee-detail.page.html";
		InstantiationPathMatch instantiationPathMatch = InstantiationPathMatcher.match(compilationUnitPath,
				templateInstantiationPath, this.metaLanguagePattern, this.placeholderResolver);

		assertTrue(instantiationPathMatch.isMatch());
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().containsKey("component"));
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().get("component").contains("employee"));
	}

	@Test
	void testInstantiationPathWithPlaceholderAndTransformationFunction() {
		String templateInstantiationPath = "src\\app\\pages\\${etoName#cap_first}-detail\\${etoName#cap_first}-detail.page.html.ftl";
		String compilationUnitPath = "directory\\project\\subproject\\src\\app\\pages\\employee-detail\\employee-detail.page.html";

		InstantiationPathMatch instantiationPathMatch = InstantiationPathMatcher.match(compilationUnitPath,
				templateInstantiationPath, this.metaLanguagePattern, this.placeholderResolver);

		assertTrue(instantiationPathMatch.isMatch());
		assertFalse(instantiationPathMatch.getPlaceholderSubstitutions().isEmpty());
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().containsKey("etoName"));
		assertEquals(instantiationPathMatch.getPlaceholderSubstitutions().get("etoName").size(), 2);
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().get("etoName").contains("employee"));
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().get("etoName").contains("Employee"));
	}

	@Test
	void testInstantiationPathWithPlaceholderAndDuplicateTransformationFunction() {
		String templateInstantiationPath = "src\\app\\pages\\${etoName#cap_first}-detail\\${etoName#cap_first#cap_first}-detail.page.html.ftl";
		String compilationUnitPath = "directory\\project\\subproject\\src\\app\\pages\\employee-detail\\employee-detail.page.html";

		InstantiationPathMatch instantiationPathMatch = InstantiationPathMatcher.match(compilationUnitPath,
				templateInstantiationPath, this.metaLanguagePattern, this.placeholderResolver);

		assertTrue(instantiationPathMatch.isMatch());
		assertFalse(instantiationPathMatch.getPlaceholderSubstitutions().isEmpty());
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().containsKey("etoName"));
		assertEquals(instantiationPathMatch.getPlaceholderSubstitutions().get("etoName").size(), 2);
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().get("etoName").contains("employee"));
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().get("etoName").contains("Employee"));
	}

	@Test
	void testInstantiationPathWithPlaceholderAndNonImplementedTransformationFunction() {
		String templateInstantiationPath = "src\\app\\pages\\${etoName#test_function}-detail\\${etoName#cap_first#cap_first}-detail.page.html.ftl";
		String compilationUnitPath = "directory\\project\\subproject\\src\\app\\pages\\employee-detail\\employee-detail.page.html";

		InstantiationPathMatch instantiationPathMatch = InstantiationPathMatcher.match(compilationUnitPath,
				templateInstantiationPath, this.metaLanguagePattern, this.placeholderResolver);

		assertTrue(instantiationPathMatch.isMatch());
		assertFalse(instantiationPathMatch.getPlaceholderSubstitutions().isEmpty());
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().containsKey("etoName"));
		assertEquals(instantiationPathMatch.getPlaceholderSubstitutions().get("etoName").size(), 2);
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().get("etoName").contains("employee"));
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().get("etoName").contains("Employee"));

		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().containsKey("etoName#test_function"));
		assertEquals(instantiationPathMatch.getPlaceholderSubstitutions().get("etoName#test_function").size(), 1);
		assertTrue(
				instantiationPathMatch.getPlaceholderSubstitutions().get("etoName#test_function").contains("employee"));
	}

	@Test
	void testInstantiationPathWithLongSuffix() {
		String templateInstantiationPath = "crud-java-server-app\\src\\main\\resources\\templates\\java\\${variables.rootPackage}\\${variables.component}\\logic\\api\\to\\${variables.entityName}SearchCriteriaTo.java.ftl";
		String compilationUnitPath = "com\\devonfw\\application\\jtqj\\accesscodemanagement\\common\\api\\AccessCode.java";

		InstantiationPathMatch instantiationPathMatch = InstantiationPathMatcher.match(compilationUnitPath,
				templateInstantiationPath, this.metaLanguagePattern, this.placeholderResolver);

		assertFalse(instantiationPathMatch.isMatch());
	}

	@Test
	void testInstantiationPathWithMultipleSegmentsForOnePlaceholder() {
		String templateInstantiationPath = "java\\${variables.rootPackage}\\${variables.component}\\logic\\api\\to\\${variables.entityName}SearchCriteriaTo.java.ftl";
		String compilationUnitPath = "core\\src\\main\\java\\com\\devonfw\\application\\jtqj\\accesscodemanagement\\logic\\api\\to\\AccessCodeSearchCriteriaTo.java";

		InstantiationPathMatch instantiationPathMatch = InstantiationPathMatcher.match(compilationUnitPath,
				templateInstantiationPath, this.metaLanguagePattern, this.placeholderResolver);

		assertTrue(instantiationPathMatch.isMatch());
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().containsKey("variables.rootPackage"));
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().containsKey("variables.component"));
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().containsKey("variables.entityName"));
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().get("variables.rootPackage")
				.contains("com.devonfw.application.jtqj"));
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().get("variables.component")
				.contains("accesscodemanagement"));
		assertTrue(instantiationPathMatch.getPlaceholderSubstitutions().get("variables.entityName")
				.contains("AccessCode"));
	}
}
