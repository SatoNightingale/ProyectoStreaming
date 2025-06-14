package controllers;

import java.io.FileNotFoundException;
import contenidos.Comentario;
import contenidos.Contenido;
import contenidos.PlayList;
import users.Administrador;
import users.Creador;
import users.Usuario;
import utils.MediaPreviewExtractor;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.scene.media.MediaPlayer.Status;


public class ViewerController extends SceneController{
    @FXML private VBox panelContenido;
    @FXML private Label lblAutor;
    @FXML private ComboBox<String> cmbCambiarUsuario;
    @FXML private VBox panelComentarios;
    @FXML private TextField tfdComentario;
    @FXML private Button btnEditarPerfil;
    @FXML private Button btnEnviarComentario;
    @FXML private Button btnLike;
    @FXML private MediaView Media;
    @FXML private ImageView imvAlbumArt;
    @FXML private Slider sldMediaProgress;
    @FXML ImageView ibnNext;
    @FXML ImageView ibnPlay;
    @FXML ImageView ibnPrev;
    @FXML private ListView<Contenido> lstRecomendaciones;
    @FXML private Button btnSubscribir;
    @FXML private Label lblTiempoAvanzado;
    @FXML private Label lblDuracionMedia;
    @FXML private Label lblTitulo;
    @FXML private Slider sldVolumen;
    @FXML private VBox panelMediaControl;
    @FXML private Button btnPostContent;
    @FXML private Button btnAdministrar;
    @FXML private StackPane mediaZone;
    @FXML private VBox boxComentarios;
    @FXML private ImageView ibnCerrarComentarios;
    @FXML private VBox boxBtnComentarios;
    @FXML private Label lblUltimoComentario;

    private Usuario usuarioActual;
    private PlayList actualPlayList;

    @FXML
    void initialize(){
        mediaZone.setOnMouseEntered(e -> panelMediaControl.setVisible(true));
        mediaZone.setOnMouseExited(e -> panelMediaControl.setVisible(false));

        btnLike.setOnAction(e -> {
            controlador.usuarioTocaLike(usuarioActual, actualPlayList.contenidoActual());
            updateBtnLike(actualPlayList.contenidoActual());
        });

        btnSubscribir.setOnAction(e -> {
            controlador.usuarioTocaSuscribir(usuarioActual, actualPlayList.contenidoActual().getCreador());
            updateBtnSuscribir(actualPlayList.contenidoActual());
        });

        btnEnviarComentario.setOnAction(e -> {
            controlador.usuarioComenta(this, usuarioActual, actualPlayList.contenidoActual(), tfdComentario.getText());
            updateLblComentarios();
            tfdComentario.setText("");
        });

        sldVolumen.valueProperty().addListener((observable, oldValue, newValue) -> 
            Media.getMediaPlayer().setVolume((double) newValue));

        boxBtnComentarios.setOnMouseClicked(e -> {
            panelComentarios.setVisible(true);
            panelContenido.setVisible(false);
        });

        ibnCerrarComentarios.setOnMouseClicked(e -> {
            panelComentarios.setVisible(false);
            panelContenido.setVisible(true);
        });

        btnPostContent.setOnAction(e -> {
            admin.cambiarEscena("fxml/PostContenidoView.fxml");
            cerrarMediaPlayer();
        });

        btnEditarPerfil.setOnAction(e -> {
            admin.cambiarEscena("fxml/EditarPerfilView.fxml");
        });
    }

    /** Este método se llama cuando se carga la pantalla del visor, ya sea por primera vez o porque hayan cambiado a esta
     * @param user El usuario que está usando la aplicación
     * @param data Se usa para pasar polimorfoseado el objeto Playlist de recomendaciones del usuario (cuando hay que pasarlo) 
     */
    public void init(Usuario user, Object...data){
        this.usuarioActual = user;

        if(data.length > 0){
            PlayList recomendaciones = (PlayList) data[0];
            this.actualPlayList = recomendaciones;
        }

        initListRecomendaciones();
        initcmbCambiarUsuarios();

        btnPostContent.setVisible(usuarioActual instanceof Creador);
        btnAdministrar.setVisible(usuarioActual instanceof Administrador);

        if(!actualPlayList.finPlaylist()){
            Contenido content = actualPlayList.contenidoActual();

            playContenido(content);
        }
    }

