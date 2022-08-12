package io.github.guentherjulian.masterthesis.patterndetection.engine.matching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.guentherjulian.masterthesis.patterndetection.engine.exception.NoMatchException;
import io.github.guentherjulian.masterthesis.patterndetection.engine.exception.PlaceholderClashException;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguageLexerRules;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguagePattern;
import io.github.guentherjulian.masterthesis.patterndetection.engine.utils.MathUtil;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ListType;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.MetaLanguageElement;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTree;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTreeElement;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTreePath;
import io.github.guentherjulian.masterthesis.patterndetection.parsing.ParseTreePathList;

public class ParseTreeMatcher {

	private static final Logger LOGGER = LogManager.getLogger(ParseTreeMatcher.class);

	private TreeMatch match;
	private ParseTree aimPatternTemplateParseTree;
	private ParseTree compilationUnitParseTree;
	private Map<String, Set<String>> placeholderSubstitutions;
	private List<Match> matches;
	private MetaLanguageLexerRules metaLanguageLexerRules;
	private MetaLanguagePattern metaLanguagePattern;

	public ParseTreeMatcher(ParseTree compilationUnitParseTree, ParseTree aimPatternTemplateParseTree,
			MetaLanguageLexerRules metaLanguageLexerRules, MetaLanguagePattern metaLanguagePattern) {
		this.compilationUnitParseTree = compilationUnitParseTree;
		this.aimPatternTemplateParseTree = aimPatternTemplateParseTree;
		this.metaLanguageLexerRules = metaLanguageLexerRules;
		this.metaLanguagePattern = metaLanguagePattern;
	}

	public TreeMatch match(Map<String, Set<String>> placeholderSubstitutions) {
		// TODO implement correct tree matching
		long startTime = System.nanoTime();

		this.matches = new ArrayList<>();
		if (placeholderSubstitutions != null && placeholderSubstitutions.isEmpty()) {
			this.placeholderSubstitutions = placeholderSubstitutions;
		} else {
			this.placeholderSubstitutions = new HashMap<>();
		}

		this.match = new TreeMatch();
		this.match.setTemplateParseTree(this.aimPatternTemplateParseTree);
		this.match.setCompilationUnitParseTree(this.compilationUnitParseTree);
		this.match.setPlaceholderSubstitutions(this.placeholderSubstitutions);
		this.match.setMatches(this.matches);
		this.match.setMatch(true);

		try {
			matchPathList(this.aimPatternTemplateParseTree.getParseTreePathList(),
					this.compilationUnitParseTree.getParseTreePathList());
		} catch (RuntimeException e) {
			// e.printStackTrace();
			LOGGER.error(e.getMessage());
		} catch (NoMatchException e) {
			this.match.setMatch(false);
			this.match.setException(e);
		}

		long endTime = System.nanoTime();
		LOGGER.info("Finished matching parse trees (took {} ns, {} ms)", (endTime - startTime),
				((endTime - startTime) / 1e6));

		return this.match;
	}

