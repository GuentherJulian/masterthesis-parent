package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.URISyntaxException;
import java.nio.file.Path;

import org.antlr.parser.java8freemarkertemplate.Java8FreemarkerTemplateLexer;
import org.antlr.parser.java8freemarkertemplate.Java8FreemarkerTemplateParser;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class JavaFreemarkerPreprocessingTest extends AbstractPreprocessingTest {

	@BeforeAll
	public static void setupTests() throws URISyntaxException {
		grammarPath = grammarPath.resolve("java8FreemarkerTemplate").resolve("Java8FreemarkerTemplate.g4");

		lexerClass = Java8FreemarkerTemplateLexer.class;
		parserClass = Java8FreemarkerTemplateParser.class;

		parserStartRule = "compilationUnit";
	}

	@Test
	void parseInvalidFile() throws Exception {
		Path invalidPrefixFilePath = resourcesPath.resolve("preprocessing").resolve("InvalidPrefix.java");
		Parser parser = this.createParser(invalidPrefixFilePath);

		ParserRuleContext tree = null;
		try {
			tree = parse(parser, parserStartRule);
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertNotNull(tree);
		assertNotNull(tree.exception);
	}

	@Test
	void parseValidFile() throws Exception {
		Path invalidPrefixFilePath = resourcesPath.resolve("preprocessing").resolve("ValidPrefix.java");
		Parser parser = this.createParser(invalidPrefixFilePath);

		ParserRuleContext tree = null;
		try {
			tree = parse(parser, parserStartRule);
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertNotNull(tree);
		assertNull(tree.exception);
	}

	@Test
	void processFileWithInvalidPrefix() throws Exception {
		Path invalidPrefixFilePath = resourcesPath.resolve("preprocessing").resolve("InvalidPrefix.java");
		Parser parser = this.createParser(invalidPrefixFilePath);

		ParserRuleContext tree = null;
		try {
			tree = parse(parser, parserStartRule);
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertNotNull(tree);
		assertNotNull(tree.exception);

		TemplatePreprocessor javaFreeMarkerPreprocessingStep = new FreeMarkerTemplatePreprocessor();

		byte[] preprocessedFileByteArray = javaFreeMarkerPreprocessingStep.processTemplate(invalidPrefixFilePath);

		parser = this.createParser(preprocessedFileByteArray);
		tree = null;
		try {
			tree = parse(parser, parserStartRule);
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertNotNull(tree);
		assertNull(tree.exception);
	}

	@Test
	void processFileWithInvalidSuffix() throws Exception {
		Path invalidPrefixFilePath = resourcesPath.resolve("preprocessing").resolve("InvalidSuffix.java");
		Parser parser = this.createParser(invalidPrefixFilePath);

		ParserRuleContext tree = null;
		try {
			tree = parse(parser, parserStartRule);
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertNotNull(tree);
		// assertNotNull(tree.exception);

		FreeMarkerTemplatePreprocessor javaFreeMarkerPreprocessingStep = new FreeMarkerTemplatePreprocessor();

		byte[] preprocessedFileByteArray = javaFreeMarkerPreprocessingStep.processTemplate(invalidPrefixFilePath);

		parser = this.createParser(preprocessedFileByteArray);

		tree = null;
		try {
			tree = parse(parser, parserStartRule);
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertNotNull(tree);
		assertNull(tree.exception);
	}
}
