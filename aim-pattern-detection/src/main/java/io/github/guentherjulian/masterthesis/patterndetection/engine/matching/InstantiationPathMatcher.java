package io.github.guentherjulian.masterthesis.patterndetection.engine.matching;

import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.metalanguage.MetaLanguagePattern;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.PlaceholderResolutionResult;
import io.github.guentherjulian.masterthesis.patterndetection.engine.placeholderresolution.PlaceholderResolver;
import io.github.guentherjulian.masterthesis.patterndetection.engine.utils.MathUtil;

public class InstantiationPathMatcher {

	static Pattern placeholderPatternWithoutPreAndSuffix = null;
	static Pattern placeholderPatternWithPreAndSuffix = null;

	public static InstantiationPathMatch match(String compilationUnitPath, String templateInstantiationPath,
			MetaLanguagePattern metaLanguagePattern, PlaceholderResolver placeholderResolver) {
		InstantiationPathMatch instantiationPathMatch = new InstantiationPathMatch();

		placeholderPatternWithoutPreAndSuffix = Pattern
				.compile("^" + metaLanguagePattern.getMetaLangPatternPlaceholder().pattern() + "$");
		placeholderPatternWithPreAndSuffix = Pattern
				.compile("(.*)" + metaLanguagePattern.getMetaLangPatternPlaceholder().pattern() + "(.*)");

		String separator = FileSystems.getDefault().getSeparator().replace("\\", "\\\\");
		String[] instantiationPathElements = templateInstantiationPath.split(separator);
		String[] compilationUnitPathElements = compilationUnitPath.split(separator);

		if (placeholderResolver != null) {
			for (int i = 0; i < instantiationPathElements.length; i++) {
				instantiationPathElements[i] = placeholderResolver
						.transformPlaceholderNotation(instantiationPathElements[i]);
			}
		}

		Matcher m = placeholderPatternWithoutPreAndSuffix.matcher(instantiationPathElements[0]);
		if (!m.find()) {
			// find first element matches the path
			String firstInstantiationPathElement = instantiationPathElements[0];
			if (firstInstantiationPathElement.endsWith(metaLanguagePattern.getMetaLangFileExtension())) {
				firstInstantiationPathElement = firstInstantiationPathElement.substring(0,
						firstInstantiationPathElement.lastIndexOf(metaLanguagePattern.getMetaLangFileExtension()) - 1);
			}

			for (int i = 0; i < compilationUnitPathElements.length; i++) {
				boolean isMatch = matchPathElement(firstInstantiationPathElement, compilationUnitPathElements[i],
						placeholderResolver, instantiationPathMatch.getPlaceholderSubstitutions());
				if (isMatch) {
					compilationUnitPathElements = Arrays.copyOfRange(compilationUnitPathElements, i,
							compilationUnitPathElements.length);
					break;
				}
			}
		}

		reverseArray(instantiationPathElements);
		reverseArray(compilationUnitPathElements);

		// ------------------------------------
		int numOfPlaceholders = 0;
		for (int i = 0; i < instantiationPathElements.length; i++) {
			Matcher matcher = placeholderPatternWithoutPreAndSuffix.matcher(instantiationPathElements[i]);
			if (matcher.find()) {
				numOfPlaceholders++;
			}
		}
		int maxSubstitutions = compilationUnitPathElements.length - instantiationPathElements.length
				+ numOfPlaceholders;
		int[][] combinations;
		if (maxSubstitutions == 0) {
			// if there are PHs, than all of them will just replace one element
			combinations = new int[1][numOfPlaceholders];
			Arrays.fill(combinations, new int[] { 1 });
		} else if (maxSubstitutions < numOfPlaceholders) {
			return instantiationPathMatch;
		} else {
			// n = maxSubstitutions, k = PHs; different buckets, same balls.
			combinations = MathUtil.multichooseMin1(maxSubstitutions, numOfPlaceholders);
		}

		boolean isPathMatching = false;

		List<Map<String, Set<String>>> possibleSubs = new ArrayList<>();
		for (int[] combination : combinations) {
			boolean isCombinationMatch = true;

			Map<String, Set<String>> variableSubstitutions = new HashMap<>();

			int observedPhIndex = 0;
			int j = 0;
			for (int i = 0; i < instantiationPathElements.length; i++) {
				if (!isCombinationMatch) {
					break;
				}

				String instantiationPathElement = instantiationPathElements[i];
				if (i == 0 && instantiationPathElement.endsWith(metaLanguagePattern.getMetaLangFileExtension())) {
					instantiationPathElement = instantiationPathElement.substring(0,
							instantiationPathElement.lastIndexOf(metaLanguagePattern.getMetaLangFileExtension()) - 1);
				}

				String compilationUnitPathElement = "";
				Matcher matcher = placeholderPatternWithoutPreAndSuffix.matcher(instantiationPathElement);
				boolean isPh = matcher.find();

				// ParseTreePath parseTreePathTemplate = (ParseTreePath)
				// parseTreeElementTemplate;
				// boolean isPh = parseTreePathTemplate.isMetaLanguageElement();
				// String parseTreePathTemplateName = parseTreePathTemplate.getName();

				// ParseTreeElement parseTreeElementCompilationUnit;
				String appMatch = "";
				boolean matches = false;
				int consumedAppElems = 0;

				do {
					if (j >= compilationUnitPathElements.length) {
						break;
					}

					compilationUnitPathElement = compilationUnitPathElements[j];

					appMatch += (appMatch.isEmpty() ? compilationUnitPathElement : "." + compilationUnitPathElement);
					consumedAppElems++;

					if (isPh) {
						if (combination[observedPhIndex] > consumedAppElems) {
							j++;
							continue;
						} else {
							// LOGGER.info("Match tokens {} --> {}", parseTreePathTemplate.getText(),
							// appMatch);
							// variableSubstitutions.put(instantiationPathElement,
							// new HashSet<String>(Arrays.asList(appMatch)));
							matches = matchPathElement(instantiationPathElement, appMatch, placeholderResolver,
									variableSubstitutions);
							isCombinationMatch = matches;
							j++;
							observedPhIndex++;
							break;
						}
					} else {
						matches = matchPathElement(instantiationPathElement, compilationUnitPathElement,
								placeholderResolver, variableSubstitutions);
					}

					if (!matches) {
						if (isPh) {
							j++;
						} else {
							isCombinationMatch = false;
							variableSubstitutions.clear();
							break;
						}
					} else {
						// LOGGER.info("Match tokens {} --> {}", parseTreePathTemplate.getText(),
						// appMatch);
						j++;
					}
				} while (!matches && isCombinationMatch);
			}

			if (isCombinationMatch) {
				isPathMatching = true;

				if (!variableSubstitutions.isEmpty()) {
					for (Entry<String, Set<String>> variableSubstitution : variableSubstitutions.entrySet()) {
						Set<String> newValues = new HashSet<>();
						for (String value : variableSubstitution.getValue()) {
							if (value.contains(".")) {
								String[] elements = value.split("\\.");
								reverseArray(elements);
								newValues.add(String.join(".", elements));
							} else {
								newValues.add(value);
							}
						}
						variableSubstitution.setValue(newValues);
					}
					possibleSubs.add(variableSubstitutions);
				}
			}

		}

		if (!possibleSubs.isEmpty()) {
			for (Map<String, Set<String>> possibleSub : possibleSubs) {
				for (Entry<String, Set<String>> entry : possibleSub.entrySet()) {
					String key = entry.getKey();
					Set<String> value = entry.getValue();

					if (instantiationPathMatch.getPlaceholderSubstitutions().containsKey(key)) {
						Set<String> alreadyFoundSubstitutions = instantiationPathMatch.getPlaceholderSubstitutions()
								.get(key);
						alreadyFoundSubstitutions.addAll(value);
						instantiationPathMatch.getPlaceholderSubstitutions().put(key, alreadyFoundSubstitutions);
					} else {
						instantiationPathMatch.getPlaceholderSubstitutions().put(key, value);
					}
				}
			}
		}

		// -----------------------------

		/*
		 * boolean isMatch = false; for (int i = 0; i <
		 * instantiationPathElements.length; i++) {
		 * 
		 * String instantiationPathElement = instantiationPathElements[i]; String
		 * compilationUnitPathElement = compilationUnitPathElements[i];
		 * 
		 * if (i == 0 && instantiationPathElement.endsWith(metaLanguagePattern.
		 * getMetaLangFileExtension())) { instantiationPathElement =
		 * instantiationPathElement.substring(0,
		 * instantiationPathElement.lastIndexOf(metaLanguagePattern.
		 * getMetaLangFileExtension()) - 1); }
		 * 
		 * if (instantiationPathElement.equals(compilationUnitPathElement)) { isMatch =
		 * true; continue; } else { // check if is a placeholder element Matcher matcher
		 * = placeholderPatternWithPreAndSuffix.matcher(instantiationPathElement); if
		 * (matcher.find()) { String placeholderPrefix = matcher.group(1); String
		 * placeholder = matcher.group(2); String placeholderPostfix = matcher.group(3);
		 * 
		 * String substitution = compilationUnitPathElement; if
		 * (!placeholderPrefix.isBlank()) { substitution =
		 * substitution.substring(placeholderPrefix.length()); } if
		 * (!placeholderPostfix.isBlank()) { if (substitution.length() >
		 * placeholderPostfix.length()) { substitution = substitution.substring(0,
		 * substitution.length() - placeholderPostfix.length()); } }
		 * 
		 * PlaceholderResolutionResult placeholderResolutionResult; try {
		 * placeholderResolutionResult =
		 * placeholderResolver.resolvePlaceholder(placeholder, substitution); } catch
		 * (Exception e) { e.printStackTrace(); isMatch = false; break; }
		 * 
		 * placeholder = placeholderResolutionResult.getPlaceholder(); Set<String>
		 * possibleSubstitutions = placeholderResolutionResult.getSubstitutions();
		 * boolean placeholderAdded = addPlaceholderSubstitution(placeholder,
		 * possibleSubstitutions, instantiationPathMatch.getPlaceholderSubstitutions());
		 * 
		 * isMatch = placeholderAdded; if (!isMatch) { break; } } else { isMatch =
		 * false; break; } } }
		 */

		instantiationPathMatch.setMatch(isPathMatching);
		return instantiationPathMatch;
	}

