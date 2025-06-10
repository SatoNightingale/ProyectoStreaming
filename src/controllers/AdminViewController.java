package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import users.Usuario;

public class AdminViewController extends SceneController{
    @FXML private VBox boxListaContenidos;
    @FXML private VBox boxListaCreadores;
    @FXML private VBox boxListaUsuarios;
    @FXML private Button btnAtras;
    
    
    @Override
    public void init(Usuario usuario, Object... data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'init'");
    }


}