	private boolean matchPathList(ParseTreePathList templateParseTreePathList,
			ParseTreePathList compilationUnitParseTreePathList) throws NoMatchException {

		// separated into unordered and ordered nodes
		List<ParseTreeElement> unorderedTemplatePaths = new ArrayList<>();
		List<ParseTreeElement> orderedTemplatePaths = new ArrayList<>();
		for (ParseTreeElement parseTreeElement : templateParseTreePathList) {
			if ((parseTreeElement instanceof ParseTreePathList && !((ParseTreePathList) parseTreeElement).isOrdered())
					|| (parseTreeElement instanceof ParseTreePath
							&& ((ParseTreePath) parseTreeElement).isNonOrderingNode())) {
				unorderedTemplatePaths.add(parseTreeElement);
			} else if (parseTreeElement instanceof ParseTreePath || (parseTreeElement instanceof ParseTreePathList
					&& ((ParseTreePathList) parseTreeElement).isOrdered())) {
				orderedTemplatePaths.add(parseTreeElement);
			}

		}

		List<ParseTreeElement> unorderedCompilationUnitPaths = new ArrayList<>();
		List<ParseTreeElement> orderedCompilationUnitPaths = new ArrayList<>();
		for (ParseTreeElement parseTreeElement : compilationUnitParseTreePathList) {
			if (parseTreeElement instanceof ParseTreePathList && !((ParseTreePathList) parseTreeElement).isOrdered()) {
				unorderedCompilationUnitPaths.add(parseTreeElement);
			} else if (parseTreeElement instanceof ParseTreePath || (parseTreeElement instanceof ParseTreePathList
					&& ((ParseTreePathList) parseTreeElement).isOrdered())) {
				orderedCompilationUnitPaths.add(parseTreeElement);
			}

		}

		try {
			matchOrderedPaths(orderedTemplatePaths, orderedCompilationUnitPaths);
			matchUnorderedPaths(unorderedTemplatePaths, unorderedCompilationUnitPaths);
		} catch (RuntimeException e) {
			// e.printStackTrace();
			LOGGER.error(e.getMessage());
		} catch (NoMatchException e) {
			// e.printStackTrace();
			LOGGER.error(e.getMessage());
			return false;
		}

		return true;
	}

	private boolean matchOrderedPaths(List<ParseTreeElement> orderedTemplatePaths,
			List<ParseTreeElement> orderedCompilationUnitPaths) throws NoMatchException {

		int j = 0;
		for (int i = 0; i < orderedTemplatePaths.size(); i++) {
			ParseTreeElement parseTreeElementTemplate = orderedTemplatePaths.get(i);

			ParseTreeElement parseTreeElementCompilationUnit;
			boolean matches = false;
			do {
				if (j >= orderedTemplatePaths.size()) {
					throw new NoMatchException(String.format("No match for ordered paths: %s -> %s",
							orderedTemplatePaths, orderedCompilationUnitPaths));
				}

				parseTreeElementCompilationUnit = orderedCompilationUnitPaths.get(j);

				matches = true;
				try {
					matchPath(parseTreeElementTemplate, parseTreeElementCompilationUnit);
				} catch (RuntimeException e) {
					matches = false;
				}

				if (!matches) {
					j++;
				} else {
					j++;
				}
			} while (!matches);
		}

		return true;
	}

	private boolean matchUnorderedPaths(List<ParseTreeElement> unorderedTemplatePaths,
			List<ParseTreeElement> unorderedCompilationUnitPaths) throws NoMatchException {

		Iterator<ParseTreeElement> iterator = unorderedTemplatePaths.iterator();
		while (iterator.hasNext()) {
			ParseTreeElement parseTreeElementTemplate = iterator.next();

			for (ParseTreeElement parseTreeElementCompilationUnit : unorderedCompilationUnitPaths) {
				try {
					if (parseTreeElementTemplate instanceof ParseTreePath
							&& parseTreeElementCompilationUnit instanceof ParseTreePath) {
						matchPath(parseTreeElementTemplate, parseTreeElementCompilationUnit);
					}
					if (parseTreeElementTemplate instanceof ParseTreePathList
							&& parseTreeElementCompilationUnit instanceof ParseTreePathList) {
						LOGGER.info("matchUnorderedPaths: {}, {}, {}",
								((ParseTreePathList) parseTreeElementTemplate).getType(),
								((ParseTreePathList) parseTreeElementTemplate).getHint(),
								((ParseTreePathList) parseTreeElementTemplate).isMetaLang());
						// TODO evaluate return value
						if (((ParseTreePathList) parseTreeElementTemplate).isMetaLang()) {
							matchMetaLanguagePathList((ParseTreePathList) parseTreeElementTemplate,
									(ParseTreePathList) parseTreeElementCompilationUnit);
						} else {
							matchPathList((ParseTreePathList) parseTreeElementTemplate,
									(ParseTreePathList) parseTreeElementCompilationUnit);
						}
					}
				} catch (RuntimeException e) {
					// e.printStackTrace();
					LOGGER.error(e.getMessage());
				} catch (NoMatchException e) {
					// e.printStackTrace();
					LOGGER.error(e.getMessage());
					return false;
				}

				// TODO evaluate match
			}
		}

		return true;
	}

