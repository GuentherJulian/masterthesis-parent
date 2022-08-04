package io.github.guentherjulian.masterthesis.patterndetection.engine.matching;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class InstantiationPathMatcherTest {

	private static final String PLACEHOLDER_REGEX = "\\$\\{(.+)\\}";

	@Test
	void testInstantiationPathWithoutPlaceholderCorrect() {
		String templateInstantiationPath = "src\\main\\java\\org\\domain\\package\\Foo.java";
		String compilationUnitPath = "directory\\project\\subproject\\src\\main\\java\\org\\domain\\package\\Foo.java";

		InstantiationPathMatch instantiationPathMatch = InstantiationPathMatcher.match(compilationUnitPath,
				templateInstantiationPath, PLACEHOLDER_REGEX);

		assertTrue(instantiationPathMatch.isMatch());
	}

	@Test
	void testInstantiationPathWithoutPlaceholderIncorrect() {
		String templateInstantiationPath = "src\\main\\java\\org\\domain\\package\\anotherpackage\\Foo.java";
		String compilationUnitPath = "directory\\project\\subproject\\src\\main\\java\\org\\domain\\package\\Foo.java";

		InstantiationPathMatch instantiationPathMatch = InstantiationPathMatcher.match(compilationUnitPath,
				templateInstantiationPath, PLACEHOLDER_REGEX);

		assertFalse(instantiationPathMatch.isMatch());
	}

	@Test
	void testInstantiationPathWithPlaceholder() {
		String templateInstantiationPath = "src\\main\\java\\org\\domain\\${a}\\Foo.java";
		String compilationUnitPath = "directory\\project\\subproject\\src\\main\\java\\org\\domain\\package\\Foo.java";

		InstantiationPathMatch instantiationPathMatch = InstantiationPathMatcher.match(compilationUnitPath,
				templateInstantiationPath, PLACEHOLDER_REGEX);

		assertTrue(instantiationPathMatch.isMatch());
	}

}
