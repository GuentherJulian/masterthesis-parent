package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.freemarker;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.AbstractPreprocessingTest;
import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.FreeMarkerTemplatePreprocessor;
import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.TemplatePreprocessor;

public class FreeMarkerIncludePreprocessingTest extends AbstractPreprocessingTest {

	@Test
	void preprocessingIncludeWithAbsolutePath() throws Exception {
		Path templatePath = resourcesPath.resolve("preprocessing").resolve("freemarker").resolve("include")
				.resolve("IncludeAbsolutePath.java");

		TemplatePreprocessor templatePreprocessor = new FreeMarkerTemplatePreprocessor();
		templatePreprocessor
				.setTemplatesRootPath(resourcesPath.resolve("preprocessing").resolve("freemarker").resolve("include"));

		byte[] preprocessedFileByteArray = templatePreprocessor.processTemplate(templatePath);

		String templateString = new String(preprocessedFileByteArray);

		System.out.println(templateString);
		assertTrue(templateString.contains("private int foo;"));
		assertTrue(templateString.contains("private int bar;"));
		assertFalse(templateString.contains("<#include"));
	}

	@Test
	void preprocessingIncludeWithRelativePath() throws Exception {
		Path templatePath = resourcesPath.resolve("preprocessing").resolve("freemarker").resolve("include")
				.resolve("relativePath").resolve("IncludeRelativePath.java");

		TemplatePreprocessor templatePreprocessor = new FreeMarkerTemplatePreprocessor();
		templatePreprocessor
				.setTemplatesRootPath(resourcesPath.resolve("preprocessing").resolve("freemarker").resolve("include"));

		byte[] preprocessedFileByteArray = templatePreprocessor.processTemplate(templatePath);

		String templateString = new String(preprocessedFileByteArray);

		System.out.println(templateString);
		assertTrue(templateString.contains("private int foo;"));
		assertTrue(templateString.contains("private int bar;"));
		assertFalse(templateString.contains("<#include"));
	}
}