	private boolean matchMetaLanguagePathList(ParseTreePathList parseTreePathListTemplate,
			ParseTreePathList parseTreePathListCompilationUnit) throws NoMatchException {

		ListType type = parseTreePathListTemplate.getType();
		LOGGER.info("{}", type);
		for (ParseTreeElement parseTreeElement : parseTreePathListTemplate) {
			if (parseTreeElement instanceof ParseTreePath) {
				LOGGER.info("ParseTreePath");
			} else {
				ParseTreePathList elem = (ParseTreePathList) parseTreeElement;
				LOGGER.info("ParseTreePathList {}", elem.getType());
				ParseTreeElement firstChild = elem.get(0);
				if (firstChild instanceof ParseTreePath) {
					LOGGER.info("First Child ParseTreePath {}", ((ParseTreePath) firstChild).getText());
				} else {
					LOGGER.info("First Child ParseTreePathList {}", ((ParseTreePathList) firstChild).getType());
				}
			}
		}

		ListType listType = parseTreePathListTemplate.getType();
		if (listType == ListType.ALTERNATIVE) {
			List<String> conditions = new ArrayList<>();
			List<Boolean> matches = new ArrayList<>();
			for (ParseTreeElement parseTreeElementChild : parseTreePathListTemplate) {
				String rawHint = ((ParseTreePathList) parseTreeElementChild).getHint();
				MetaLanguageElement metaLanguageElement = ((ParseTreePathList) parseTreeElementChild)
						.getMetaLanguageElement();

				String condition = getCondition(rawHint, metaLanguageElement);
				// TODO fix, cause error for else token
				// if (condition.isEmpty()) {
				// throw new NoMatchException("Unable to get condition for " + rawHint);
				// }

				boolean match = true;
				try {
					if (((ParseTreePathList) parseTreeElementChild).isMetaLang()) {
						match = matchMetaLanguagePathList((ParseTreePathList) parseTreeElementChild,
								parseTreePathListCompilationUnit);
					} else {
						match = matchPathList((ParseTreePathList) parseTreeElementChild,
								parseTreePathListCompilationUnit);
					}
				} catch (NoMatchException e) {
					// do not return, try other paths
					match = false;
				}
				matches.add(match);
			}

			// Check matches
			int countMatches = (int) matches.stream().filter(match -> match).count();
			LOGGER.info("{}, true = {}", matches, countMatches);
			if (countMatches == 0) {
				throw new NoMatchException("No of the alternative match!" + parseTreePathListTemplate);
			}

		} else if (listType == ListType.OPTIONAL || listType == listType.ARBITRARY) {

		} else if (listType == ListType.ATOMIC) {
			ParseTreePathList child = (ParseTreePathList) parseTreePathListTemplate.get(0);
			if (child.isMetaLang()) {
				return matchMetaLanguagePathList(child, parseTreePathListCompilationUnit);
			} else {
				return matchPathList(child, parseTreePathListCompilationUnit);
			}
		}

		return true;
	}

	private String getCondition(String rawHint, MetaLanguageElement metaLanguageElement) {
		Pattern pattern = null;
		String condition = "";
		if (metaLanguageElement == MetaLanguageElement.IF) {
			pattern = this.metaLanguagePattern.getMetaLangPatternIf();
		} else if (metaLanguageElement == MetaLanguageElement.IF_ELSE) {
			pattern = this.metaLanguagePattern.getMetaLangPatternIfElse();
		} else if (metaLanguageElement == MetaLanguageElement.ELSE) {
			pattern = this.metaLanguagePattern.getMetaLangPatternElse();
		} else if (metaLanguageElement == MetaLanguageElement.IF_CLOSE) {
			pattern = this.metaLanguagePattern.getMetaLangPatternIfClose();
		} else if (metaLanguageElement == MetaLanguageElement.LIST) {
			pattern = this.metaLanguagePattern.getMetaLangPatternList();
		} else if (metaLanguageElement == MetaLanguageElement.LIST_CLOSE) {
			pattern = this.metaLanguagePattern.getMetaLangPatternListClose();
		}

		if (pattern != null) {
			Matcher matcher = pattern.matcher(rawHint);
			boolean match = matcher.find();
			if (match) {
				String group = "";
				try {
					group = matcher.group(1).trim();
				} catch (Exception e) {
					// ignore, return empty string instead
				}
				condition = group;
			}
		}
		return condition;
	}

