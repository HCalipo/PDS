package com.tasku.ui.presentation.controllers;

import com.tasku.ui.client.http.DesktopApiException;
import com.tasku.ui.client.http.TaskuApiClient;
import com.tasku.ui.state.DesktopSessionState;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class InicioSesionController {
	@FXML
	private TextField txtEmail;

	@FXML
	private TextField txtNombre;

	@FXML
	private Label lblMensajeError;

	private final TaskuApiClient apiClient = new TaskuApiClient();

	@FXML
	private void iniciarSesion() {
		String email = normalize(txtEmail.getText());
		String nombre = normalize(txtNombre.getText());

		if (email.isBlank() || nombre.isBlank()) {
			showError("Email y nombre son obligatorios.");
			return;
		}

		try {
			int boardsCount = apiClient.findBoardsByOwner(email).size();
			DesktopSessionState.setUser(email, nombre);
			showSuccess("Sesion iniciada. Tableros detectados: " + boardsCount);
		} catch (DesktopApiException ex) {
			showError("No se pudo conectar con la API: " + ex.getMessage());
		}
	}

	private static String normalize(String value) {
		return value == null ? "" : value.trim();
	}

	private void showError(String message) {
		lblMensajeError.setStyle("-fx-text-fill: #d63031;");
		lblMensajeError.setText(message);
	}

	private void showSuccess(String message) {
		lblMensajeError.setStyle("-fx-text-fill: #0ba360;");
		lblMensajeError.setText(message);
	}
}
