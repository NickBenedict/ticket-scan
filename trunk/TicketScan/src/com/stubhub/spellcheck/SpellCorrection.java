package com.stubhub.spellcheck;

public class SpellCorrection {

    protected int offset = 0;

    protected int length = 0;

    protected int confidence = 0;

    protected String value = "";

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String[] getWords() {
        return ( value == null ) ? null : value.split( "\\s" );
    }
}
