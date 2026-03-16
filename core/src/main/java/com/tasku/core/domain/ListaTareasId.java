package com.tasku.core.domain;

import java.util.UUID;

public record ListaTareasId(URL url, UUID id) {
	public ListaTareasId {
		if (url == null) {
			throw new IllegalArgumentException("La URL no puede ser nula");
		}
		if (id == null) {
			throw new IllegalArgumentException("El id no puede ser nulo");
		}
	}

	public ListaTareasId(URL url) {
		this(url, UUID.randomUUID());
	}
}
