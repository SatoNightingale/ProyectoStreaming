package controllers;
import javafx.scene.Scene;

public class SceneController {
    protected Scene escena;
    protected MainController controlador;
    protected AdministradorEscena admin;

    // SceneController(MainController parent){ this.parent = parent; }

    public void setParents(MainController controller, AdministradorEscena admin){
        this.controlador = controller;
        this.admin = admin;
    }

    public void setScene(Scene escena){ this.escena = escena; }
    public Scene getScene(){ return escena; }
}
