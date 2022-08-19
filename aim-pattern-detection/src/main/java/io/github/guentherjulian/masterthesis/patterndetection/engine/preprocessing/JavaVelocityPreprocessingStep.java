package io.github.guentherjulian.masterthesis.patterndetection.engine.preprocessing;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaVelocityPreprocessingStep extends AbstractPreprocessingStep {

	private static String regexPlaceholder = "\\$(.+)";
	private Pattern patternPlaceholder;

	public JavaVelocityPreprocessingStep(Path input) throws Exception {
		super(input);

		this.patternPlaceholder = Pattern.compile(regexPlaceholder);
	}

	@Override
	public String process(String lineToProcess) {
		String returnValue = lineToProcess;

		boolean patternFound = true;
		Matcher matcher = this.patternPlaceholder.matcher(returnValue);
		patternFound = matcher.find();

		if (patternFound) {
			returnValue = replacePlaceholder(returnValue);
		}

		return returnValue;
	}

	private String replacePlaceholder(String str) {
		String regexSinlgePlaceholder = ".*(\\$(.+))";
		Pattern patternPlaceholder = Pattern.compile(regexSinlgePlaceholder);

		String replacedString = str;
		String[] arr = replacedString.split(" ");
		boolean isIfStatement = false;
		boolean isIfElseStatement = false;
		boolean isForeachStatement = false;

		isIfStatement = str.trim().startsWith("#if");
		isIfElseStatement = str.trim().startsWith("#elseif");
		isForeachStatement = str.trim().startsWith("#foreach");

		for (int i = 0; i < arr.length; i++) {
			if (!isIfStatement && !isIfElseStatement && !isForeachStatement) {
				Matcher matcherPlaceholder = patternPlaceholder.matcher(arr[i]);
				boolean matchFound = matcherPlaceholder.find();

				if (matchFound) {
					if (!isListPattern(arr[i])) {
						String match = matcherPlaceholder.group(0);
						String placeholder = matcherPlaceholder.group(1);
						String innerPlaceholder = matcherPlaceholder.group(2);
						String replacedPlaceholder;
						// $a --> ${a}
						if (innerPlaceholder.endsWith(";") || innerPlaceholder.endsWith(")")) {
							replacedPlaceholder = "${" + innerPlaceholder.substring(0, innerPlaceholder.length() - 1)
									+ "}" + innerPlaceholder.charAt(innerPlaceholder.length() - 1);
						} else {
							replacedPlaceholder = "${" + innerPlaceholder + "}";
						}

						arr[i] = match.replace(placeholder, replacedPlaceholder);
					} else {
						String[] listPatternParts = arr[i].split("\\.");
						for (int j = 0; j < listPatternParts.length; j++) {
							if (listPatternParts[j].startsWith("$")) {
								if (listPatternParts[j].endsWith(";")) {
									listPatternParts[j] = "${"
											+ listPatternParts[j].substring(1, listPatternParts[j].length() - 1) + "};";
								} else {
									listPatternParts[j] = "${" + listPatternParts[j].substring(1) + "}";
								}
							}
						}
						arr[i] = String.join(".", listPatternParts);
					}
				}
			}
		}
		replacedString = String.join(" ", arr);

		return replacedString;
	}

	private boolean isListPattern(String str) {
		return str.chars().filter(c -> c == '$').count() > 1;
	}

	private class PlaceholderReplacement {
		private String match;
		private String placeholder;
		private String innterPlaceholder;

		public PlaceholderReplacement(String match, String placeholder, String innterPlaceholder) {
			this.match = match;
			this.placeholder = placeholder;
			this.innterPlaceholder = innterPlaceholder;
		}

		public String getMatch() {
			return match;
		}

		public String getPlaceholder() {
			return placeholder;
		}

		public String getInnterPlaceholder() {
			return innterPlaceholder;
		}
	}
}
