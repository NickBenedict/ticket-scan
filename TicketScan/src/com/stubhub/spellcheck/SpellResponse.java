package com.stubhub.spellcheck;

public class SpellResponse {

    protected int error = 0;

    protected Boolean clipped = false;

    protected int charsChecked = 0;

    protected SpellCorrection[] corrections;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public Boolean isClipped() {
        return clipped;
    }

    public void setClipped(Boolean clipped) {
        this.clipped = clipped;
    }

    public int getCharsChecked() {
        return charsChecked;
    }

    public void setCharsChecked(int charsChecked) {
        this.charsChecked = charsChecked;
    }

    public SpellCorrection[] getCorrections() {
        return corrections;
    }

    public void setCorrections(SpellCorrection[] corrections) {
        this.corrections = corrections;
    }
}
