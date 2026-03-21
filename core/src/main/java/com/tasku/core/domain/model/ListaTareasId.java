package com.tasku.core.domain.model;

import java.util.UUID;

public record ListaTareasId(TableroId url, UUID id) {
	public ListaTareasId {
		if (url == null) {
			throw new IllegalArgumentException("La URL no puede ser nula");
		}
		if (id == null) {
			throw new IllegalArgumentException("El id no puede ser nulo");
		}
	}

	public ListaTareasId(TableroId url) {
		this(url, UUID.randomUUID());
	}
}
