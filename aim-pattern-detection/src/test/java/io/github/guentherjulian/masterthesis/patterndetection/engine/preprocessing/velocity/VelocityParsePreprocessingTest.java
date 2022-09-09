package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.velocity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.AbstractPreprocessingTest;
import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.TemplatePreprocessor;
import io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing.VelocityTemplatePreprocessor;

public class VelocityParsePreprocessingTest extends AbstractPreprocessingTest {

	@Test
	void preprocessingParseTest() throws Exception {
		Path templatePath = resourcesPath.resolve("preprocessing").resolve("velocity").resolve("parse")
				.resolve("Parse.java");

		TemplatePreprocessor templatePreprocessor = new VelocityTemplatePreprocessor();
		templatePreprocessor
				.setTemplatesRootPath(resourcesPath.resolve("preprocessing").resolve("velocity").resolve("parse"));

		byte[] preprocessedFileByteArray = templatePreprocessor.processTemplate(templatePath);

		String templateString = new String(preprocessedFileByteArray);

		System.out.println(templateString);
		assertTrue(templateString.contains("#if ($anything)"));
		assertFalse(templateString.contains("#parse( \"Foo.vm\")"));
	}
}
