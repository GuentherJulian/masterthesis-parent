package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractPreprocessingTest {

	protected static Path resourcesPath;
	protected static Path grammarPath;

	protected static Class<? extends Parser> parserClass;
	protected static Class<? extends Lexer> lexerClass;

	protected static String parserStartRule;

	@BeforeAll
	public static void setup() throws URISyntaxException {
		resourcesPath = Paths
				.get(AbstractPreprocessingTest.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		grammarPath = resourcesPath.resolve("grammars");
	}

	protected Parser createParser(Path inputPath) throws IOException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		InputStream inputStream = new FileInputStream(inputPath.toFile());
		CharStream charStream = CharStreams.fromStream(inputStream);

		Constructor<? extends Lexer> lexerConstructor = lexerClass.getConstructor(CharStream.class);
		Lexer lexer = lexerConstructor.newInstance(charStream);

		TokenStream tokenStream = new CommonTokenStream(lexer);
		Constructor<? extends Parser> parserConstructor = parserClass.getConstructor(TokenStream.class);
		Parser parser = parserConstructor.newInstance(tokenStream);

		return parser;
	}

	protected Parser createParser(byte[] byteInput) throws IOException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		InputStream inputStream = new ByteArrayInputStream(byteInput);
		CharStream charStream = CharStreams.fromStream(inputStream);

		Constructor<? extends Lexer> lexerConstructor = lexerClass.getConstructor(CharStream.class);
		Lexer lexer = lexerConstructor.newInstance(charStream);

		TokenStream tokenStream = new CommonTokenStream(lexer);
		Constructor<? extends Parser> parserConstructor = parserClass.getConstructor(TokenStream.class);
		Parser parser = parserConstructor.newInstance(tokenStream);

		return parser;
	}

	protected ParserRuleContext parse(Parser parser, String parseRule) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method = parser.getClass().getMethod(parseRule);
		return (ParserRuleContext) method.invoke(parser);
	}
}
