package contenidos;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import exceptions.MediaFileNoEncontradoException;
import javafx.scene.media.Media;
import users.*;

public class Contenido implements Serializable{
    protected transient Media media;        // El objeto javafx.Media que contiene los datos del archivo a reproducir
    protected String mediaPath;             // ruta al contenido
    protected int id;                       // ID único del contenido en la contentDB
    protected String nombre;                // Nombre del contenido
    protected int tipo;                     // Tipo del contenido (0: Video, 1: Audio)
    protected Creador creador;              // Creador del contenido
    protected double duracion;              // duración del contenido en segundos
    protected ListaEtiquetas tags;          // las etiquetas del contenido
    protected List<Usuario> votantes;       // todos los usuarios que dieron like a este contenido
    protected List<Comentario> comentarios; // Comentarios a este contenido
    protected int reproducciones;           // La cantidad de reproducciones de este contenido
    protected double sumTiempoReproducido;  // La suma de los porcientos del contenido reproducido en cada visualización

    public Contenido(int id, String mediaPath, String nombre, int tipo, Creador creador, ListaEtiquetas etiquetas) throws MediaFileNoEncontradoException {
        this.mediaPath = mediaPath;
        this.media = cargarMedia(mediaPath);
        
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.creador = creador;
        this.votantes = new ArrayList<>();
        this.comentarios = new ArrayList<>();
        this.tags = etiquetas; // new ListaEtiquetas(etiquetas);

        reproducciones = 0;
        duracion = media.getDuration().toSeconds();
    }

    private Media cargarMedia(String path) throws MediaFileNoEncontradoException{
        File file = new File(path);
        String URI;

        if(file.exists())
            URI = file.toURI().toString();
        else
            throw new MediaFileNoEncontradoException(this, path);
        
        return new Media(URI);
    }

    public double popularidadGlobal(){
        return getLikes() * 3 + promedioTiempoReproducido() * 5 + comentarios.size();
    }

    // Cada vez que el contenido se va a ejecutar, se espera que primero se recargue el Media de la carpeta de medios del programa, y que al terminar se libere de la memoria. De esa forma, solo el Media que se está reproduciendo está cargado, y el uso de la memoria es más eficiente
    public void reloadMedia() throws MediaFileNoEncontradoException {
        this.media = cargarMedia(mediaPath);
    }

    public void liberarMedia() { this.media = null; }

    /*
     * sumTiempoReproducido = porc1 + porc2 + porc3 + ... + porcN (porcX - porciento del contenido reproducido en cada reproduccion)
     * promedioTiempoReproducido = sumTiempoReproducido / cant_reproducciones
    */

    public double promedioTiempoReproducido() {
        return sumTiempoReproducido / reproducciones;
    }

    public void addReproduccion(double porc) {
        reproducciones++;
        sumTiempoReproducido += porc;
    }

    /**
     * Este método se usa para indicar que el archivo al que está asociado este contenido no existe. De esta manera se evita que el reproductor intente ejecutarlo
     */
    public void setBrokenPath(){ this.mediaPath = "BROKEN_PATH"; }
    public boolean pathIsBroken(){ return this.mediaPath.equals("BROKEN_PATH"); }

    public int getId(){ return id; }
    public String getNombre() {return nombre; }
    public Creador getCreador() { return creador; }
    public int getTipoContenido(){ return tipo; }
    public ListaEtiquetas getTags() { return tags; }
    public Etiqueta getTag(String name){ return tags.getEtiqueta(name); }
    public List<Usuario> getVotantes(){ return votantes; }
    public int getLikes(){ return votantes.size(); }
    public List<Comentario> getComentarios(){ return comentarios; }
    public int getReproducciones(){ return reproducciones; }
    public Media getMedia(){ return media; }
    public String getMediaPath(){ return mediaPath; }
}