	private boolean matchPath(ParseTreeElement templatePathElement, ParseTreeElement compilationUnitPathElement)
			throws NoMatchException {

		if (templatePathElement instanceof ParseTreePath && compilationUnitPathElement instanceof ParseTreePath) {
			ParseTreePath parseTreePathTemplate = (ParseTreePath) templatePathElement;
			ParseTreePath parseTreePathCompilationUnit = (ParseTreePath) compilationUnitPathElement;

			if (!parseTreePathTemplate.isMetaLanguageElement()) {
				if (parseTreePathTemplate.getText().equals(parseTreePathCompilationUnit.getText())) {
					this.registerNewMatch(parseTreePathTemplate, parseTreePathCompilationUnit);
				} else if (!parseTreePathTemplate.containsMetaLanguage()) {
					throw new RuntimeException("Could not find " + parseTreePathTemplate.getPath() + " in "
							+ parseTreePathCompilationUnit.getPath());
				} else if (parseTreePathCompilationUnit.getPureObjLangPath()
						.equals(parseTreePathTemplate.getPureObjLangPath())
						&& parseTreePathCompilationUnit.getText().equals(parseTreePathTemplate.getText())) {
					this.registerNewMatch(parseTreePathTemplate, parseTreePathCompilationUnit);
				} else {
					throw new RuntimeException("Could not find " + parseTreePathTemplate.getPath() + " in "
							+ parseTreePathCompilationUnit.getPath());
				}
			} else {
				// Metalanguage code in template
				ParseTreePath currentParseTreePathTemplate = parseTreePathTemplate;
				ParseTreePath currentParseTreePathCompilationUnit = parseTreePathCompilationUnit;
				boolean placeholderFound = false;
				do {
					if (currentParseTreePathCompilationUnit == null) {
						throw new RuntimeException("Could not find " + parseTreePathTemplate.getPath() + " in "
								+ parseTreePathCompilationUnit.getPath());
					}

					if (currentParseTreePathTemplate.getName().equals(currentParseTreePathCompilationUnit.getName())) {
						currentParseTreePathCompilationUnit = currentParseTreePathCompilationUnit.getParent();
					} else if (currentParseTreePathTemplate.isMetaLanguageElement()) {
						String parserRuleToCheck = Character
								.toUpperCase(this.metaLanguageLexerRules.getMetaLanguagePrefix().charAt(0))
								+ this.metaLanguageLexerRules.getMetaLanguagePrefix().substring(1)
								+ currentParseTreePathCompilationUnit.getName() + "Context";
						if (currentParseTreePathTemplate.getParent().getName().matches(parserRuleToCheck)) {
							this.registerNewPlaceholderSubstitution(currentParseTreePathTemplate.getText(),
									currentParseTreePathCompilationUnit.getText(), true);
							this.registerNewMatch(currentParseTreePathTemplate, currentParseTreePathCompilationUnit);
							placeholderFound = true;
							break;
						}
					} else {
						currentParseTreePathCompilationUnit = currentParseTreePathCompilationUnit.getParent();
					}

					currentParseTreePathTemplate = currentParseTreePathTemplate.getParent();
				} while (currentParseTreePathTemplate != null);

				if (!placeholderFound) {
					this.registerNewPlaceholderSubstitution(parseTreePathTemplate.getText(),
							parseTreePathCompilationUnit.getText(), true);
					this.registerNewMatch(parseTreePathTemplate, parseTreePathCompilationUnit);
				}
			}
		} else if (templatePathElement instanceof ParseTreePathList
				&& compilationUnitPathElement instanceof ParseTreePathList) {
			ParseTreePathList parseTreePathListTemplate = (ParseTreePathList) templatePathElement;
			ParseTreePathList parseTreePathListCompilationUnit = (ParseTreePathList) compilationUnitPathElement;

			// matchOrderedPaths(parseTreePathListTemplate,
			// parseTreePathListCompilationUnit);
			if (parseTreePathListTemplate.isAtomic() && parseTreePathListCompilationUnit.isAtomic()) {
				matchOrderedPaths(parseTreePathListTemplate, parseTreePathListCompilationUnit);
			} else if (parseTreePathListTemplate.isListPattern() && parseTreePathListCompilationUnit.isListPattern()) {
				matchListPattern(parseTreePathListTemplate, parseTreePathListCompilationUnit);
			} else if (!parseTreePathListTemplate.isAtomic() & !parseTreePathListCompilationUnit.isAtomic()) {
				matchOrderedPaths(parseTreePathListTemplate, parseTreePathListCompilationUnit);
			} else {
				throw new NoMatchException("Unable to match the parse tree lists");
			}
		}
		return true;
	}

