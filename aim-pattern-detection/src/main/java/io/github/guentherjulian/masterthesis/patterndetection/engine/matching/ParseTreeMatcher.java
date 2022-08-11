package io.github.guentherjulian.masterthesis.patterndetection.engine.matching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.guentherjulian.masterthesis.patterndetection.engine.exception.NoMatchException;
import io.github.guentherjulian.masterthesis.patterndetection.engine.exception.PlaceholderClashException;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.metalanguage.MetaLanguageLexerRules;
import io.github.guentherjulian.masterthesis.patterndetection.engine.utils.MathUtil;
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

	public ParseTreeMatcher(ParseTree compilationUnitParseTree, ParseTree aimPatternTemplateParseTree,
			MetaLanguageLexerRules metaLanguageLexerRules) {
		this.compilationUnitParseTree = compilationUnitParseTree;
		this.aimPatternTemplateParseTree = aimPatternTemplateParseTree;
		this.metaLanguageLexerRules = metaLanguageLexerRules;
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
			e.printStackTrace();
		} catch (NoMatchException e) {
			this.match.setMatch(false);
			this.match.setException(e);
		}

		long endTime = System.nanoTime();
		LOGGER.info("Finished matching parse trees (took {} ns, {} ms)", (endTime - startTime),
				((endTime - startTime) / 1e6));

		return this.match;
	}

	private TreeMatch matchPathList(ParseTreePathList templateParseTreePathList,
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
			e.printStackTrace();
		}

		return null;
	}

	private TreeMatch matchOrderedPaths(List<ParseTreeElement> orderedTemplatePaths,
			List<ParseTreeElement> orderedCompilationUnitPaths) throws NoMatchException {

		int j = 0;
		for (int i = 0; i < orderedTemplatePaths.size(); i++) {
			ParseTreeElement parseTreeElementTemplate = orderedTemplatePaths.get(i);

			ParseTreeElement parseTreeElementCompilationUnit;
			boolean matches = false;
			do {
				if (j >= orderedTemplatePaths.size()) {
					throw new RuntimeException("Match exception");
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

		return null;
	}

	private TreeMatch matchUnorderedPaths(List<ParseTreeElement> unorderedTemplatePaths,
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
						matchPathList((ParseTreePathList) parseTreeElementTemplate,
								(ParseTreePathList) parseTreeElementCompilationUnit);
					}
				} catch (RuntimeException e) {
					e.printStackTrace();
				}

				// TODO evaluate match
			}
		}

		return null;
	}

	private TreeMatch matchPath(ParseTreeElement templatePathElement, ParseTreeElement compilationUnitPathElement)
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
									currentParseTreePathCompilationUnit.getText());
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
							parseTreePathCompilationUnit.getText());
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
		return null;
	}

	private TreeMatch matchListPattern(ParseTreePathList parseTreePathListTemplate,
			ParseTreePathList parseTreePathListCompilationUnit) throws NoMatchException {

		int numOfPlaceholders = (int) parseTreePathListTemplate.stream().filter(path -> path instanceof ParseTreePath)
				.map(path -> (ParseTreePath) path).filter(path -> path.containsMetaLanguage()).count();
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

		List<List<Match>> foundMatches = new ArrayList<>();
		for (int[] combination : combinations) {
			Map<String, String> variableSubstitutions = new HashMap<>();

			try {
				int observedPhIndex = 0;
				int j = 0;
				for (int i = 0; i < parseTreePathListTemplate.size(); i++) {

					boolean isPh = false;
					ParseTreeElement tempElem = parseTreePathListTemplate.get(i);
					String tempName;
					if (tempElem instanceof ParseTreePath) {
						isPh = ((ParseTreePath) tempElem).isMetaLanguageElement();
						tempName = ((ParseTreePath) tempElem).getName();
					} else {
						// let's not support this for now
						throw new IllegalStateException("AstPathCollection not supported here so far!");
					}

					ParseTreeElement appElem;
					String appMatch = "";
					boolean matches = false;
					int consumedAppElems = 0;
					do {
						if (j >= parseTreePathListCompilationUnit.size()) {
							// template could not be found entirely in app!
							String debugVal = tempElem instanceof ParseTreePath ? ((ParseTreePath) tempElem).getPath()
									: ((ParseTreePathList) tempElem).getHint();
							throw new NoMatchException("Could not find path " + debugVal);
						}
						appElem = parseTreePathListCompilationUnit.get(j);

						if (appElem instanceof ParseTreePath) {
							appMatch += ((ParseTreePath) appElem).getText();
							consumedAppElems++;
						} else {
							// let's not support this for now
							throw new IllegalStateException("AstPathCollection not supported here so far!");
						}

						if (isPh) {
							if (combination[observedPhIndex] > consumedAppElems) {
								j++;
								continue;
							} else {
								System.out.println("Consume (t->a): " + tempElem + " --> " + appMatch);
								// variableSubstitutions
								// .putAll(matchPlaceholder(((ParseTreePath) tempElem).getText(), appMatch));
								j++;
								observedPhIndex++;
								break;
							}
						} else {
							matches = tempName.equals(((ParseTreePath) appElem).getName())
									&& ((ParseTreePath) tempElem).getText().equals(((ParseTreePath) appElem).getText());
						}

						if (!matches) {
							if (isPh) {
								j++;
							} else {
								throw new NoMatchException(
										"Could not match template path " + ((ParseTreePath) tempElem).getPath());
							}
						} else {
							System.out.println("Consume (t->a): " + tempElem + " --> " + appMatch);
							j++;
						}
					} while (!matches);
				}

				// foundMatches.add(Arrays.asList(new AtomarMatch(parseTreePathListTemplate,
				// parseTreePathListCompilationUnit, variableSubstitutions)));
			} catch (NoMatchException e) {
				// ignore, try next
			}
		}

		if (foundMatches.isEmpty()) {
			throw new NoMatchException("Could not match template " + parseTreePathListTemplate + " to "
					+ parseTreePathListCompilationUnit);
		}

		return null;
	}

	private void registerNewPlaceholderSubstitution(String placeholder, String substitution)
			throws PlaceholderClashException {

		if (checkPlaceholderSubstitution(placeholder, substitution)) {
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
		} else {
			throw new PlaceholderClashException(placeholder, substitution,
					this.placeholderSubstitutions.get(placeholder), "Placeholder clash for placeholder " + placeholder);
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
