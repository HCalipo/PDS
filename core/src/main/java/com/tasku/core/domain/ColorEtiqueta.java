package com.tasku.core.domain;

public record ColorEtiqueta(String color) {
	public ColorEtiqueta {
		
        if (color == null || color.isBlank()) {
			throw new IllegalArgumentException("El color no puede ser nulo ni vacío");
		}

		color = color.trim().toLowerCase();

        // Comprobamos si los colores están en hexadecimal
		boolean esHex = color.matches("^#[0-9a-f]{6}$");
        // Si no lo están, comprobamos que estén bien escritos (en inglés) y estén en permitidos
		boolean esNombrePermitido = esNombrePermitido(color);

		if (!esHex && !esNombrePermitido) {
			throw new IllegalArgumentException("Color inválido. Usa un nombre permitido o formato HEX #rrggbb");
		}
	}

	private static boolean esNombrePermitido(String color) {
		try {
			NombreColorEtiqueta.desde(color);
			return true;
		} catch (IllegalArgumentException ex) {
			return false;
		}
	}
}
