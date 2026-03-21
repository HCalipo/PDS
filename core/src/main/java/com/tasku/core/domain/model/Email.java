package com.tasku.core.domain.model;

import java.util.regex.Pattern;

public record Email(String email) {
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

	public Email {
		if (email == null || email.isBlank()) {
			throw new IllegalArgumentException("El email no puede ser nulo ni vacío");
		}

		email = email.trim().toLowerCase();

		if (!EMAIL_PATTERN.matcher(email).matches()) {
			throw new IllegalArgumentException("Formato de email inválido");
		}
	}
}
