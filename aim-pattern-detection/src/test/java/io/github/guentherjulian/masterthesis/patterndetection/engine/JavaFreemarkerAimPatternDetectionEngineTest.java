package io.github.guentherjulian.masterthesis.patterndetection.engine;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.antlr.parser.java8freemarkertemplate.Java8FreemarkerTemplateLexer;
import org.antlr.parser.java8freemarkertemplate.Java8FreemarkerTemplateParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.guentherjulian.masterthesis.patterndetection.aimpattern.AimPattern;
import io.github.guentherjulian.masterthesis.patterndetection.aimpattern.AimPatternTemplate;

public class JavaFreemarkerAimPatternDetectionEngineTest extends AbstractAimPatternDetectionEngineTest {

	@BeforeAll
	public static void setupTests() throws URISyntaxException {
		templatesPath = resourcesPath.resolve("templates").resolve("java_freemarker");
		compilationUnitsPath = resourcesPath.resolve("compilation-units").resolve("java");
		grammarPath = grammarPath.resolve("java8FreemarkerTemplate").resolve("Java8FreemarkerTemplate.g4");
	}

	@Test
	void javaFreeMarkerSimplePlaceholderTest() throws Exception {

		List<AimPatternTemplate> aimPatternTemplates = new ArrayList<>();
		aimPatternTemplates.add(new AimPatternTemplate(templatesPath.resolve("SimplePackageDeclTemplate.java"),
				"SimplePackageDeclTemplate.java"));
		AimPattern aimPattern = new AimPattern(aimPatternTemplates);
		List<AimPattern> aimPatterns = new ArrayList<>();
		aimPatterns.add(aimPattern);

		List<Path> compilationUnits = new ArrayList<>();
		compilationUnits.add(compilationUnitsPath.resolve("SimplePackageDecl.java"));

		AimPatternDetectionEngine aimPatternDetectionEngine = new AimPatternDetectionEngine(aimPatterns,
				compilationUnits, Java8FreemarkerTemplateParser.class, Java8FreemarkerTemplateLexer.class, grammarPath);

		AimPatternDetectionResult aimPatternDetectionResult = aimPatternDetectionEngine.detect();
	}
}
