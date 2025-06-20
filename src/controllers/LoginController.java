package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import users.Usuario;
import utils.MensajesDialogo;

public class LoginController extends SceneController {
    @FXML private Button btnEnter;
    @FXML private TextField tfdNombre;
    @FXML private Button btnNuevoUsuario;
    @FXML private PasswordField pfdPassword;
    
    @FXML
    public void initialize(){
        btnEnter.setOnAction(this::enviarPeticionLogin);
        btnNuevoUsuario.setOnAction(e -> admin.cambiarEscena("fxml/NuevoUsuarioView.fxml"));
    }

    public void init(Usuario user, Object...data){}

    private boolean validarDatosUsuario(){
        boolean band = false;

        String nombre = tfdNombre.getText();
        String password = pfdPassword.getText();

        if(
            !nombre.isEmpty() &&
            !password.isEmpty()
            // && controlador.getAdminUsuarios().usuarioExiste(nombre, password)
        )
        band = true;

        return band;
    }

    private void enviarPeticionLogin(ActionEvent e){
        try{
            if(validarDatosUsuario()){
                controlador.usuarioLogin(tfdNombre.getText(), pfdPassword.getText());
            } else { // TODO: Tírame una excepción
                MensajesDialogo.mostrarError("Por favor, llene todos los campos");
            }
        } catch(Exception ex){
            ex.printStackTrace();
            MensajesDialogo.mostrarError("Datos de usuario incorrectos");
        }
    }
}
