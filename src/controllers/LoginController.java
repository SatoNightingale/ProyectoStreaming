package controllers;

import java.util.ArrayList;

import exceptions.FaltanCamposObligatoriosException;
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

    private boolean validarDatosUsuario() throws FaltanCamposObligatoriosException{
        ArrayList<String> camposFaltan = new ArrayList<>();

        if(tfdNombre.getText().isEmpty()) camposFaltan.add("Nombre");
        if(pfdPassword.getText().isEmpty()) camposFaltan.add("Contraseña");

        if(!camposFaltan.isEmpty())
            throw new FaltanCamposObligatoriosException((String[]) camposFaltan.toArray());

        // Si pasa estas comprobaciones sin dar error, devuelve true
        return true;
    }

    private void enviarPeticionLogin(ActionEvent e){
        try{
            if(validarDatosUsuario()){
                controlador.usuarioLogin(tfdNombre.getText(), pfdPassword.getText());
            }
        } catch(Exception ex){
            MensajesDialogo.mostrarError(ex.getMessage());
        }
    }
}
