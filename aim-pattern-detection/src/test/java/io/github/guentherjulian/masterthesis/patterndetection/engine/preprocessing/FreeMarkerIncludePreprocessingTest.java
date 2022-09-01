package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class FreeMarkerIncludePreprocessingTest extends AbstractPreprocessingTest {

	@Test
	void preprocessingIncludeWithAbsolutePath() throws Exception {
		Path templatePath = resourcesPath.resolve("preprocessing").resolve("include")
				.resolve("IncludeAbsolutePath.java");

		TemplatePreprocessor templatePreprocessor = new FreeMarkerTemplatePreprocessor();
		templatePreprocessor.setTemplatesRootPath(resourcesPath.resolve("preprocessing").resolve("include"));

		byte[] preprocessedFileByteArray = templatePreprocessor.processTemplate(templatePath);

		String templateString = new String(preprocessedFileByteArray);

		System.out.println(templateString);
		assertTrue(templateString.contains("private int foo;"));
		assertTrue(templateString.contains("private int bar;"));
		assertFalse(templateString.contains("<#include"));
	}

	@Test
	void preprocessingIncludeWithRelativePath() throws Exception {
		Path templatePath = resourcesPath.resolve("preprocessing").resolve("include").resolve("relativePath")
				.resolve("IncludeRelativePath.java");

		TemplatePreprocessor templatePreprocessor = new FreeMarkerTemplatePreprocessor();
		templatePreprocessor.setTemplatesRootPath(resourcesPath.resolve("preprocessing").resolve("include"));

		byte[] preprocessedFileByteArray = templatePreprocessor.processTemplate(templatePath);

		String templateString = new String(preprocessedFileByteArray);

		System.out.println(templateString);
		assertTrue(templateString.contains("private int foo;"));
		assertTrue(templateString.contains("private int bar;"));
		assertFalse(templateString.contains("<#include"));
	}
}
