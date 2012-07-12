package com.stubhub.spellcheck;

@SuppressWarnings("serial")
public class SpellCheckException extends RuntimeException {

    public SpellCheckException() {
        super();
    }

    public SpellCheckException(String message, Throwable cause) {
        super( message, cause );
    }

    public SpellCheckException(String message) {
        super( message );
    }

    public SpellCheckException(Throwable cause) {
        super( cause );
    }
}

