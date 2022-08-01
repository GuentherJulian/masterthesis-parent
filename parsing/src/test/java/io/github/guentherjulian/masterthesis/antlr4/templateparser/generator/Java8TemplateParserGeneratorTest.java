package io.github.guentherjulian.masterthesis.antlr4.templateparser.generator;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.antlr4.parser.generator.Antlr4ParserGenerator;

public class Java8TemplateParserGeneratorTest {

	private static Path projectPath;
	private static Path grammarsPath;

	@BeforeAll
	public static void prepare() throws URISyntaxException {
		projectPath = Paths
				.get(Java8TemplateParserGeneratorTest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
				.getParent().getParent();

		grammarsPath = projectPath.resolve("src/main/resources/grammars");
	}

	@Test
	public void generateJavaFreemarkerTemplateParser() {
		Path grammarFile = grammarsPath.resolve("java8FreemarkerTemplate/Java8FreemarkerTemplate.g4");
		String packageName = "org.antlr.parser.java8freemarkertemplate";
		Path outputPath = projectPath.resolve("target/generated-sources/antlr4").resolve(packageName.replace(".", "/"));
		String placeholderName = "FM_PLACEHOLDER";

		Antlr4ParserGenerator antlr4ParserGenerator = new Antlr4ParserGenerator();
		antlr4ParserGenerator.generateParser(grammarFile, packageName, outputPath, placeholderName);
	}

	@Test
	public void generateJavaVelocityTemplateParser() {
		Path grammarFile = grammarsPath.resolve("java8VelocityTemplate/Java8VelocityTemplate.g4");
		String packageName = "org.antlr.parser.java8velocitytemplate";
		Path outputPath = projectPath.resolve("target/generated-sources/antlr4").resolve(packageName.replace(".", "/"));
		String placeholderName = "FM_PLACEHOLDER";

		Antlr4ParserGenerator antlr4ParserGenerator = new Antlr4ParserGenerator();
		antlr4ParserGenerator.generateParser(grammarFile, packageName, outputPath, placeholderName);
	}

	@Test
	public void generateJavaStringTemplateTemplateParser() {
		Path grammarFile = grammarsPath.resolve("java8StringTemplateTemplate/Java8StringTemplateTemplate.g4");
		String packageName = "org.antlr.parser.java8stringtemplatetemplate";
		Path outputPath = projectPath.resolve("target/generated-sources/antlr4").resolve(packageName.replace(".", "/"));
		String placeholderName = "FM_PLACEHOLDER";

		Antlr4ParserGenerator antlr4ParserGenerator = new Antlr4ParserGenerator();
		antlr4ParserGenerator.generateParser(grammarFile, packageName, outputPath, placeholderName);
	}
}
