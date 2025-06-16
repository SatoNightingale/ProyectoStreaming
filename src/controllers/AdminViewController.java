package controllers;

import java.util.List;

import contenidos.Contenido;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import users.Creador;
import users.Usuario;
import utils.MensajesDialogo;

public class AdminViewController extends SceneController{
    @FXML private ListView<Contenido> lstContenidos;
    @FXML private ListView<Usuario> lstUsuarios;
    @FXML private ListView<Creador> lstCreadores;
    @FXML private Button btnAtras;
    
    
    @Override
    public void init(Usuario usuario, Object... data) {
        inicializarListaContenidos();
        inicializarListaUsuarios();
    }

    private void inicializarListaContenidos(){
        lstContenidos.setCellFactory(lv -> new ListCell<Contenido>(){
            protected void updateItem(Contenido item, boolean empty){
                super.updateItem(item, empty);

                if(empty || item == null){
                    setText(null);
                } else {
                    setGraphic(crearFichaContenido(item));
                }
            }
        });

        actualizarListaContenidos();
    }

    private void inicializarListaUsuarios(){
        lstUsuarios.setCellFactory(lv -> new ListCell<Usuario>(){
            protected void updateItem(Usuario item, boolean empty){
                super.updateItem(item, empty);

                if(empty || item == null){
                    setText(null);
                } else {
                    setGraphic(crearFichaUsuario(item));
                }
            }
        });

        actualizarListaUsuarios();
    }

    private void actualizarListaContenidos(){
        List<Contenido> contenidos = controlador.getAdminContenidos().getContenidos();
        lstContenidos.getItems().setAll(contenidos);
    }

    private void actualizarListaUsuarios(){
        List<Usuario> usuarios = controlador.getAdminUsuarios().getUsuarios();
        lstUsuarios.getItems().setAll(usuarios);
    }

    private Node crearFichaContenido(Contenido content){
        GridPane grid = new GridPane();
        
        Label nombre = new Label(content.getNombre());
        Label creador = new Label(content.getCreador().getNombre());
        Label vistas = new Label(content.getReproducciones() + " vistas");
        Label likes = new Label(content.getLikes() + " likes");
        Button borrar = new Button("Retirar contenido");

        GridPane.setHalignment(borrar, HPos.RIGHT);

        borrar.setOnAction(e -> {
            ButtonType respuesta = MensajesDialogo.mostrarEleccion("¿Retirar este contenido?");

            if(respuesta.equals(ButtonType.YES)){
                controlador.retirarContenido(content);;
                actualizarListaContenidos();

                MensajesDialogo.mostrarInfo("El contenido ha sido retirado");
            }

            controlador.retirarContenido(content);
            actualizarListaContenidos();
        });

        grid.add(nombre, 0, 0);
        grid.add(creador, 1, 0);
        grid.add(vistas, 0, 1);
        grid.add(likes, 1, 1);
        grid.add(borrar, 2, 1);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(70);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(30);
        col2.setMinWidth(30);

        grid.getColumnConstraints().add(col1);

        return grid;
    }

    private Node crearFichaUsuario(Usuario user){
        Label nombre = new Label(user.getNombre());

        VBox box = new VBox(nombre);

        if(user instanceof Creador){
            Creador c = (Creador) user;
            Label lblSubidos = new Label(String.valueOf(c.getContenidosSubidos().size()));
            lblSubidos.setFont(new Font(11));
            lblSubidos.setPadding(new Insets(0, 0, 0, 5));
            box.getChildren().add(lblSubidos);
        }

        HBox spanBox = new HBox(box);                    
        HBox.setHgrow(box, Priority.ALWAYS);

        Button expulsar = new Button("Expulsar");

        expulsar.setOnAction(e -> {
            ButtonType respuesta = MensajesDialogo.mostrarEleccion("¿Está seguro de que quiere expulsar al usuario " + user.getNombre() + "?");

            if(respuesta.equals(ButtonType.YES)){
                controlador.eliminarUsuario(user);
                actualizarListaUsuarios();

                MensajesDialogo.mostrarInfo(user.getNombre() + " ha sido expulsado");
            }
        });

        HBox ficha = new HBox(spanBox, expulsar);

        return ficha;
    }
}