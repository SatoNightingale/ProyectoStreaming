package controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AdministradorEscena {
    private MainController controlador;

    private Stage stage;
    private Map<String, SceneController> mapaEscenas;

    public AdministradorEscena(Stage primaryStage, MainController mainCon) throws IOException{
        this.controlador = mainCon;
        this.stage = primaryStage;
        mapaEscenas = new HashMap<>();
    }

    public SceneController cargarEscena(String ruta) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
        
        Scene s = new Scene(loader.load());
        SceneController sc = loader.getController();
        sc.setParents(controlador, this);
        sc.setScene(s);
        
        mapaEscenas.put(ruta, sc);

        return sc;
    }

    public void cambiarEscena(String nombreEscena, Object...data) {
        try{
            if(!mapaEscenas.containsKey(nombreEscena))
                cargarEscena(nombreEscena);

            SceneController sc = mapaEscenas.get(nombreEscena);
            sc.init(controlador.getUsuario(), data);

            stage.setScene(sc.getScene());
        } catch(IOException e){
            MainController.showErrorMessage("No se pudo cargar la escena " + nombreEscena);

            e.printStackTrace();
        }
    }

    public Stage getStage(){ return stage; }

    public SceneController getSceneController(String ruta){
        if(mapaEscenas.containsKey(ruta)){
            return mapaEscenas.get(ruta);
        } else {
            return null;
        }
    }
}
