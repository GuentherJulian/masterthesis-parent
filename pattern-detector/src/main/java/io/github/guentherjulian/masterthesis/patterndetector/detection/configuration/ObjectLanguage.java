package io.github.guentherjulian.masterthesis.patterndetector.detection.configuration;

import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.objectlanguage.CLanguageConfiguration;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.objectlanguage.JavaLanguageConfiguration;
import io.github.guentherjulian.masterthesis.patterndetection.engine.configuration.objectlanguage.ObjectLanguageConfiguration;

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

	public static ObjectLanguageConfiguration getObjectLanguageProperties(ObjectLanguage objectLanguage,
			String metalanguagePrefix) {
		ObjectLanguageConfiguration objectLanguageProperties = null;
		if (objectLanguage == ObjectLanguage.JAVA) {
			objectLanguageProperties = new JavaLanguageConfiguration(metalanguagePrefix);
		}
		if (objectLanguage == ObjectLanguage.C) {
			objectLanguageProperties = new CLanguageConfiguration(metalanguagePrefix);
		}
		return objectLanguageProperties;
	}

}
