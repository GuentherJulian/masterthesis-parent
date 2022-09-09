package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.velocity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.AbstractPreprocessingTest;
import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.TemplatePreprocessor;
import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.VelocityTemplatePreprocessor;

public class VelocityMacroPreprocessingTest extends AbstractPreprocessingTest {

	@Test
	void preprocessingMacroWithoutBodyTest() throws Exception {
		Path templatePath = resourcesPath.resolve("preprocessing").resolve("velocity").resolve("macro")
				.resolve("MacroWithoutBody.java");

		TemplatePreprocessor templatePreprocessor = new VelocityTemplatePreprocessor();

		byte[] preprocessedFileByteArray = templatePreprocessor.processTemplate(templatePath);

		String templateString = new String(preprocessedFileByteArray);

		System.out.println(templateString);
		assertTrue(templateString.contains("private int j;"));
		assertFalse(templateString.contains("#testMacro()"));
		assertFalse(templateString.contains("#macro"));
	}

	@Disabled
	@Test
	void preprocessingMacroWithBodyTest() throws Exception {
		Path templatePath = resourcesPath.resolve("preprocessing").resolve("velocity").resolve("macro")
				.resolve("MacroWithBody.java");

		TemplatePreprocessor templatePreprocessor = new VelocityTemplatePreprocessor();

		byte[] preprocessedFileByteArray = templatePreprocessor.processTemplate(templatePath);

		String templateString = new String(preprocessedFileByteArray);

		System.out.println(templateString);
		assertTrue(templateString.contains("private int foo;"));
		assertFalse(templateString.contains("#@testMacro"));
		assertFalse(templateString.contains("#macro"));
	}

	@Test
	void preprocessingMacroWithParamsTest() throws Exception {
		Path templatePath = resourcesPath.resolve("preprocessing").resolve("velocity").resolve("macro")
				.resolve("MacroWithParams.java");

		TemplatePreprocessor templatePreprocessor = new VelocityTemplatePreprocessor();

		byte[] preprocessedFileByteArray = templatePreprocessor.processTemplate(templatePath);

		String templateString = new String(preprocessedFileByteArray);

		System.out.println(templateString);
		assertTrue(templateString.contains("private int i;"));
		assertTrue(templateString.contains("private int j;"));
		assertFalse(templateString.contains("#testMacro"));
		assertFalse(templateString.contains("#macro"));
	}

	@Test
	void preprocessingMacroWithMetaIfTest() throws Exception {
		Path templatePath = resourcesPath.resolve("preprocessing").resolve("velocity").resolve("macro")
				.resolve("MacroWithoutBodyWithIf.java");

		TemplatePreprocessor templatePreprocessor = new VelocityTemplatePreprocessor();

		byte[] preprocessedFileByteArray = templatePreprocessor.processTemplate(templatePath);

		String templateString = new String(preprocessedFileByteArray);

		System.out.println(templateString);
		assertTrue(templateString.contains("private int j;"));
		assertFalse(templateString.contains("#testMacro()"));
		assertFalse(templateString.contains("#macro"));
	}
}
