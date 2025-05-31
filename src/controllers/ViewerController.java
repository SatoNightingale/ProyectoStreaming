package controllers;

import java.io.FileNotFoundException;

import contenidos.Contenido;
import contenidos.PlayList;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import javafx.scene.media.MediaPlayer.Status;
import users.Usuario;

public class ViewerController extends SceneController{
    @FXML private Label lblAutor;
    @FXML private ComboBox<Usuario> cmbCambiarUsuario;
    @FXML private VBox panelComentarios;
    @FXML private TextField tfdComentario;
    @FXML private Button btnEditarPerfil;
    @FXML private Button btnEnviarComentario;
    @FXML private Button btnLike;
    @FXML private MediaView Media;
    @FXML private Slider sldMediaProgress;
    // @FXML private Button btnNext;
    // @FXML private Button btnPlay;
    // @FXML private Button btnPrev;
    @FXML ImageView ibnNext;
    @FXML ImageView ibnPlay;
    @FXML ImageView ibnPrev;
    @FXML private VBox panelRecomendados;
    @FXML private Button btnSubscribir;
    @FXML private Label lblTiempoAvanzado;
    @FXML private Label lblDuracionMedia;
    @FXML private Label lblTitulo;
    @FXML private Slider sldVolumen;
    @FXML private VBox panelMediaControl;
    @FXML private Button btnPostContent;
    @FXML private Button btnAdministrar;
    @FXML private StackPane mediaZone;

    private Usuario user;
    private PlayList actualPlayList;

    @FXML
    void initialize(){
        mediaZone.setOnMouseEntered(e -> panelMediaControl.setVisible(true));
        mediaZone.setOnMouseExited(e -> panelMediaControl.setVisible(false));

        btnLike.setOnAction(e -> {
            controlador.usuarioTocaLike(user, actualPlayList.contenidoActual());
            updateBtnLike(actualPlayList.contenidoActual());
        });

        btnSubscribir.setOnAction(e -> {
            controlador.usuarioTocaSuscribir(user, actualPlayList.contenidoActual().getCreador());
            updateBtnSuscribir(actualPlayList.contenidoActual());
        });

        sldVolumen.valueProperty().addListener((observable, oldValue, newValue) -> 
            Media.getMediaPlayer().setVolume((double) newValue));
    }

    /** Este método se llama cuando se carga la pantalla del visor, ya sea por primera vez o porque hayan cambiado a esta
     * @param user El usuario que está usando la aplicación
     */
    public void init(Usuario user, PlayList recomendaciones){
        this.user = user;
        this.actualPlayList = recomendaciones;

        initcmbCambiarUsuarios();

        if(!actualPlayList.finPlaylist()){
            Contenido content = actualPlayList.contenidoActual();

            playContenido(content);
        }
    }

