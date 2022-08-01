package io.github.guentherjulian.masterthesis.antlr4.parser.generator;

import java.io.IOException;
import java.nio.file.Path;

import org.antlr.v4.Tool;
import org.antlr.v4.tool.ErrorType;

public class Antlr4ParserGenerator {

	public void generateParser(Path grammarFile, String packageName, Path outputPath, String placeholderName) {
		String[] args = { grammarFile.toAbsolutePath().toString(), "-listener", "-o",
				outputPath.toAbsolutePath().toString(), "-package", packageName, "-long-messages",
				"-metalang-placeholder", placeholderName };

		Tool antlr4 = new Tool(args);
		if (args.length == 0) {
			antlr4.help();
		}

		try {
			antlr4.processGrammarsOnCommandLine();
		} finally {
			if (antlr4.log) {
				try {
					String logname = antlr4.logMgr.save();
					System.out.println("wrote " + logname);
				} catch (IOException ioe) {
					antlr4.errMgr.toolError(ErrorType.INTERNAL_ERROR, ioe);
				}
			}
		}

		if (antlr4.errMgr.getNumErrors() > 0) {
			antlr4.exit(1);
		}
	}
}