    private void playContenido(Contenido content){
        // Cerrar el mediaPlayer si está ejecutando algo
        cerrarMediaPlayer();

        try {
            content.reloadMedia();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        MediaPlayer player = new MediaPlayer(content.getMedia());
        Media.setMediaPlayer(player);

        player.setAutoPlay(true);

        player.setVolume(sldVolumen.getValue());

        if(content.getTipoContenido() == 1){ // Si el contenido es audio
            imvAlbumArt.setVisible(true);
            Media.setVisible(false);
            configureAlbumArt(content);
        } else {
            imvAlbumArt.setVisible(false);
            Media.setVisible(true);
        }

        lblTitulo.setText(content.getNombre());
        lblAutor.setText(content.getCreador().getNombre());

        updateBtnLike(content);
        updateBtnSuscribir(content);

        initProgressSlider();

        Media.getMediaPlayer().setOnEndOfMedia(() -> {
            // Pequeña pausita de dos segundos antes de reproducir el siguiente contenido
            PauseTransition pausaAntesDeContinuar = new PauseTransition(Duration.seconds(2));
            pausaAntesDeContinuar.setOnFinished(e -> playNext());
            pausaAntesDeContinuar.play();
        });

        reconstruirComentarios(content);
    }

    public void cerrarMediaPlayer(){
        MediaPlayer player = Media.getMediaPlayer();

        if(player != null){
            controlador.usuarioVeContenido(usuarioActual, actualPlayList.contenidoActual(), getFraccionVisto());

            player.dispose();
        }
    }

    private void configureAlbumArt(Contenido content) {
        try{
            Image img = utils.MediaPreviewExtractor.getAlbumArt(content.getMediaPath());

            if(img != null){
                MediaPreviewExtractor.setFitImageView(imvAlbumArt, img);
            } else
                throw new Exception("");
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("El archivo no tiene art o no se pudo cargar");
        }
    }

    private void initListRecomendaciones(){
        lstRecomendaciones.getItems().setAll(actualPlayList.getListaContenidos());
        
        lstRecomendaciones.setCellFactory(lv -> {
            // Construir cada celda (elemento) de la lista
            ListCell<Contenido> celda = new ListCell<>() {
                protected void updateItem(Contenido item, boolean empty){
                    super.updateItem(item, empty);

                    if(empty || item == null){
                        setText(null);
                    } else {
                        Label lblNombre = new Label(item.getNombre());
                        lblNombre.setFont(new Font(14));
                        Label lblAutor = new Label(item.getCreador().getNombre());
                        lblAutor.setFont(new Font(11));

                        VBox box = new VBox(lblNombre, lblAutor);
                        setGraphic(box);
                    }
                }
            };

            // Acción al dar doble click: reproducir el contenido recomendado
            celda.setOnMouseClicked(e -> {
                if(e.getClickCount() == 2 && !celda.isEmpty()){
                    Contenido content = celda.getItem();
                    playContenido(content);
                }
            });

            return celda;
        });
    }

    private void initcmbCambiarUsuarios(){
        // Crea el comboBox de cambiar usuario con una lista de nombres de todos los usuarios
        // También podría usar controlador.getModelo().getUsuarios(), pero para eso tendría que implementar el toString de Usuario y por cuestiones de debug lo necesito sin implementar
        cmbCambiarUsuario.setItems(FXCollections.observableArrayList(
            controlador.getModelo().getUsuarios().stream().map(Usuario::getNombre).toList()
        ));

        cmbCambiarUsuario.getSelectionModel().select(usuarioActual.getId());

        // Este setOnAction no se ejecuta si se selecciona el mismo elemento que ya estaba
        cmbCambiarUsuario.setOnAction(e -> {
            admin.cambiarEscena("fxml/LoginView.fxml");
            cerrarMediaPlayer();
            // System.out.println("Wanna change usuarioActual huh");
        });

        // cmbCambiarUsuario.setOnMouseClicked(e -> {
        //     admin.cambiarEscena("fxml/LoginView.fxml");
        // });
    }

    private void initProgressSlider(){
        MediaPlayer mediaPlayer = Media.getMediaPlayer();

        mediaPlayer.setOnReady(() -> {
            sldMediaProgress.setMax(mediaPlayer.getTotalDuration().toSeconds());
            lblDuracionMedia.setText(crearStringTiempo(mediaPlayer.getTotalDuration()));
        });

        // Actualizar mientras se reproduce
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if(!sldMediaProgress.isValueChanging()){ // Si no está siendo manipulado manualmente
                sldMediaProgress.setValue(newValue.toSeconds());
                lblTiempoAvanzado.setText(crearStringTiempo(newValue));
            }
        });
        
