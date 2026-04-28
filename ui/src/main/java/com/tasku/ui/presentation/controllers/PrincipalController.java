package com.tasku.ui.presentation.controllers;

import com.tasku.ui.port.NavigationPort;
import com.tasku.ui.port.TableroPort;
import com.tasku.ui.port.ListaCallback;
import com.tasku.ui.port.SessionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import java.util.UUID;

public class PrincipalController {

    @FXML private Button ButtonTableroBlock;
    @FXML private SVGPath CandadoImage;
    @FXML private MenuButton boardMenuButton;
    @FXML private HBox boardContainer;

    private SessionService sessionService;	//gestion de la sesion iniciada en el login
    private TableroPort tableroPort;		//para poder gestionar el tablero
    private NavigationPort navigationPort;	//para movernos entre interfaces

    
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public void setTableroPort(TableroPort tableroPort) {
        this.tableroPort = tableroPort;
    }

    public void setNavigationPort(NavigationPort navigationPort) {
        this.navigationPort = navigationPort;
    }

    private boolean estaBloqueado = false;	//para el boton de bloqueo (guardar el estado en local y no tener q pedirlo constantemente al servidor) PENSAR EN ESTO
    										
    
    private ObservableList<RadioMenuItem> tablerosMenuItems = FXCollections.observableArrayList();	//menu de seleccion de tableros
    
    @FXML
    private void initialize() {
    	
    	//creacion del menu de seleccion de los tableros
        MenuItem labelSeccion = new MenuItem("TUS TABLEROS");
        labelSeccion.setDisable(true);
        labelSeccion.getStyleClass().add("menu-section-label");
        boardMenuButton.getItems().add(labelSeccion);

        //tableros por defecto BORRAR AHORA DESPUES
        agregarTablero("Tablero 1");
        agregarTablero("Tablero 2");
        
        //si hay tableros se muestra y selecciona el primero (por defecto)
        if (!tablerosMenuItems.isEmpty()) {
            RadioMenuItem primero = tablerosMenuItems.get(0);
            primero.setSelected(true);
            boardMenuButton.setText(primero.getText());
        }
        
        //separador del menu
        boardMenuButton.getItems().add(new SeparatorMenuItem());
        
        //se añaden los botones de añadir y unirse a un tablero
        MenuItem itemAñadir = new MenuItem("＋ Añadir tablero");
        itemAñadir.setOnAction(e -> handleAñadirTablero());
        itemAñadir.getStyleClass().add("menu-action-item");
        boardMenuButton.getItems().add(itemAñadir);

        MenuItem itemUnirse = new MenuItem("👥 Unirse a tablero");
        itemUnirse.setOnAction(e -> handleUnirseTablero());
        itemUnirse.getStyleClass().add("menu-action-item");
        boardMenuButton.getItems().add(itemUnirse);
    }

    @FXML
    private void handleBloquearTablero() {
        estaBloqueado = !estaBloqueado;
        var tablero = sessionService.getCurrentBoard();
        if (tablero != null) {
            if (estaBloqueado) {
                tableroPort.bloquear(tablero.url());
                System.out.println("Tablero " + tablero.name() + " bloqueado.");
            } else {
                tableroPort.desbloquear(tablero.url());
                System.out.println("Tablero " + tablero.name() + " desbloqueado.");
            }
        }
        actualizarIconoBloqueo();
    }

