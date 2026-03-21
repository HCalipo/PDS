package com.tasku.core.domain.model;

public record ColorEtiqueta(String color) {
	public ColorEtiqueta {
		if (color == null || color.isBlank()) {
			throw new IllegalArgumentException("El color no puede ser nulo ni vacio");
		}
		color = color.trim();
	}
}
