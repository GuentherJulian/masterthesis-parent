package io.github.guentherjulian.masterthesis.patterndetection.parsing;

public enum ListType {

    /*
     * The declaration order is important for matching priority! Do not change it by accident.
     */

    ATOMIC(false), LIST_PATTERN(false), ORDERED(false), NONORDERED(false), ALTERNATIVE(true), OPTIONAL(true), ARBITRARY(
        true);

    private boolean isMetaLang;

    private ListType(boolean isMetaLang) {
        this.isMetaLang = isMetaLang;
    }

    public boolean isMetaLang() {
        return isMetaLang;
    }

}