package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FreeMarkerTemplatePreprocessor extends AbstractTemplatePreprocessor {

	private final String regexPrefix = ".*[ ]((\\w+)\\$\\{(.+)\\}).*";
	private final String regexSuffix = ".*((\\$\\{(.+)\\})(\\w+))[ ].*";

	private final Pattern prefixPattern = Pattern.compile(regexPrefix);
	private final Pattern suffixPattern = Pattern.compile(regexSuffix);

	@Override
	public String processTemplateLine(String lineToProcess) {
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
				String innerPlaceholder = matcher.group(3);
				String suffix = matcher.group(4);

				String replacedValue = "${" + innerPlaceholder + " + '" + suffix + "'}";
				returnValue = returnValue.replace(combinedPlaceholder, replacedValue);
			}
		} while (suffixFound);

		return returnValue;
	}

}
