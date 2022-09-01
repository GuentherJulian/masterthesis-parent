package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class FreeMarkerAssignPreprocessingTest extends AbstractPreprocessingTest {

	@Test
	void preprocessingSingleAssignTest() throws Exception {
		Path templatePath = resourcesPath.resolve("preprocessing").resolve("assign").resolve("SingleAssign.java");

		TemplatePreprocessor templatePreprocessor = new FreeMarkerTemplatePreprocessor();

		byte[] preprocessedFileByteArray = templatePreprocessor.processTemplate(templatePath);

		Map<String, Set<String>> variables = templatePreprocessor.getVariables();
		String templateString = new String(preprocessedFileByteArray);

		System.out.println(templateString);
		assertFalse(templateString.contains("<#assign"));
		assertTrue(variables.containsKey("var"));
		assertTrue(variables.get("var").contains("foo"));
	}

	@Test
	void preprocessingMultipleAssignTest() throws Exception {
		Path templatePath = resourcesPath.resolve("preprocessing").resolve("assign").resolve("MultipleAssign.java");

		TemplatePreprocessor templatePreprocessor = new FreeMarkerTemplatePreprocessor();

		byte[] preprocessedFileByteArray = templatePreprocessor.processTemplate(templatePath);

		Map<String, Set<String>> variables = templatePreprocessor.getVariables();
		String templateString = new String(preprocessedFileByteArray);

		System.out.println(templateString);
		assertFalse(templateString.contains("<#assign"));
		assertTrue(variables.containsKey("var1"));
		assertTrue(variables.get("var1").contains("foo"));
		assertTrue(variables.containsKey("var2"));
		assertTrue(variables.get("var2").contains("bar"));
	}
}
