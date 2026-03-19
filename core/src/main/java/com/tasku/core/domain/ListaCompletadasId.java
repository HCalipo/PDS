package com.tasku.core.domain;

import java.util.UUID;

public record ListaCompletadasId(TableroId url, UUID id) {
	public ListaCompletadasId {
		if (url == null) {
			throw new IllegalArgumentException("La URL no puede ser nula");
		}
		if (id == null) {
			throw new IllegalArgumentException("El id no puede ser nulo");
		}
	}

	public ListaCompletadasId(TableroId url) {
		this(url, UUID.randomUUID());
	}
}