	private static boolean matchPathElement(String instantiationPathElement, String compilationUnitPathElement,
			PlaceholderResolver placeholderResolver, Map<String, Set<String>> currentPlaceholderSubstitutions) {
		boolean isMatch = false;

		if (instantiationPathElement.equals(compilationUnitPathElement)) {
			isMatch = true;
		} else {
			// check if is a placeholder element
			Matcher matcher = placeholderPatternWithPreAndSuffix.matcher(instantiationPathElement);
			if (matcher.find()) {
				String placeholderPrefix = matcher.group(1);
				String placeholder = matcher.group(2);
				String placeholderPostfix = matcher.group(3);

				String substitution = compilationUnitPathElement;
				if (!placeholderPrefix.isBlank()) {
					if (!substitution.contains(placeholderPrefix)) {
						return false;
					}
					substitution = substitution.substring(placeholderPrefix.length());
				}
				if (!placeholderPostfix.isBlank()) {
					if (!substitution.contains(placeholderPostfix)) {
						return false;
					}
					if (substitution.length() > placeholderPostfix.length()) {
						substitution = substitution.substring(0, substitution.length() - placeholderPostfix.length());
					}
				}

				PlaceholderResolutionResult placeholderResolutionResult = null;
				try {
					placeholderResolutionResult = placeholderResolver.resolvePlaceholder(placeholder, substitution);
				} catch (Exception e) {
					e.printStackTrace();
					isMatch = false;
				}

				placeholder = placeholderResolutionResult.getPlaceholder();
				Set<String> possibleSubstitutions = placeholderResolutionResult.getSubstitutions();
				boolean placeholderAdded = addPlaceholderSubstitution(placeholder, possibleSubstitutions,
						currentPlaceholderSubstitutions);

				isMatch = placeholderAdded;
			} else {
				isMatch = false;
			}
		}

		return isMatch;
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
