package controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AdministradorEscena {
    private MainController controladorPrincipal;

    private Stage stage;
    private Map<String, SceneController> sceneMap;

    private ViewerController contentViewerController;

    public AdministradorEscena(Stage primaryStage, MainController mainCon) throws IOException{
        this.controladorPrincipal = mainCon;
        this.stage = primaryStage;
        sceneMap = new HashMap<>();

        contentViewerController = (ViewerController) cargarEscena("fxml/VistaContenido.fxml");
    }

    private SceneController cargarEscena(String ruta) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
        
        Scene s = new Scene(loader.load());
        SceneController sc = loader.getController();
        sc.setParents(controladorPrincipal, this);
        sc.setScene(s);
        
        sceneMap.put(ruta, sc);

        return sc;
    }

    public void cambiarEscena(String nombreEscena) {
        try{
            if(!sceneMap.containsKey(nombreEscena))
                cargarEscena(nombreEscena);

            stage.setScene(sceneMap.get(nombreEscena).getScene());
        } catch(IOException e){
            MainController.showErrorMessage("No se pudo cargar la escena " + e.getMessage());

            e.printStackTrace();
        }
    }

    public ViewerController getVisorController(){ return contentViewerController; }
}
