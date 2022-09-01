package io.github.guentherjulian.masterthesis.patterndetection.engine.utils;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

public class ParseTreeUtil {

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

	public static int countTerminalNodes(ParseTree tree) {
		int count = 0;
		if (tree != null) {
			if (tree instanceof TerminalNode) {
				count = 1;
			} else {
				if (tree.getChildCount() > 0) {
					for (int i = 0; i < tree.getChildCount(); i++) {
						count += countTerminalNodes(tree.getChild(i));
					}
				}
			}
		}
		return count;
	}
}
