package controllers;
import javafx.scene.Scene;
import users.Usuario;

public abstract class SceneController {
    protected Scene escena;
    protected MainController controlador;
    protected AdministradorEscenas admin;
    protected Usuario usuario;

    // SceneController(MainController parent){ this.parent = parent; }

    public void setParents(MainController controller, AdministradorEscenas admin){
        this.controlador = controller;
        this.admin = admin;
    }

    // public void setUsuario(Usuario usuario){ this.usuario = usuario; }

    public abstract void init(Usuario usuario, Object...data);

    public void setScene(Scene escena){ this.escena = escena; }
    public Scene getScene(){ return escena; }
}