    private void actualizarIconoBloqueo() {
        if (estaBloqueado) {
            CandadoImage.setContent("M405.333,179.712v-30.379C405.333,66.859,338.475,0,256,0S106.667,66.859,106.667,149.333v30.379   c-38.826,16.945-63.944,55.259-64,97.621v128C42.737,464.214,90.452,511.93,149.333,512h213.333   c58.881-0.07,106.596-47.786,106.667-106.667v-128C469.278,234.971,444.159,196.657,405.333,179.712z M277.333,362.667   c0,11.782-9.551,21.333-21.333,21.333c-11.782,0-21.333-9.551-21.333-21.333V320c0-11.782,9.551-21.333,21.333-21.333   c11.782,0,21.333,9.551,21.333,21.333V362.667z M362.667,170.667H149.333v-21.333c0-58.91,47.756-106.667,106.667-106.667   s106.667,47.756,106.667,106.667V170.667z");
            ButtonTableroBlock.setStyle("-fx-background-color: #0ba360; -fx-text-fill: white;");
            CandadoImage.setScaleX(0.03);
            CandadoImage.setScaleY(0.03);
            
            
        } else {
            CandadoImage.setContent("M264-168h432v-384H264v384Zm267-141.21q21-21.21 21-51T530.79-411q-21.21-21-51-21T429-410.79q-21 21.21-21 51T429.21-309q21.21 21 51 21T531-309.21ZM264-168v-384 384Zm-.28 72Q234-96 213-117.15T192-168v-384q0-29.7 21.15-50.85Q234.3-624 264-624h264v-96q0-79.68 56.23-135.84 56.22-56.16 136-56.16Q800-912 856-855.84q56 56.16 56 135.84h-72q0-50-35-85t-85-35q-50 0-85 35t-35 85v96h96q29.7 0 50.85 21.15Q768-581.7 768-552v384q0 29.7-21.16 50.85Q725.68-96 695.96-96H263.72Z");
            ButtonTableroBlock.setStyle("");
            CandadoImage.setScaleX(0.02);
            CandadoImage.setScaleY(0.02);
        }
    }

    private void agregarTablero(String nombreTablero) {
        RadioMenuItem item = new RadioMenuItem(nombreTablero);
        item.getStyleClass().add("menu-action-item");
        item.setOnAction(e -> seleccionarTablero(item));
        tablerosMenuItems.add(item);
        boardMenuButton.getItems().add(item);
        
        //AQUI HAY Q PEDIR AL SERVIDOR Q CREE UN NUEVO TABLERO
        
        
    }

    private void seleccionarTablero(RadioMenuItem itemSeleccionado) {
        for (RadioMenuItem item : tablerosMenuItems) {
            item.setSelected(false);
        }
        itemSeleccionado.setSelected(true);
        boardMenuButton.setText(itemSeleccionado.getText());
        
        
        //AQUI HAY Q AÑADIR EL CAMBIO DE TABLERO (PEDIRLE AL SERVIDOR Q TE DE EL TABLERO)
        
        
        
    }

    @FXML
    private void agregarColumna(String nombre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ListaTareas.fxml"));
            VBox nuevaColumna = loader.load();
            ListaTareasController controller = loader.getController();
            controller.setTitulo(nombre);
            int indice = boardContainer.getChildren().size() - 1;
            boardContainer.getChildren().add(indice, nuevaColumna);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAñadirTablero() {
        navigationPort.showAñadirTablero();
    }

    @FXML
    private void handleUnirseTablero() {
        navigationPort.showUnirTablero();
    }

    @FXML
    private void handleVerHistorial() {
        navigationPort.showHistorial();
    }

    @FXML
    private void handleAñadirLista() {						//POR AHORA NO COMPROBADO EL FUNCIONAMIENTO PUESTO QUE NECESITAS ESTAR LOGEADO Y TIENE Q SER EL DOMINIO QUIEN LO CREE
        var tablero = sessionService.getCurrentBoard();
        var usuario = sessionService.getCurrentUser();
        
        if (tablero == null || usuario == null) {           
            System.err.println("Error: No hay tablero o usuario en sesión");
            return;
        }

        navigationPort.showAñadirLista(nombre -> {  
            agregarColumna(nombre); //añade la columna (lista de tareas con el nombre introducido en la interfaz de creacion)
            UUID listaId = tableroPort.crearListaTareas(tablero.url(), usuario);    //habla con el controlador del tablero para que el servidor registre la lista
            System.out.println("Lista creada: " + listaId); //imprime por el terminal el nombre de la lista creada
        });
    }

    @FXML
    private void handleCrearTarea() {
        navigationPort.showCreateCard();
    }
}
