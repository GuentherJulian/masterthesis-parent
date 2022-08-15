package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaFreeMarkerPreprocessingStep extends AbstractPreprocessingStep {

	private static final String REGEX_PREFIX = ".*[ ]((\\w+)\\$\\{(.+)\\}).*";

	private Pattern prefixPattern;

	public JavaFreeMarkerPreprocessingStep(Path input) throws Exception {
		super(input);

		this.prefixPattern = Pattern.compile(REGEX_PREFIX);
	}

	@Override
	public String process(String lineToProcess) {
		String returnValue = lineToProcess;
		boolean prefixFound = true;
		do {
			Matcher matcher = this.prefixPattern.matcher(returnValue);
			prefixFound = matcher.find();

			if (prefixFound) {
				String combinedPlaceholder = matcher.group(1);
				String prefix = matcher.group(2);
				String innerPlaceholder = matcher.group(3);

				String replacedValue = "${'" + prefix + "' + " + innerPlaceholder + "}";
				returnValue = returnValue.replace(combinedPlaceholder, replacedValue);
			}
		} while (prefixFound);

		return returnValue;
	}

}
