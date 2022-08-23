package io.github.guentherjulian.masterthesis.patterndetector.detection.configuration;

import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.objectlanguage.CProperties;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.objectlanguage.JavaProperties;
import io.github.guentherjulian.masterthesis.patterndetection.engine.languages.objectlanguage.ObjectLanguageProperties;

public enum ObjectLanguage {

	JAVA, C;

	public static String[] getSupportedObjectLanguages() {
		return new String[] { "Java", "C" };
	}

	public static ObjectLanguage getObjectLanguage(String objectLanguageString) {
		ObjectLanguage objectLanguage = null;
		if (objectLanguageString.equals("Java")) {
			objectLanguage = ObjectLanguage.JAVA;
		}
		if (objectLanguageString.equals("C")) {
			objectLanguage = ObjectLanguage.C;
		}
		return objectLanguage;
	}

	public static ObjectLanguageProperties getObjectLanguageProperties(ObjectLanguage objectLanguage,
			String metalanguagePrefix) {
		ObjectLanguageProperties objectLanguageProperties = null;
		if (objectLanguage == ObjectLanguage.JAVA) {
			objectLanguageProperties = new JavaProperties(metalanguagePrefix);
		}
		if (objectLanguage == ObjectLanguage.C) {
			objectLanguageProperties = new CProperties(metalanguagePrefix);
		}
		return objectLanguageProperties;
	}

}
