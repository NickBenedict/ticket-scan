package com.stubhub.spellcheck;

public enum Language {
	DANISH("da"), GERMAN("de"), ENGLISH("en"), SPANISH("es"), FINNISH("fi"), FRENCH(
			"fr"), ITALIAN("it"), DUTCH("nl"), POLISH("pl"), PORTUGUESE("pt"), SWEDISH(
			"sv");

	private final String code;

	private Language(String code) {
		this.code = code;
	}

	public String code() {
		return code;
	}
}
