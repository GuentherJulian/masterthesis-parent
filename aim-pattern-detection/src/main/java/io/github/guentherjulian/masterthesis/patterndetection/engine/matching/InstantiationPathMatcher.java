package io.github.guentherjulian.masterthesis.patterndetection.engine.matching;

import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstantiationPathMatcher {
	public static InstantiationPathMatch match(String compilationUnitPath, String templateInstantiationPath,
			String placeholderRegex) {
		InstantiationPathMatch instantiationPathMatch = new InstantiationPathMatch();

		String separator = FileSystems.getDefault().getSeparator().replace("\\", "\\\\");
		String[] instantiationPathElements = templateInstantiationPath.split(separator);
		String[] compilationUnitPathElements = compilationUnitPath.split(separator);

		reverseArray(instantiationPathElements);
		reverseArray(compilationUnitPathElements);

		boolean isMatch = false;
		for (int i = 0; i < instantiationPathElements.length; i++) {
			if (instantiationPathElements[i].equals(compilationUnitPathElements[i])) {
				isMatch = true;
				continue;
			} else {
				// check if is a placeholder element
				Pattern pattern = Pattern.compile(placeholderRegex);
				Matcher matcher = pattern.matcher(instantiationPathElements[i]);
				if (matcher.find()) {
					String placeholder = matcher.group(1);
					isMatch = true;
				} else {
					isMatch = false;
					break;
				}
			}
		}

		instantiationPathMatch.setMatch(isMatch);
		return instantiationPathMatch;
	}

	private static void reverseArray(String[] originalArray) {
		Collections.reverse(Arrays.asList(originalArray));
	}
}