	private boolean matchListPattern(ParseTreePathList parseTreePathListTemplate,
			ParseTreePathList parseTreePathListCompilationUnit) throws NoMatchException {

		int numOfPlaceholders = (int) parseTreePathListTemplate.stream()
				.filter(path -> path instanceof ParseTreePath && ((ParseTreePath) path).containsMetaLanguage()).count();
		int maxSubstitutions = parseTreePathListCompilationUnit.size() - parseTreePathListTemplate.size()
				+ numOfPlaceholders;

		// will be small enough to cast to int
		int[][] combinations;
		if (maxSubstitutions == 0) {
			// if there are PHs, than all of them will just replace one element
			combinations = new int[1][numOfPlaceholders];
			Arrays.fill(combinations, new int[] { 1 });
		} else if (maxSubstitutions < numOfPlaceholders) {
			throw new NoMatchException("App path list to short. Could not instantiate all placeholders.");
		} else {
			// n = maxSubstitutions, k = PHs; different buckets, same balls.
			combinations = MathUtil.multichooseMin1(maxSubstitutions, numOfPlaceholders);
		}

		List<Map<String, String>> possibleSubstitutions = new ArrayList<>();
		for (int[] combination : combinations) {
			Map<String, String> variableSubstitutions = new HashMap<>();

			try {
				int observedPhIndex = 0;
				int j = 0;
				for (int i = 0; i < parseTreePathListTemplate.size(); i++) {

					ParseTreeElement parseTreeElementTemplate = parseTreePathListTemplate.get(i);
					if (!(parseTreeElementTemplate instanceof ParseTreePath)) {
						throw new IllegalStateException("ParseTreePathList not supported yet!");
					}

					ParseTreePath parseTreePathTemplate = (ParseTreePath) parseTreeElementTemplate;
					boolean isPh = parseTreePathTemplate.isMetaLanguageElement();
					String parseTreePathTemplateName = parseTreePathTemplate.getName();

					ParseTreeElement parseTreeElementCompilationUnit;
					String appMatch = "";
					boolean matches = false;
					int consumedAppElems = 0;

					do {
						if (j >= parseTreePathListCompilationUnit.size()) {
							// template could not be found entirely in app!
							throw new NoMatchException("Could not find path " + parseTreePathTemplate.getPath());
						}

						parseTreeElementCompilationUnit = parseTreePathListCompilationUnit.get(j);
						if (!(parseTreeElementCompilationUnit instanceof ParseTreePath)) {
							throw new IllegalStateException("ParseTreePathList not supported yet!");
						}

						ParseTreePath parseTreePathCompilationUnit = (ParseTreePath) parseTreeElementCompilationUnit;
						appMatch += parseTreePathCompilationUnit.getText();
						consumedAppElems++;

						if (isPh) {
							if (combination[observedPhIndex] > consumedAppElems) {
								j++;
								continue;
							} else {
								// LOGGER.info("Match tokens {} --> {}", parseTreePathTemplate.getText(),
								// appMatch);
								variableSubstitutions.put(parseTreePathTemplate.getText(), appMatch);
								j++;
								observedPhIndex++;
								break;
							}
						} else {
							matches = parseTreePathTemplateName.equals(parseTreePathCompilationUnit.getName())
									&& parseTreePathTemplate.getText().equals(parseTreePathCompilationUnit.getText());
						}

						if (!matches) {
							if (isPh) {
								j++;
							} else {
								throw new NoMatchException(
										"Could not match template path " + parseTreePathTemplate.getPath());
							}
						} else {
							// LOGGER.info("Match tokens {} --> {}", parseTreePathTemplate.getText(),
							// appMatch);
							j++;
						}
					} while (!matches);
				}

				possibleSubstitutions.add(variableSubstitutions);
			} catch (NoMatchException e) {
				// ignore, try next
			}
		}

		if (possibleSubstitutions.isEmpty()) {
			throw new NoMatchException(
					String.format("Could not match template  %s to %s! No possible placeholder substitutions found.",
							parseTreePathListTemplate, parseTreePathListCompilationUnit));
		}

		LOGGER.info("{} possible placeholder substitutions found. {}", possibleSubstitutions.size(),
				possibleSubstitutions);
		List<Map<String, String>> validSubstitutions = new ArrayList<>();
		for (Map<String, String> possiblePlaceholderSubstitution : possibleSubstitutions) {
			boolean isValidSubstitution = true;
			for (Entry<String, String> placeholderSub : possiblePlaceholderSubstitution.entrySet()) {

				isValidSubstitution = checkPlaceholderSubstitution(placeholderSub.getKey(), placeholderSub.getValue());
				if (isValidSubstitution)
					break;
			}

			if (isValidSubstitution) {
				validSubstitutions.add(possiblePlaceholderSubstitution);
			}
		}

		LOGGER.info("{} valid placeholder substitutions found. {}", validSubstitutions.size(), validSubstitutions);
		for (Map<String, String> validSubstitution : validSubstitutions) {
			for (Entry<String, String> validSub : validSubstitution.entrySet()) {
				this.registerNewPlaceholderSubstitution(validSub.getKey(), validSub.getValue(), false);
			}
		}

		return true;
	}

