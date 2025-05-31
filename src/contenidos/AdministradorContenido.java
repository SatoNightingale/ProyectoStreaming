package contenidos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import controllers.DataBaseIncremental;
import exceptions.RutaInvalidaException;
import fileHandling.FileHandling;
import javafx.collections.FXCollections;
import users.*;

public class AdministradorContenido extends DataBaseIncremental<Contenido> implements Serializable{
    private final String carpetaContenidos = "content";
    // private static final long serialVersionUID = 42L;

    // public enum TipoContenido{
    //     VIDEO("Video"),
    //     AUDIO("Audio"),
    //     PODCAST("Pódcast");

    //     private final String nombre;

    //     TipoContenido(String nombre){ this.nombre = nombre; }

    //     public String toString(){ return nombre; }
    // }

    public AdministradorContenido(){
        File contentDir = new File(carpetaContenidos);

        if(!contentDir.exists()) contentDir.mkdirs();
    }
    
    public Contenido addContenido(String mediaPath, String nombre, Creador creador, int tipoContenido, List<Etiqueta> etiquetas) throws IOException, RutaInvalidaException{
        String newPath = agregarMediaFile(mediaPath, nombre);

        return this.addElemento(newPath, nombre, creador, tipoContenido, new ListaEtiquetas(etiquetas));
    }

    protected Contenido registrarElemento(int id, Object...args)  { //throws FileNotFoundException
        String mediaPath = (String) args[0];
        String nombre = (String) args[1];
        Creador creador = (Creador) args[2];
        int tipoContenido =  (Integer) args[3];
        ListaEtiquetas etiquetas = (ListaEtiquetas) args[4];
        
        Contenido nuevoContenido = null;

        switch(tipoContenido){
            case 0: // VIDEO
            //     nuevoContenido = new Consumidor(i, nombre, password); break;
            // case 1: // AUDIO
            //     nuevoContenido = new Creador(i, nombre, password); break;
            // case 2: // PODCAST
            //     nuevoContenido = new Administrador(i, nombre, password); break;
            default: // TODO
                try {
                    nuevoContenido = new Contenido(id, mediaPath, nombre, creador, etiquetas);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
        }

        return nuevoContenido;
    }

    public String agregarMediaFile(String mediaPath, String nombre) throws IOException,RutaInvalidaException{
        String newPath = carpetaContenidos + File.separator + nombre;

        try {
            Path dest = Paths.get(newPath); //comprobar si es una ruta válida
            FileHandling.copiaRecursiva(mediaPath, newPath);
        } catch (InvalidPathException | FileNotFoundException e){
            throw new RutaInvalidaException();
        }
        
        return newPath;
    }

    public void listarContenidos(){
        for (Contenido content : getContenidos()) {
            System.out.println(content.getNombre() + "(" + content + ")");
        }
    }

    // private void writeObject(ObjectOutputStream out) throws IOException{
    //     // super.writeObject(out);
    //     out.defaultWriteObject();
    //     Map<Integer, Contenido> mapaBackup = new HashMap<>(mapaElementos);
    //     out.writeObject(mapaBackup);
    // }

    // private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException{
    //     // super.readObject(in);
    //     in.defaultReadObject();
    //     @SuppressWarnings("unchecked")
    //     Map<Integer, Contenido> mapaBackup = (Map<Integer, Contenido>) in.readObject();
    //     mapaElementos = FXCollections.observableMap(mapaBackup);

    //     for (Contenido c : mapaElementos.values()) c.reloadMedia();
    // }

    public List<Contenido> getContenidos(){ return mapaElementos.values().stream().toList(); }
}
