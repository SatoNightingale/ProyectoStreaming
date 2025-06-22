package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import contenidos.Etiqueta;
import exceptions.ArchivoNoExisteException;
import exceptions.ArchivoNoSeleccionadoException;
import exceptions.FaltanCamposObligatoriosException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import users.Creador;
import users.Usuario;
import utils.MediaPreviewExtractor;
import utils.MensajesDialogo;

public class PostContenidoController extends SceneController{
    @FXML private Button btnAtras;
    @FXML private Button btnPost;
    @FXML private ImageView mediaPreview;
    @FXML private TextField tfdEtiquetasContenido;
    @FXML private TextField tfdNombreContenido;
    @FXML private TextField tfdFilePath;
    @FXML private Label promptView;
	
    private Image imgPreview;
    private int tipoContenido;

    @FXML
    void initialize(){
        btnAtras.setOnAction(e -> goBack());

        mediaPreview.setOnMouseClicked(e -> cargarMediaFile());
        promptView.setOnMouseClicked(e -> cargarMediaFile());

        btnPost.setOnAction(e -> post());
    }

    public void init(Usuario user, Object...data){
        this.usuario = (Creador) user;
        tipoContenido = -1;
    }

    private void post(){
        try{
            if(validarPost()){
                List<String> nombresEtiquetas = Arrays.asList(tfdEtiquetasContenido.getText().split(" "));
                List<Etiqueta> listaEtiquetas = new ArrayList<>();

                String mediaPath = tfdFilePath.getText();

                for(int i = 0; i < nombresEtiquetas.size(); i++) {
                    listaEtiquetas.add(new Etiqueta(nombresEtiquetas.get(i), nombresEtiquetas.size() - i));
                }

                String nombre = tfdNombreContenido.getText();

                controlador.postearContenido(mediaPath, nombre, (Creador) usuario, tipoContenido, listaEtiquetas);

                MensajesDialogo.mostrarInfo("Su contenido se ha añadido a la plataforma");

                goBack();
            }
        } catch(Exception ex){
            MensajesDialogo.mostrarError(ex.getMessage());
        }
    }

    private boolean validarPost() throws ArchivoNoExisteException, FaltanCamposObligatoriosException, ArchivoNoSeleccionadoException{
        File fichero = new File(tfdFilePath.getText());

        ArrayList<String> camposFaltan = new ArrayList<>();

        if(!fichero.exists()) throw new ArchivoNoExisteException(fichero.getAbsolutePath());

        if(tfdNombreContenido.getText().isEmpty()) camposFaltan.add("Nombre del contenido");
        if(tfdEtiquetasContenido.getText().isEmpty()) camposFaltan.add("Etiquetas del contenido");

        if(!camposFaltan.isEmpty())
            throw new FaltanCamposObligatoriosException((String[]) camposFaltan.toArray());

        if(tipoContenido == -1) // No ha seleccionado ningún archivo
            throw new ArchivoNoSeleccionadoException();

        // Si pasa todas estas comprobaciones sin lanzar error, devuelve true
        return true;
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
            String mediaPath = f.getAbsolutePath();
            String extension = "*" + mediaPath.substring(mediaPath.lastIndexOf('.'));

            tfdFilePath.setText(mediaPath);

            if(filtrosVideo.contains(extension)) tipoContenido = 0;
            if(filtrosAudio.contains(extension)) tipoContenido = 1;

            if(tipoContenido == 0){
                MediaPreviewExtractor.getVideoThumbnail(mediaPath, img -> {
                    if(img != null)
                        MediaPreviewExtractor.setFitImageView(mediaPreview, img);
                    else
                        System.out.println("la imagen devuelta por " + mediaPath + " es null");
                });
            } else if(tipoContenido == 1){
                try{
                    imgPreview = MediaPreviewExtractor.getAlbumArt(mediaPath);
                    MediaPreviewExtractor.setFitImageView(mediaPreview, imgPreview);
                } catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    private void goBack(){
        controlador.prepararVistaContenido(usuario);
    }
}