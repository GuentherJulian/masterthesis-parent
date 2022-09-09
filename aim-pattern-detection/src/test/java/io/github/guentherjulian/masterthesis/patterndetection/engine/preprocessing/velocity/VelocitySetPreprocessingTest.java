package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.velocity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.AbstractPreprocessingTest;
import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.TemplatePreprocessor;
import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.VelocityTemplatePreprocessor;

public class VelocitySetPreprocessingTest extends AbstractPreprocessingTest {

	@Test
	void preprocessingParseTest() throws Exception {
		Path templatePath = resourcesPath.resolve("preprocessing").resolve("velocity").resolve("set")
				.resolve("SetDirective.java");

		TemplatePreprocessor templatePreprocessor = new VelocityTemplatePreprocessor();

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
