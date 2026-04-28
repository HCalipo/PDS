package com.tasku.ui.presentation.controllers;

import com.tasku.ui.port.NavigationPort;
import com.tasku.ui.port.Result;
import com.tasku.ui.port.SessionService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML private TextField txtEmail;
    @FXML private TextField txtNombre;
    @FXML private Button btnIngresar;
    @FXML private Label lblMensajeError;

    private SessionService sessionService;	//para ver el usuario y la sesion iniciada en el login
    private NavigationPort navigationPort;	//para moverte entre interfaces

    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public void setNavigationPort(NavigationPort navigationPort) {
        this.navigationPort = navigationPort;
    }

    @FXML
    private void initialize() {
        if (lblMensajeError != null) {
            lblMensajeError.setText("");
        }
    }

    @FXML
    private void iniciarSesion() {
        String email = txtEmail.getText();
        String nombre = txtNombre.getText();

        if (email == null || email.isBlank() || nombre == null || nombre.isBlank()) {
            lblMensajeError.setText("Por favor, completa todos los campos");
            return;
        }
        // la clase result es para que se pueda saber si el login ha funcionado o no (un booleano), que hace q no haya q manejar los errores
        Result<?> result = sessionService.login(email, nombre);		//aqui es donde se hace el login
        if (result.isSuccess()) {


        //borrar de aqui
        
        }
        if (true){

        //hasta aqui


            navigationPort.showPrincipal();
        } else {
            lblMensajeError.setText(result.getError());
        }
    }
}