    private void playContenido(Contenido content){
        // Cerrar el mediaPlayer si está ejecutando algo
        MediaPlayer player = Media.getMediaPlayer();

        if(player != null){
            Media.getMediaPlayer().dispose();
        }

        try {
            content.reloadMedia();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        player = new MediaPlayer(content.getMedia());
        Media.setMediaPlayer(player);

        player.setAutoPlay(true);

        player.setVolume(sldVolumen.getValue());

        lblTitulo.setText(content.getNombre());
        lblAutor.setText(content.getCreador().getNombre());

        updateBtnLike(content);
        updateBtnSuscribir(content);

        initProgressSlider();

        content.getCreador().listarSuscriptores();
        user.listarSuscripciones();
        content.listarVotantes();
    }

    private void initcmbCambiarUsuarios(){
        // Inicializar los items
        // cmbCambiarUsuario.itemsProperty().bind(Bindings.createObjectBinding(
        //     () -> FXCollections.observableArrayList(
        //         controlador.getAdminUsuarios().mapaUsuariosProperty().values()),
        //     controlador.getAdminUsuarios().mapaUsuariosProperty()
        // ));

        cmbCambiarUsuario.setItems(FXCollections.observableArrayList(controlador.getModelo().getUsuarios()));

        cmbCambiarUsuario.getSelectionModel().select(user.getId());

        // Este setOnAction no se ejecuta si se selecciona el mismo elemento que ya estaba
        cmbCambiarUsuario.setOnAction(e -> {
            System.out.println("Wanna change user huh");
        });
    }

    private void initProgressSlider(){
        MediaPlayer mediaPlayer = Media.getMediaPlayer();

        mediaPlayer.setOnReady(() -> {
            sldMediaProgress.setMax(mediaPlayer.getTotalDuration().toSeconds());
            lblDuracionMedia.setText(crearStringTiempo(mediaPlayer.getTotalDuration()));
        });

        // Actualizar mientras se reproduce
        mediaPlayer.currentTimeProperty().addListener((observale, oldValue, newValue) -> {
            if(!sldMediaProgress.isValueChanging()){ // Si no está siendo manipulado manualmente
                sldMediaProgress.setValue(newValue.toSeconds());
                // lblTiempoAvanzado.setText(String.valueOf(newValue.toSeconds()) + " / " + String.valueOf(mediaPlayer.getTotalDuration().toSeconds()));
                lblTiempoAvanzado.setText(crearStringTiempo(newValue));
            }
        });
        
        sldMediaProgress.setOnMousePressed(e -> mediaPlayer.seek(Duration.seconds(sldMediaProgress.getValue())));
        sldMediaProgress.setOnMouseDragged(e -> mediaPlayer.seek(Duration.seconds(sldMediaProgress.getValue())));
        sldMediaProgress.setOnMouseReleased(e -> mediaPlayer.seek(Duration.seconds(sldMediaProgress.getValue())));

        // // Correr el video moviendo el slider
        // sldMediaProgress.valueChangingProperty().addListener((observable, wasChanging, isValueChanging) -> {
        //     if(!isValueChanging)
        //         mediaPlayer.seek(Duration.seconds(sldMediaProgress.getValue())); 
        // });

        // sldMediaProgress.valueChangingProperty().addListener((observable, wasChanging, isValueChanging) -> {
        //     if(sldMediaProgress.isValueChanging())
        //         mediaPlayer.seek(Duration.seconds(sldMediaProgress.getValue())); 
        // });
    }

    private void updateBtnLike(Contenido content){
        // btnLike.textProperty().unbind();
        // btnLike.textProperty().bind(Bindings.createStringBinding(
        //     () -> String.valueOf(content.getLikes()), content.votantesProperty()
        // ));

        // btnLike.styleProperty().unbind();
        // btnLike.styleProperty().bind(Bindings.createStringBinding(() -> {
        //     return (content.getVotantes().contains(user))?
        //         "-fx-background-color: lightblue" : "";
        // }, content.votantesProperty()));

        // btnLike.setText(String.valueOf(content.getLikes()));
        // btnLike.setStyle((content.getVotantes().contains(user))?
        //         "-fx-background-color: lightblue" : "");

        btnLike.setText(String.valueOf(content.getLikes()));

        if(content.getVotantes().contains(user)){
            btnLike.setStyle("-fx-background-color: lightblue");
        } else {
            btnLike.setStyle("");
        }
    }

    private void updateBtnSuscribir(Contenido content){
        // if(!user.suscritoACreador(content.getCreador())){
        //     btnSubscribir.setText("Suscrito");
        //     btnSubscribir.setStyle("-fx-background-color: lightblue");
        // } else {
        //     btnSubscribir.setText("Suscribirme");   
        // }

        // btnSubscribir.setText((
        //     !user.suscritoACreador(content.getCreador()))? "Suscribirme" : "Suscrito"
        // );

        // btnSubscribir.styleProperty().unbind();
        // btnSubscribir.styleProperty().bind(Bindings.createStringBinding(() -> {
        //     return (user.suscritoACreador(content.getCreador()))?
        //         "-fx-background-color: lightblue" : "";
        // }, user.suscripcionesProperty()));

        // btnSubscribir.textProperty().unbind();
        // btnSubscribir.textProperty().bind(Bindings.createStringBinding(() -> {
        //     return (user.suscritoACreador(content.getCreador()))?
        //         "Suscrito" : "Suscribirme";
        // }, user.suscripcionesProperty()));

        if(user.suscritoACreador(content.getCreador())){
            btnSubscribir.setText("Suscrito");
            btnSubscribir.setStyle("-fx-background-color: lightblue");
        } else {
            btnSubscribir.setText("Suscribirme");
            btnSubscribir.setStyle("");
        }
    }


    @FXML private void initIbnPlay(MouseEvent evt){
        MediaPlayer player = Media.getMediaPlayer();
        Status estado = player.getStatus();

        if(estado.equals(Status.PAUSED) || estado.equals(Status.STOPPED) || estado.equals(Status.HALTED)){
            player.play();
            ibnPlay.setImage(new Image("resources/ic_action_pause.png"));
        }else{
            player.pause();
            ibnPlay.setImage(new Image("resources/ic_action_play.png"));
        }
    }

    @FXML private void initIbnNext(MouseEvent evt){
        System.out.println("next");
        if(!actualPlayList.finPlaylist()){
            playContenido(actualPlayList.siguiente());
        } else {
            //TODO
        }
    }

    @FXML private void initIbnPrev(MouseEvent evt){
        System.out.println("prev");
        if(!actualPlayList.inicioPlayList()){
            playContenido(actualPlayList.anterior());
        } else {
            // TODO
        }
    }

    private String crearStringTiempo(Duration duracion){
        String horas, minutos, segundos;

        horas = String.valueOf((int) duracion.toHours());
        minutos = String.valueOf((int) duracion.toMinutes());
        segundos = String.valueOf((int) duracion.toSeconds());

        if(horas.length() > 2) horas = "XX";
        if(horas.length() < 2) horas = "0" + horas;
        if(minutos.length() < 2) minutos = "0" + minutos;
        if(segundos.length() < 2) segundos = "0" + segundos;

        return horas + ":" + minutos + ":" + segundos;
    }
}
