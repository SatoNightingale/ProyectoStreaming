package controllers;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import contenidos.Contenido;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import users.Creador;
import users.Usuario;

public class EditarPerfilController extends SceneController{
    @FXML private ListView<Creador> lstSuscripciones;
    @FXML private ListView<Contenido> lstMiContenido;
    @FXML private ListView<Contenido> lstHistorial;
    @FXML private TextField tfdNombre;
    @FXML private PasswordField pfdPassword;
    @FXML private PasswordField pfdNewPassword;
    @FXML private Button btnBorrarCuenta;
    @FXML private Button btnModificarDatos;
    @FXML private TitledPane paneMiContenido;
    @FXML private Button btnAtras;

    private Image iconCancel;

    @FXML
    public void initialize(){
        iconCancel = new Image(getClass().getResource("/resources/ic_action_cancel light.png").toExternalForm());

        btnModificarDatos.setOnAction(e -> intentarGuardarDatos());
    }

    public void init(Usuario usuario, Object... data) {
        this.usuario = usuario;

        tfdNombre.setText(usuario.getNombre());

        actualizarListaSuscripciones();
        actualizarListaHistorial();
        actualizarListaMiContenido();

        paneMiContenido.setDisable(!(usuario instanceof Creador));

        btnAtras.setOnAction(e -> goBack());
    }
	
    public void intentarGuardarDatos() {
        try {
            controlador.cambiarDatosUsuario(usuario, pfdPassword.getText(), tfdNombre.getText(), pfdNewPassword.getText());

            MainController.showInfoMessage("Sus datos se han cambiado con éxito");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void actualizarListaSuscripciones(){
        lstSuscripciones.getItems().clear();
        lstSuscripciones.getItems().addAll(usuario.getSuscripciones());

        lstSuscripciones.setCellFactory(lv -> new ListCell<Creador>() {
            protected void updateItem(Creador item, boolean empty){
                super.updateItem(item, empty);

                if(empty || item == null){
                    setText(null);
                } else {
                    setGraphic(crearTarjetaSuscripcion(item));
                }
            }
        });
    }

    public void actualizarListaMiContenido(){
        lstMiContenido.getItems().clear();
        lstMiContenido.getItems().addAll(((Creador) usuario).getContenidosSubidos());
        
        lstMiContenido.setCellFactory(lv -> new ListCell<Contenido>() {
            protected void updateItem(Contenido item, boolean empty){
                super.updateItem(item, empty);

                if(empty || item == null){
                    setText(null);
                } else {
                    setGraphic(crearCeldaContenido(item));
                }
            }
        });
    }

    public void actualizarListaHistorial(){
        List<Contenido> historial = usuario.getHistorial();
        Collections.reverse(historial);
        lstHistorial.getItems().addAll(historial);

        lstHistorial.setCellFactory(lv -> new ListCell<Contenido>() {
            protected void updateItem(Contenido item, boolean empty){
                super.updateItem(item, empty);
                if(empty || item == null){
                    setText(null);
                } else {
                    Label lblNombre = new Label(item.getNombre());
                    lblNombre.setFont(new Font(14));
                    Label lblAutor = new Label(item.getCreador().getNombre());
                    lblAutor.setFont(new Font(11));

                    VBox box = new VBox(lblNombre, lblAutor);
                    setGraphic(box);
                }
                setStyle("");
            }
        });
    }

    public HBox crearTarjetaSuscripcion(Creador creador){
        Label lblName = new Label(creador.getNombre());
        lblName.setPadding(new Insets(0, 0, 0, 5));

        HBox nameBox = new HBox(lblName);
        nameBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(nameBox, Priority.ALWAYS);

        ImageView ibnCancel = new ImageView(iconCancel);
        ibnCancel.setFitWidth(25);
        ibnCancel.setFitHeight(25);

        HBox result = new HBox(nameBox, ibnCancel);
        result.setSpacing(10);

        ibnCancel.setOnMouseClicked(e -> {
            controlador.desuscribirUsuario(usuario, creador);
            actualizarListaSuscripciones();
        });

        return result;
    }

    public HBox crearCeldaContenido(Contenido content){
        Label lblNombre = new Label(content.getNombre());
        lblNombre.setFont(new Font(14));
        
        Label lblReproducciones = new Label(String.valueOf(content.getReproducciones()) + " visualizaciones");
        lblReproducciones.setFont(new Font(11));

        Label lblLikes = new Label(String.valueOf(content.getLikes()) + " likes");
        lblLikes.setFont(new Font(11));

        HBox infoBox = new HBox(lblReproducciones, lblLikes);
        infoBox.setSpacing(10);

        VBox contentBox = new VBox(lblNombre, infoBox);
        // contentBox.setAlignment(Pos.CENTER_LEFT);
        
        HBox contentWrapper = new HBox(contentBox);
        HBox.setHgrow(contentWrapper, Priority.ALWAYS);

        ImageView ibnCancel = new ImageView(iconCancel);
        ibnCancel.setFitHeight(25);
        ibnCancel.setFitWidth(25);
        
        

        HBox box = new HBox(contentWrapper, ibnCancel);
        box.setAlignment(Pos.CENTER_LEFT);

        return box;
    }

    private void goBack(){
        // admin.cambiarEscena("fxml/VistaContenido.fxml");

        try {
            controlador.prepararVistaContenido(usuario);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}
