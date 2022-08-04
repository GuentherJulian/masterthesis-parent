package io.github.guentherjulian.masterthesis.patterndetection.engine.matching;

import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguagePattern;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.PlaceholderResolutionResult;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.PlaceholderResolver;

public class InstantiationPathMatcher {
	public static InstantiationPathMatch match(String compilationUnitPath, String templateInstantiationPath,
			MetaLanguagePattern metaLanguagePattern, PlaceholderResolver placeholderResolver) {
		InstantiationPathMatch instantiationPathMatch = new InstantiationPathMatch();

		String separator = FileSystems.getDefault().getSeparator().replace("\\", "\\\\");
		String[] instantiationPathElements = templateInstantiationPath.split(separator);
		String[] compilationUnitPathElements = compilationUnitPath.split(separator);

		reverseArray(instantiationPathElements);
		reverseArray(compilationUnitPathElements);

		String placeholderRegex = "(.*)" + metaLanguagePattern.getMetaLangPatternPlaceholder() + "(.*)";
		boolean isMatch = false;
		for (int i = 0; i < instantiationPathElements.length; i++) {

			String instantiationPathElement = instantiationPathElements[i];
			String compilationUnitPathElement = compilationUnitPathElements[i];

			if (i == 0 && instantiationPathElement.endsWith(metaLanguagePattern.getMetaLangFileExtension())) {
				instantiationPathElement = instantiationPathElement.substring(0,
						instantiationPathElement.lastIndexOf(metaLanguagePattern.getMetaLangFileExtension()) - 1);
			}

			if (instantiationPathElement.equals(compilationUnitPathElement)) {
				isMatch = true;
				continue;
			} else {
				// check if is a placeholder element
				Pattern pattern = Pattern.compile(placeholderRegex);
				Matcher matcher = pattern.matcher(instantiationPathElement);
				if (matcher.find()) {
					String placeholderPrefix = matcher.group(1);
					String placeholder = matcher.group(2);
					String placeholderPostfix = matcher.group(3);

					String substitution = compilationUnitPathElement;
					if (!placeholderPrefix.isBlank()) {
						substitution = substitution.substring(placeholderPrefix.length());
					}
					if (!placeholderPostfix.isBlank()) {
						substitution = substitution.substring(0, substitution.length() - placeholderPostfix.length());
					}

					PlaceholderResolutionResult placeholderResolutionResult;
					try {
						placeholderResolutionResult = placeholderResolver.resolvePlaceholder(placeholder, substitution);
					} catch (Exception e) {
						e.printStackTrace();
						isMatch = false;
						break;
					}

					placeholder = placeholderResolutionResult.getPlaceholder();
					Set<String> possibleSubstitutions = placeholderResolutionResult.getSubstitutions();
					boolean placeholderAdded = addPlaceholderSubstitution(placeholder, possibleSubstitutions,
							instantiationPathMatch.getPlaceholderSubstitutions());

					isMatch = placeholderAdded;
					if (!isMatch) {
						break;
					}
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

	private static boolean addPlaceholderSubstitution(String placeholder, Set<String> possibleSubstitutions,
			Map<String, Set<String>> placeholderSubstitutions) {

		if (!placeholderSubstitutions.containsKey(placeholder)) {
			placeholderSubstitutions.put(placeholder, possibleSubstitutions);
		} else {
			Set<String> alreadyCoveredSubstitutions = placeholderSubstitutions.get(placeholder);
			Set<String> newSubstitutions = new HashSet<>();
			for (String alreadyCoveredSubstitution : alreadyCoveredSubstitutions) {
				if (possibleSubstitutions.contains(alreadyCoveredSubstitution)) {
					newSubstitutions.add(alreadyCoveredSubstitution);
				}
			}

			if (newSubstitutions.isEmpty()) {
				return false;
			} else {
				placeholderSubstitutions.put(placeholder, newSubstitutions);
			}
		}

		return true;
	}
}
