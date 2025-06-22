package controllers;

import java.util.ArrayList;

import exceptions.FaltanCamposObligatoriosException;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import users.Usuario;
import utils.MensajesDialogo;

public class NuevoUsuarioController extends SceneController {
    @FXML private PasswordField pfdAdminPassword;
    @FXML private Button btnAtras;
    @FXML private ComboBox<String> cmbTipoCuenta;
    @FXML private Button btnCrearCuenta;
    @FXML private TextField tfdNombre;
    @FXML private PasswordField pfdPassword;
    @FXML private Label lblAdminPassword;

    @FXML
    void initialize(){
        cmbTipoCuenta.getItems().setAll(new String[]{"Usuario", "Creador", "Administrador"});

        btnAtras.setOnAction(e -> admin.cambiarEscena("fxml/LoginView.fxml"));
        btnCrearCuenta.setOnAction(this::enviarPeticionAddUsuario);

        lblAdminPassword.visibleProperty().bind(Bindings.equal(2, cmbTipoCuenta.getSelectionModel().selectedIndexProperty()));
        pfdAdminPassword.visibleProperty().bind(Bindings.equal(2, cmbTipoCuenta.getSelectionModel().selectedIndexProperty()));

        btnCrearCuenta.textProperty().bind(Bindings.createStringBinding(
            () -> "Crear cuenta como \"" + tfdNombre.textProperty().get() + "\"",
            tfdNombre.textProperty())
        );
    }

    public void init(Usuario user, Object...data){}

    private boolean validarCampos() throws FaltanCamposObligatoriosException{
        ArrayList<String> camposFaltan = new ArrayList<>();

        if(cmbTipoCuenta.getSelectionModel().getSelectedIndex() == -1)
            camposFaltan.add("Tipo de usuario");

        if(tfdNombre.getText().isEmpty())
            camposFaltan.add("Nombre");

        if(pfdPassword.getText().isEmpty())
            camposFaltan.add("Contraseña");

        if(cmbTipoCuenta.getSelectionModel().getSelectedIndex() == 2 && pfdAdminPassword.getText().isEmpty())
            camposFaltan.add("Contraseña de administrador");

        if(!camposFaltan.isEmpty())
            throw new FaltanCamposObligatoriosException((String[]) camposFaltan.toArray());

        // Si pasa estas comprobaciones sin dar error, devuelve true
        return true;
    }

    private void enviarPeticionAddUsuario(ActionEvent e){
        try{
            if(validarCampos()){
                controlador.nuevoUsuario(
                    tfdNombre.getText(),
                    pfdPassword.getText(),
                    pfdAdminPassword.getText(),
                    cmbTipoCuenta.getSelectionModel().getSelectedIndex());
            }
        } catch (Exception ex){
            MensajesDialogo.mostrarError(ex.getMessage());   
        }
    }
}
