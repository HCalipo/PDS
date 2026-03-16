package com.tasku.core.domain;

import java.util.UUID;

public record URL(String url) {
	private static final String PREFIJO = "tasku://tablero/";

	public URL {
		if (url == null || url.isBlank()) {
			throw new IllegalArgumentException("La URL no puede ser nula ni vacia");
		}

		String valorNormalizado = url.trim();
		UUID uuid;

		if (valorNormalizado.startsWith(PREFIJO)) {
			String token = valorNormalizado.substring(PREFIJO.length());
			uuid = parseUuid(token);
		} else {
			uuid = parseUuid(valorNormalizado);
		}

		// Guardamos siempre en formato canonico para compartir y comparar.
		url = PREFIJO + uuid;
	}

	public URL(UUID idTablero) {
		this(PREFIJO + validarId(idTablero));
	}

	public URL() {
		this(UUID.randomUUID());
	}

	public UUID idTablero() {
		return UUID.fromString(url.substring(PREFIJO.length()));
	}

	public static String prefijo() {
		return PREFIJO;
	}

	private static UUID parseUuid(String valor) {
		try {
			return UUID.fromString(valor);
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("La URL debe contener un UUID valido", ex);
		}
	}

	private static UUID validarId(UUID idTablero) {
		if (idTablero == null) {
			throw new IllegalArgumentException("El id del tablero no puede ser nulo");
		}
		return idTablero;
	}
}