        sldMediaProgress.setOnMousePressed(e -> mediaPlayer.seek(Duration.seconds(sldMediaProgress.getValue())));
        sldMediaProgress.setOnMouseDragged(e -> mediaPlayer.seek(Duration.seconds(sldMediaProgress.getValue())));
        sldMediaProgress.setOnMouseReleased(e -> mediaPlayer.seek(Duration.seconds(sldMediaProgress.getValue())));
    }

    private void updateBtnLike(Contenido content){
        btnLike.setText(String.valueOf(content.getLikes()));

        if(content.getVotantes().contains(usuarioActual)){
            btnLike.setStyle("-fx-background-color: lightblue");
        } else {
            btnLike.setStyle("");
        }
    }

    private void updateBtnSuscribir(Contenido content){
        if(usuarioActual.suscritoACreador(content.getCreador())){
            btnSubscribir.setText("Suscrito");
            btnSubscribir.setStyle("-fx-background-color: lightblue");
        } else {
            btnSubscribir.setText("Suscribirme");
            btnSubscribir.setStyle("");
        }
    }

    private void updateLblComentarios(){
        if(!actualPlayList.contenidoActual().getComentarios().isEmpty()){
            Comentario lastComment = actualPlayList.contenidoActual().getComentarios().get(actualPlayList.contenidoActual().getComentarios().size() - 1);

            lblUltimoComentario.setText(lastComment.getAutor().getNombre() + ": " + lastComment.getTexto());
        }else lblUltimoComentario.setText("Sin comentarios hasta ahora");
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

    private void playNext(){
        if(!actualPlayList.finPlaylist()){
            playContenido(actualPlayList.siguiente());
        } else {
            playContenido(actualPlayList.reload());
        }
    }

    @FXML private void initIbnNext(MouseEvent evt){
        playNext();
    }

    @FXML private void initIbnPrev(MouseEvent evt){
        if(!actualPlayList.inicioPlayList()){
            cerrarMediaPlayer();
            playContenido(actualPlayList.anterior());
        } else {
            playContenido(actualPlayList.getLast());
        }
    }

    private void reconstruirComentarios(Contenido content){
        boxComentarios.getChildren().clear();

        Comentario comment = null;

        for (int i = 0; i < content.getComentarios().size(); i++) {
            comment = content.getComentarios().get(i);
            addPanelComentario(comment);
        }

        if(comment != null)
            lblUltimoComentario.setText(comment.getAutor().getNombre() + ": " + comment.getTexto());
        else lblUltimoComentario.setText("Sin comentarios hasta ahora");
    }

    private String crearStringTiempo(Duration duracion){
        String horas, minutos, segundos;
        /*
         * Si hay 2 horas, 40 minutos y 35 segundos
         * 2 horas
         * 160 minutos
         * 9635 segundos
         * 160 % 60 = 40 min
         * 9635 % 60 = 35 seg
         */

        horas = String.valueOf((int) duracion.toHours());
        minutos = String.valueOf((int) duracion.toMinutes() % 60);
        segundos = String.valueOf((int) duracion.toSeconds() % 60);

        if(horas.length() > 2) horas = "XX";
        if(horas.length() < 2) horas = "0" + horas;
        if(minutos.length() < 2) minutos = "0" + minutos;
        if(segundos.length() < 2) segundos = "0" + segundos;

        return horas + ":" + minutos + ":" + segundos;
    }

    public void addPanelComentario(Comentario comment){
        Label autorLabel = new Label(comment.getAutor().getNombre());
        Label textoLabel = new Label(comment.getTexto());

        VBox box = new VBox(autorLabel, textoLabel);

        box.setStyle("-fx-border-color: black; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-background-color: #e0e0e0");
        VBox.setMargin(box, new Insets(0, 10, 0, 10));
        box.setPadding(new Insets(0, 10, 0, 10));

        box.setPrefWidth(VBox.USE_COMPUTED_SIZE);
        box.setPrefHeight(VBox.USE_COMPUTED_SIZE);
        box.setMaxWidth(VBox.USE_PREF_SIZE);

        boxComentarios.getChildren().add(box);
    }

    public double getFraccionVisto(){
        return Media.getMediaPlayer().getCurrentTime().toSeconds() / Media.getMediaPlayer().getTotalDuration().toSeconds();
    }
}
