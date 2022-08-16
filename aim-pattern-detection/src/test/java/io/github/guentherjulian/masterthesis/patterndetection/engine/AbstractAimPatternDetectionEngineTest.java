package io.github.guentherjulian.masterthesis.patterndetection.engine;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractAimPatternDetectionEngineTest {

	protected static Path resourcesPath;
	protected static Path templatesPath;
	protected static Path compilationUnitsPath;
	protected static Path grammarPath;

	protected static Class<? extends Parser> parserClass;
	protected static Class<? extends Lexer> lexerClass;

	@BeforeAll
	public static void setup() throws URISyntaxException {
		resourcesPath = Paths.get(AbstractAimPatternDetectionEngineTest.class.getProtectionDomain().getCodeSource()
				.getLocation().toURI());
		grammarPath = resourcesPath.resolve("grammars");
	}
}
