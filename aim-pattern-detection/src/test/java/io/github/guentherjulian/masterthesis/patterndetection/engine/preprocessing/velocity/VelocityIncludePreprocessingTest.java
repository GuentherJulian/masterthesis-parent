package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.velocity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.AbstractPreprocessingTest;
import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.TemplatePreprocessor;
import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.VelocityTemplatePreprocessor;

public class VelocityIncludePreprocessingTest extends AbstractPreprocessingTest {

	@Test
	void preprocessingSingleIncludeTest() throws Exception {
		Path templatePath = resourcesPath.resolve("preprocessing").resolve("velocity").resolve("include")
				.resolve("IncludeSingle.java");

		TemplatePreprocessor templatePreprocessor = new VelocityTemplatePreprocessor();
		templatePreprocessor
				.setTemplatesRootPath(resourcesPath.resolve("preprocessing").resolve("velocity").resolve("include"));

		byte[] preprocessedFileByteArray = templatePreprocessor.processTemplate(templatePath);

		String templateString = new String(preprocessedFileByteArray);

		System.out.println(templateString);
		assertTrue(templateString.contains("public class Foo"));
		assertFalse(templateString.contains("#include("));
	}

	@Test
	void preprocessingMultipleIncludeTest() throws Exception {
		Path templatePath = resourcesPath.resolve("preprocessing").resolve("velocity").resolve("include")
				.resolve("IncludeMultiple.java");

		TemplatePreprocessor templatePreprocessor = new VelocityTemplatePreprocessor();
		templatePreprocessor
				.setTemplatesRootPath(resourcesPath.resolve("preprocessing").resolve("velocity").resolve("include"));

		byte[] preprocessedFileByteArray = templatePreprocessor.processTemplate(templatePath);

		String templateString = new String(preprocessedFileByteArray);

		System.out.println(templateString);
		assertTrue(templateString.contains("public class Foo"));
		assertTrue(templateString.contains("public class Bar"));
		assertFalse(templateString.contains("#include("));
	}
}
