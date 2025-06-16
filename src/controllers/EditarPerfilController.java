package controllers;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import contenidos.Contenido;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import utils.MensajesDialogo;

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

        inicializarListaSuscripciones();
        inicializarListaHistorial();
        inicializarListaMiContenido();

        paneMiContenido.setDisable(!(usuario instanceof Creador));

        btnBorrarCuenta.setOnAction(e -> solicitarEliminacionCuenta());

        btnAtras.setOnAction(e -> goBack());
    }
	
    public void intentarGuardarDatos() {
        try {
            controlador.cambiarDatosUsuario(usuario, pfdPassword.getText(), tfdNombre.getText(), pfdNewPassword.getText());

            MensajesDialogo.mostrarInfo("Sus datos se han cambiado con éxito");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void solicitarEliminacionCuenta(){
        ButtonType respuesta = MensajesDialogo.mostrarEleccion("¿Estás seguro de que quieres eliminar tu cuenta?\nEsta acción es irreversible...");

        if(respuesta.equals(ButtonType.YES)){ //CAGASTE
            MensajesDialogo.mostrarInfo("Arigatou. Sayonara...\nありがとう. さよなら ..."); // Your name ('Kimi no Na Wa') referencia
            controlador.eliminarUsuario(usuario);
            
            admin.cambiarEscena("fxml/LoginView.fxml");
        }
    }
    
    private void inicializarListaSuscripciones(){
        lstSuscripciones.setCellFactory(lv -> new ListCell<Creador>() {
            protected void updateItem(Creador item, boolean empty){
                super.updateItem(item, empty);

                if(empty || item == null){
                    setText(null);
                } else {
                    setGraphic(crearCeldaSuscripcion(item));
                }
            }
        });

        actualizarListaSuscripciones();
    }

    private void inicializarListaMiContenido(){
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

        actualizarListaMiContenido();
    }

    private void inicializarListaHistorial(){
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

        actualizarListaHistorial();
    }

    private void actualizarListaSuscripciones(){
        lstSuscripciones.getItems().setAll(usuario.getSuscripciones());
    }

    private void actualizarListaMiContenido(){
        lstMiContenido.getItems().clear();
        lstMiContenido.getItems().setAll(((Creador) usuario).getContenidosSubidos());
    }

    private void actualizarListaHistorial(){
        List<Contenido> historial = usuario.getHistorial();
        Collections.reverse(historial);
        lstHistorial.getItems().setAll(historial);
    }

    private HBox crearCeldaSuscripcion(Creador creador){
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

    private HBox crearCeldaContenido(Contenido content){
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
        
        ibnCancel.setOnMouseClicked(e -> {
            // System.out.println("Contenidos antes: " + controlador.getModelo().getContenidos().size());
            controlador.retirarContenido(content);
            actualizarListaMiContenido();
            // System.out.println("Contenidos ahora: " + controlador.getModelo().getContenidos().size());
        });

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
