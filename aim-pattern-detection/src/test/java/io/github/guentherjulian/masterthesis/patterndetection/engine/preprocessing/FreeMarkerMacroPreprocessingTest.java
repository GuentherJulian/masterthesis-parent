package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class FreeMarkerMacroPreprocessingTest extends AbstractPreprocessingTest {

	@Test
	void preprocessingMacroWithParamsTest() throws Exception {
		Path templatePath = resourcesPath.resolve("preprocessing").resolve("macro").resolve("MacroWithParams.java");

		TemplatePreprocessor templatePreprocessor = new FreeMarkerTemplatePreprocessor();

		byte[] preprocessedFileByteArray = templatePreprocessor.processTemplate(templatePath);

		String templateString = new String(preprocessedFileByteArray);

		System.out.println(templateString);
		assertTrue(templateString.contains("Macro 1, params: a, b, c"));
		assertTrue(templateString.contains("Macro 2, params: foo, bar, foobar"));
		assertTrue(templateString.contains("Macro 3, params: foo, b, foobar"));
	}

	@Test
	void preprocessingMacroWithoutParamsTest() throws Exception {
		Path templatePath = resourcesPath.resolve("preprocessing").resolve("macro").resolve("MacroWithoutParams.java");

		TemplatePreprocessor templatePreprocessor = new FreeMarkerTemplatePreprocessor();

		byte[] preprocessedFileByteArray = templatePreprocessor.processTemplate(templatePath);

		String templateString = new String(preprocessedFileByteArray);

		System.out.println(templateString);
		assertTrue(templateString.contains("Macro Test"));
		assertFalse(templateString.contains("<@test />"));
		assertFalse(templateString.contains("<#macro test>"));
	}

	@Test
	void preprocessingMacroFromIncludeTest() throws Exception {
		Path templatePath = resourcesPath.resolve("preprocessing").resolve("macro").resolve("MacroFromInclude.java");

		TemplatePreprocessor templatePreprocessor = new FreeMarkerTemplatePreprocessor();
		templatePreprocessor.setTemplatesRootPath(resourcesPath.resolve("preprocessing").resolve("macro"));

		byte[] preprocessedFileByteArray = templatePreprocessor.processTemplate(templatePath);

		String templateString = new String(preprocessedFileByteArray);

		System.out.println(templateString);
		assertTrue(templateString.contains("private int j;"));
		assertFalse(templateString.contains("<@test />"));
	}
}
