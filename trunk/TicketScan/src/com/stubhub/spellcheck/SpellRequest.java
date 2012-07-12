package com.stubhub.spellcheck;

public class SpellRequest {

    protected Boolean textAlreadyClipped = false;

    protected Boolean ignoreDuplicates = false;

    protected Boolean ignoreWordsWithDigits = true;

    protected Boolean ignoreWordsWithAllCaps = true;

    protected String text;

    public SpellRequest() {
    }

    public SpellRequest(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean isTextAlreadyClipped() {
        return textAlreadyClipped;
    }

    public Boolean isIgnoreDuplicates() {
        return ignoreDuplicates;
    }

    public Boolean isIgnoreWordsWithDigits() {
        return ignoreWordsWithDigits;
    }

    public Boolean isIgnoreWordsWithAllCaps() {
        return ignoreWordsWithAllCaps;
    }

    public void setTextAlreadyClipped(Boolean textAlreadyClipped) {
        this.textAlreadyClipped = textAlreadyClipped;
    }

    public void setIgnoreDuplicates(Boolean ignoreDuplicates) {
        this.ignoreDuplicates = ignoreDuplicates;
    }

    public void setIgnoreWordsWithDigits(Boolean ignoreWordsWithDigits) {
        this.ignoreWordsWithDigits = ignoreWordsWithDigits;
    }

    public void setIgnoreWordsWithAllCaps(Boolean ignoreWordsWithAllCaps) {
        this.ignoreWordsWithAllCaps = ignoreWordsWithAllCaps;
    }
}
