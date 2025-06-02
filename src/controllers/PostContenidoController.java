package controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import contenidos.Etiqueta;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import users.Creador;
import utils.MediaPreviewExtractor;

public class PostContenidoController extends SceneController{
    @FXML private Button btnAtras;
    @FXML private Button btnPost;
    @FXML private ImageView mediaPreview;
    @FXML private TextField tfdEtiquetasContenido;
    @FXML private TextField tfdNombreContenido;
    @FXML private Text promptView;
	
    private Creador creador;
    private String mediaPath;
    private Image imgPreview;
    private int tipo;

    @FXML
    void initialize(){
        btnAtras.setOnAction(e -> {
            try {
                goBack();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });

        mediaPreview.setOnMouseClicked(e -> cargarMediaFile());
        promptView.setOnMouseClicked(e -> cargarMediaFile());

        btnPost.setOnAction(e -> post());
    }

    public void init(Creador creador){
        this.creador = creador;
        mediaPath = "";
        tipo = -1;
    }

    private void post(){
        if(validarPost()){
            List<String> nombresEtiquetas = Arrays.asList(tfdEtiquetasContenido.getText().split(" "));
            List<Etiqueta> listaEtiquetas = new ArrayList<>();

            for(int i = 0; i < nombresEtiquetas.size(); i++) {
                listaEtiquetas.add(new Etiqueta(nombresEtiquetas.get(i), nombresEtiquetas.size() - i));
            }

            String nombre = tfdNombreContenido.getText();

            try{
                controlador.postearContenido(mediaPath, nombre, creador, tipo, listaEtiquetas);
                MainController.showInfoMessage("Su contenido se ha añadido a la plataforma");
                goBack();
            } catch (Exception ex){
                MainController.showErrorMessage("Ocurrió un error y no se pudo subir el contenido\n" + ex.getMessage());
                ex.printStackTrace();
            }
        } else {
            MainController.showErrorMessage("Por favor, llene todos los campos");
        }
    }

    private boolean validarPost(){
        return !tfdNombreContenido.getText().isEmpty() && !tfdEtiquetasContenido.getText().isEmpty() && !mediaPath.isEmpty() && tipo != -1;
    }

    private void cargarMediaFile(){
        FileChooser fileChooser = new FileChooser();

        List<String> filtrosVideo = Arrays.asList("*.mp4", "*.flv", "*.fxm");
        List<String> filtrosAudio = Arrays.asList("*.mp3", "*.wav", "*.aiff", "*.aac");

        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Video", filtrosVideo),
            new FileChooser.ExtensionFilter("Audio", filtrosAudio)
        );

        File f = fileChooser.showOpenDialog(admin.getStage());

        if(f != null){
            String rutaF = f.getAbsolutePath();
            String extensionF = "*" + rutaF.substring(rutaF.lastIndexOf('.'));

            if(filtrosVideo.contains(extensionF)) tipo = 0;
            if(filtrosAudio.contains(extensionF)) tipo = 1;

            mediaPath = rutaF;

            // imgPreview = null;

            if(tipo == 0){
                MediaPreviewExtractor.getVideoThumbnail(mediaPath, img -> {
                    // System.out.println("intentando cargar imagen");
                    if(img != null)
                        MediaPreviewExtractor.setFitImageView(mediaPreview, img);
                });

                // System.out.println("tipo de video");
            } else if(tipo == 1){
                try{
                    imgPreview = MediaPreviewExtractor.getAlbumArt(mediaPath);
                    MediaPreviewExtractor.setFitImageView(mediaPreview, imgPreview);

                    // System.out.println("tipo de audio");
                } catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    private void goBack() throws IOException{
        controlador.prepararVistaContenido(creador);
        // admin.cambiarEscena("fxml/VistaContenido.fxml");
    }
}