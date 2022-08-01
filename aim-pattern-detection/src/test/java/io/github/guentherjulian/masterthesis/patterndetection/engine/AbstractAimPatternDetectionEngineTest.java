package io.github.guentherjulian.masterthesis.patterndetection.engine;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractAimPatternDetectionEngineTest {

	protected static Path resourcesPath;
	protected static Path templatesPath;
	protected static Path compilationUnitsPath;
	protected static Path grammarPath;

	@BeforeAll
	public static void setup() throws URISyntaxException {
		resourcesPath = Paths.get(AbstractAimPatternDetectionEngineTest.class.getProtectionDomain().getCodeSource()
				.getLocation().toURI());
		grammarPath = resourcesPath.resolve("grammars");
	}
}
