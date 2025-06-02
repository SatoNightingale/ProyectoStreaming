package modelo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import contenidos.Contenido;
import contenidos.Etiqueta;
import contenidos.ListaEtiquetas;
import users.*;
import utils.FileHandling;

public class AdministradorContenido extends DataBaseIncremental<Contenido> {
    private final String carpetaContenidos = "content";
    
    public AdministradorContenido(){
        File contentDir = new File(carpetaContenidos);

        if(!contentDir.exists()) contentDir.mkdirs();
    }
    
    public Contenido addContenido(String mediaPath, String nombre, Creador creador, int tipoContenido, List<Etiqueta> etiquetas) throws IOException, FileNotFoundException{
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

        try {
            nuevoContenido = new Contenido(id, mediaPath, nombre, tipoContenido, creador, etiquetas);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return nuevoContenido;
    }

    private String agregarMediaFile(String mediaPath, String nombre) throws IOException, FileNotFoundException{
        String newPath = carpetaContenidos + File.separator + nombre;

        FileHandling.copiaRecursiva(mediaPath, newPath);
        
        return newPath;
    }

    public void listarContenidos(){
        for (Contenido content : getContenidos()) {
            System.out.println(content.getNombre() + "(" + content + ")");
        }
    }

    public List<Contenido> getContenidos(){ return mapaElementos.values().stream().toList(); }
}
