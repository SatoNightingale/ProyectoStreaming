package controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import modelo.Modelo;
import users.*;
import contenidos.*;
import exceptions.RutaInvalidaException;

public class MainController extends Application {
    // private AdministradorUsuarios adminUsuarios;
    // private AdministradorContenido adminContenidos;
    private Modelo modelo;
    private AdministradorEscena adminEscena;

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.modelo = Modelo.cargarModelo();

        adminEscena = new AdministradorEscena(primaryStage, this);
        
        if(modelo.getContenidos().isEmpty()){
            inicializarSimulador();
        }
        
        primaryStage.setOnCloseRequest(this::intentarGuardarDatos);
        
        adminEscena.cambiarEscena("fxml/LoginView.fxml");
        primaryStage.setTitle("Forever in debt to your priceless advice...");
        primaryStage.show();
    }

    public void inicializarSimulador(){
        System.out.println("Llamada a inicializarSimulador");
        Creador creadorInit = (Creador) modelo.addUsuario("Satoshi", "04242564", 1);

        try {
            modelo.addContenido(
                "default content/Fairy Tail Main Theme.mp4", 
                "Fairy Tail Main Theme (Demo Video)",
                creadorInit, 
                0, 
                Arrays.asList(new Etiqueta("Anime", 5), new Etiqueta("Música", 3)));

            modelo.addContenido(
                "default content/amaranth.mp4", 
                "Nightwish - Amaranth (Demo Video)", 
                creadorInit, 
                0, 
                Arrays.asList(new Etiqueta("Música", 3), new Etiqueta("Rock", 5)));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RutaInvalidaException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void usuarioLogin(String nombre, String password) throws Exception{
        Usuario user = modelo.getUsuario(nombre, password);
        System.out.println(user);
        if(user != null){
            prepararVista(user);
        } else{
            throw new Exception("Usuario no encontrado"); //TODO
        }
    }

    public void nuevoUsuario(String nombre, String password, String adminPassword, int tipoCuenta) throws Exception{
        if(modelo.validarNuevoUsuario(nombre, password, adminPassword)){
            Usuario nuevoUsuario = modelo.addUsuario(nombre, password, tipoCuenta);

            prepararVista(nuevoUsuario);
        } else {
            throw new Exception("");
        }
    }

    public void prepararVista(Usuario user){
        PlayList recomendaciones = crearPlaylistRecomendaciones(user);

        adminEscena.getVisorController().init(user, recomendaciones);

        adminEscena.cambiarEscena("fxml/VistaContenido.fxml");
    }

    /** Cuando el usuario termina de ver este contenido (o sea, pasa para otra cosa) se actualizan sus preferencias. Según la fracción del contenido que haya visto, se suman a sus preferencias las etiquetas del contenido multiplicadas por esta fracción. Así, por ejemplo, si ve la mitad del video, se añaden a sus etiquetas las mismas etiquetas del contenido pero con la mitad de su peso. Además, se actualiza el promedio de tiempo que este video es reproducido globalmente
     * @param user El usuario que va a actualizar
     * @param content El contenido que el usuario ha visto
     * @param porcientoVisto La fracción del contenido que vio el usuario. Va desde 0.0 (no vio nada) a 1.0 (lo vio completo)
     */
    public void usuarioVeContenido(Usuario user, Contenido content, double porcientoVisto){
        List<Etiqueta> etiquetasContenido = content.getTags().getList();
        ListaEtiquetas etiquetasUsuario = user.getPreferencias();

        for (Etiqueta etiqueta : etiquetasContenido) {
            etiquetasUsuario.actualizarEtiqueta(etiqueta.getNombre(), etiqueta.getPeso() / porcientoVisto);
        }

        content.addReproduccion(porcientoVisto);
    }

    public void usuarioTocaLike(Usuario user, Contenido content){
        if(!content.getVotantes().contains(user)){
            System.out.println("Contenido no tiene a " + user.getNombre() + "(" + user + ") como votante");
            ListaEtiquetas etiquetasUsuario = user.getPreferencias();
            List<Etiqueta> etiquetasCreador = content.getCreador().getTemas().getList();

            for (Etiqueta etiqueta : etiquetasCreador) {
                etiquetasUsuario.actualizarEtiqueta(etiqueta.getNombre(), etiqueta.getPeso() * 0.5);
            }

            content.getVotantes().add(user);
        } else {
            System.out.println("Contenido tiene a " + user.getNombre() + "(" + user + ") como votante");
            content.getVotantes().remove(user);
        }
    }

    public void usuarioTocaSuscribir(Usuario user, Creador creador){
        if(!user.suscritoACreador(creador))
            suscribirUsuario(user, creador);
        else desuscribirUsuario(user, creador); 
    }

    public void suscribirUsuario(Usuario user, Creador creador){
        ListaEtiquetas etiquetasUsuario = user.getPreferencias();
        List<Etiqueta> etiquetasCreador = creador.getTemas().getList();
        int suscripcionesUsuario = user.getSuscripciones().size();

        for (Etiqueta etiqueta : etiquetasCreador) {
            etiquetasUsuario.actualizarEtiqueta(
                etiqueta.getNombre(),
                // Función racional (lorentziana) para lograr que la influencia de los temas del creador en las preferencias del usuario que se suscribe a él sea menor cuantas más suscripciones tenga el usuario
                etiqueta.getPeso() / (1 + Math.pow(0.1 * suscripcionesUsuario, 2)));
        }

        user.getSuscripciones().add(creador);
        creador.getSuscriptores().add(user);
    }

    public void desuscribirUsuario(Usuario user, Creador creador){
        user.getSuscripciones().remove(creador);
        creador.getSuscriptores().remove(user);
    }

    /**Añade ("sube") un nuevo contenido a la cuenta del creador. Se añaden las etiquetas del contenido subido a los temas del creador, pero tienen menos importancia mientras más contenido haya subido este.
     * @param mediaPath La ruta del archivo (en el disco) del contenido que se va a subir, para importarlo
     * @param nombre El nombre del nuevo contenido
     * @param creador El creador del nuevo contenido
     * @param tipoContenido El tipo de contenido: puede ser de Video (0), Música (1) o Podcast (2)
     * @param etiquetas Las etiquetas del contenido a crear
     * @throws IOException Cuando hay un error al leer el archivo
     * @throws RutaInvalidaException Cuando el archivo no existe, o la ruta provista contiene caracteres no soportados
     */
    public void postearContenido(String mediaPath, String nombre, Creador creador, int tipoContenido, List<Etiqueta> etiquetas) throws IOException, RutaInvalidaException{
        Contenido nc = modelo.addContenido(mediaPath, nombre, creador, tipoContenido, etiquetas);
        creador.getContenidosSubidos().add(nc);
        
        int cantContenidos = creador.getContenidosSubidos().size();
        List<Etiqueta> etiquetasContenido = nc.getTags().getList();
        ListaEtiquetas temasCreador = creador.getTemas();

        for (Etiqueta contentTag : etiquetasContenido) {
            temasCreador.actualizarEtiqueta(contentTag.getNombre(), contentTag.getPeso() / cantContenidos);
        }
    }

    /**Retira un contenido de la cuenta del creador y de la plataforma. Sin embargo, las etiquetas de temas del creador añadidas con este contenido no se modificarán
     * @param id El id del contenido a eliminar
     * @param creador El creador que posee este contenido
     */
    public void retirarContenido(int id){
        Contenido aRetirar = modelo.getContenido(id);
        Creador autor = aRetirar.getCreador();

        modelo.eliminarContenido(id);
        autor.getContenidosSubidos().remove(aRetirar);
    }
    
    /** Crea una PlayList con recomendaciones de contenido personalizadas para este usuario.
     * Para ello toma las preferencias del usuario y organiza los contenidos que existen con un algoritmo que busca que las etiquetas de cada recomendación se adecúen lo más posible a las preferencias del usuario. Para esto es importante entender que, aunque el contenido y el usuario pueden tener etiquetas con el mismo nombre, sus pesos pueden ser distintos; es decir que aunque usuario y contenido compartan una misma etiqueta (con el mismo nombre), se trata de objetos diferentes con pesos diferentes.<br><br>
     *
     * El criterio para ordenar etiquetas es el siguiente: a cada contenido se le asigna una puntuación, y con la instrucción sorted de la clase Stream los contenidos se ordenan de mayor a menor de acuerdo con la puntuación de cada uno. Para asignar la puntuación, se toman una serie de características, cada una de las cuales tiene distinto peso relativo.<br><br>
     * Entre estas características, están la afinidad del contenido con las preferencias del usuario, la cercanía entre estas y los temas del creador, y la popularidad general del creador
     * @param user El usuario para el que se va a hacer la PlayList
     * @return Una PlayList personalizada para el usuario
     */
    public PlayList crearPlaylistRecomendaciones(Usuario user){
        List<Contenido> contenidos = modelo.getContenidos();

        List<Contenido> resultados = contenidos.stream()
            .sorted(Comparator.comparingDouble( // Ordena según los criterios especificados
                (Contenido contenido) -> { return
                    3 * user.afinidadUsuarioContenido(contenido) +
                    2 * contenido.getCreador().afinidadCreadorUsuario(user) +
                        contenido.getCreador().popularidadGeneral();
                }
            ).reversed()).toList();
        
        return new PlayList(resultados);
    }


    public void intentarGuardarDatos(WindowEvent event){
        try{
            modelo.guardarDatos();
        } catch(FileNotFoundException ex){
            System.out.println(ex.getMessage());
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public static Alert showErrorMessage(String mensaje){
        Alert mensajeError = new Alert(Alert.AlertType.ERROR, mensaje, ButtonType.CLOSE);

        mensajeError.showAndWait();
        // mensajeError.getResult().APPLY
        return mensajeError;
    }

    public Modelo getModelo(){ return modelo; }
}
