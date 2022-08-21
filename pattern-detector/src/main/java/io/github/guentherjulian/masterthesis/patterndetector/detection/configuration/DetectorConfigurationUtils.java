package io.github.guentherjulian.masterthesis.patterndetector.detection.configuration;

import org.antlr.parser.cfreemarkertemplate.CFreemarkerTemplateLexer;
import org.antlr.parser.cfreemarkertemplate.CFreemarkerTemplateParser;
import org.antlr.parser.cvelocitytemplate.CVelocityTemplateLexer;
import org.antlr.parser.cvelocitytemplate.CVelocityTemplateParser;
import org.antlr.parser.java8freemarkertemplate.Java8FreemarkerTemplateLexer;
import org.antlr.parser.java8freemarkertemplate.Java8FreemarkerTemplateParser;
import org.antlr.parser.java8stringtemplatetemplate.Java8StringTemplateTemplateLexer;
import org.antlr.parser.java8stringtemplatetemplate.Java8StringTemplateTemplateParser;
import org.antlr.parser.java8velocitytemplate.Java8VelocityTemplateLexer;
import org.antlr.parser.java8velocitytemplate.Java8VelocityTemplateParser;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;

public class DetectorConfigurationUtils {
	public static Class<? extends Parser> getParserClass(ObjectLanguage objectLanguage, MetaLanguage metaLanguage) {
		if (objectLanguage == ObjectLanguage.JAVA) {
			if (metaLanguage == MetaLanguage.FREEMARKER) {
				return Java8FreemarkerTemplateParser.class;
			}
			if (metaLanguage == MetaLanguage.VELOCITY) {
				return Java8VelocityTemplateParser.class;
			}
			if (metaLanguage == MetaLanguage.STRINGTEMPLATE) {
				return Java8StringTemplateTemplateParser.class;
			}
		}
		if (objectLanguage == ObjectLanguage.C) {
			if (metaLanguage == MetaLanguage.FREEMARKER) {
				return CFreemarkerTemplateParser.class;
			}
			if (metaLanguage == MetaLanguage.VELOCITY) {
				return CVelocityTemplateParser.class;
			}
		}
		return null;
	}

	public static Class<? extends Lexer> getLexerClass(ObjectLanguage objectLanguage, MetaLanguage metaLanguage) {
		if (objectLanguage == ObjectLanguage.JAVA) {
			if (metaLanguage == MetaLanguage.FREEMARKER) {
				return Java8FreemarkerTemplateLexer.class;
			}
			if (metaLanguage == MetaLanguage.VELOCITY) {
				return Java8VelocityTemplateLexer.class;
			}
			if (metaLanguage == MetaLanguage.STRINGTEMPLATE) {
				return Java8StringTemplateTemplateLexer.class;
			}
		}
		if (objectLanguage == ObjectLanguage.C) {
			if (metaLanguage == MetaLanguage.FREEMARKER) {
				return CFreemarkerTemplateLexer.class;
			}
			if (metaLanguage == MetaLanguage.VELOCITY) {
				return CVelocityTemplateLexer.class;
			}
		}
		return null;
	}
}
