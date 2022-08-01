package io.github.guentherjulian.masterthesis.antlr4.parser.utils;

import org.antlr.v4.runtime.tree.ParseTree;

public class ParseTreeUtils {

	public static int countTreeNodes(ParseTree tree) {
		int count = 0;
		if (tree != null) {
			count++;

			if (tree.getChildCount() > 0) {
				for (int i = 0; i < tree.getChildCount(); i++) {
					count += countTreeNodes(tree.getChild(i));
				}
			}
		}
		return count;
	}
}
