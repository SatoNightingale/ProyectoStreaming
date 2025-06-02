package controllers;
import javafx.scene.Scene;
import users.Usuario;

public class SceneController {
    protected Scene escena;
    protected MainController controlador;
    protected AdministradorEscena admin;
    protected Usuario usuario;

    // SceneController(MainController parent){ this.parent = parent; }

    public void setParents(MainController controller, AdministradorEscena admin){
        this.controlador = controller;
        this.admin = admin;
    }

    public void setUsuario(Usuario usuario){ this.usuario = usuario; }

    public void setScene(Scene escena){ this.escena = escena; }
    public Scene getScene(){ return escena; }
}