	private void registerNewPlaceholderSubstitution(String placeholder, String substitution, boolean checkForValidity)
			throws PlaceholderClashException {

		if (checkForValidity && !checkPlaceholderSubstitution(placeholder, substitution)) {
			throw new PlaceholderClashException(placeholder, substitution,
					this.placeholderSubstitutions.get(placeholder), "Placeholder clash for placeholder " + placeholder);
		}

		LOGGER.info("New placeholder substitution {} {}", placeholder, substitution);
		if (!this.placeholderSubstitutions.containsKey(placeholder)) {
			Set<String> set = new HashSet<>();
			set.add(substitution);
			this.placeholderSubstitutions.put(placeholder, set);
		} else {
			Set<String> set = placeholderSubstitutions.get(placeholder);
			set.add(substitution);
			this.placeholderSubstitutions.put(placeholder, set);
		}
	}

	private void registerNewMatch(ParseTreePath parseTreePathTemplate, ParseTreePath parseTreePathCompilationUnit) {
		LOGGER.info("New match: {}, {}", parseTreePathTemplate.getText(), parseTreePathCompilationUnit.getText());
		this.matches.add(new Match(parseTreePathTemplate, parseTreePathCompilationUnit));
	}

	private boolean checkPlaceholderSubstitution(String placeholder, String substitution) {
		if (this.placeholderSubstitutions.containsKey(placeholder)) {
			if (!this.placeholderSubstitutions.get(placeholder).contains(substitution)) {
				return false;
			}
		}
		return true;
	}
}
