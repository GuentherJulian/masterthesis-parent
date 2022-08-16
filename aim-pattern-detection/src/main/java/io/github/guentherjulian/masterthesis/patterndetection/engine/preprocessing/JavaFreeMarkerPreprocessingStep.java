package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaFreeMarkerPreprocessingStep extends AbstractPreprocessingStep {

	private static final String REGEX_PREFIX = ".*[ ]((\\w+)\\$\\{(.+)\\}).*";
	private static final String REGEX_SUFFIX = ".*((\\$\\{(.+)\\})(\\w+))[ ].*";

	private Pattern prefixPattern;
	private Pattern suffixPattern;

	public JavaFreeMarkerPreprocessingStep(Path input) throws Exception {
		super(input);

		this.prefixPattern = Pattern.compile(REGEX_PREFIX);
		this.suffixPattern = Pattern.compile(REGEX_SUFFIX);
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

		boolean suffixFound = true;
		do {
			Matcher matcher = this.suffixPattern.matcher(returnValue);
			suffixFound = matcher.find();

			if (suffixFound) {
				String combinedPlaceholder = matcher.group(1);
				String placeholder = matcher.group(2);
				String innerPlaceholder = matcher.group(3);
				String suffix = matcher.group(4);

				String replacedValue = "${" + innerPlaceholder + " + '" + suffix + "'}";
				returnValue = returnValue.replace(combinedPlaceholder, replacedValue);
			}
		} while (suffixFound);

		return returnValue;
	}

}
