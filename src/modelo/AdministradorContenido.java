package modelo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import contenidos.Contenido;
import contenidos.Etiqueta;
import contenidos.ListaEtiquetas;
import exceptions.MediaFileNoEncontradoException;
import users.*;
import utils.FileHandling;

public class AdministradorContenido extends DataBaseIncremental<Contenido> {
    private final String carpetaContenidos = "content";
    
    public AdministradorContenido(){
        File contentDir = new File(carpetaContenidos);

        if(!contentDir.exists()) contentDir.mkdirs();
    }
    
    public Contenido addContenido(String mediaPath, String nombre, Creador creador, int tipoContenido, List<Etiqueta> etiquetas) throws Exception {
        String newPath = agregarMediaFile(mediaPath, nombre);
        Contenido nuevoContenido = this.addElemento(newPath, nombre, creador, tipoContenido, new ListaEtiquetas(etiquetas));
        creador.getContenidosSubidos().add(nuevoContenido);
        
        return nuevoContenido;
    }

    protected Contenido construirElemento(int id, Object...args) throws MediaFileNoEncontradoException {
        String mediaPath = (String) args[0];
        String nombre = (String) args[1];
        Creador creador = (Creador) args[2];
        int tipoContenido =  (Integer) args[3];
        ListaEtiquetas etiquetas = (ListaEtiquetas) args[4];
        
        Contenido nuevoContenido = null;

        nuevoContenido = new Contenido(id, mediaPath, nombre, tipoContenido, creador, etiquetas);

        return nuevoContenido;
    }

    public void eliminarContenido(Contenido content){
        eliminarElemento(content.getId());
    }

    private String agregarMediaFile(String mediaPath, String nombre) throws IOException, FileNotFoundException{
        String newPath = carpetaContenidos + File.separator + nombre;

        FileHandling.copiaRecursiva(mediaPath, newPath);
        
        return newPath;
    }

    // public void listarContenidos(){
    //     for (Contenido content : getContenidos()) {
    //         System.out.println(content.getId() + ": " + content.getNombre());
    //     }
    // }

    /**
     * Se usa para revisar si todos los contenidos tienen la ruta "rota", es decir si todos están asociados a un archivo de medios que no existe en el disco. Devuelve false si al menos uno está asociado a una ruta válida
    */
    public boolean allContentsPathBroken(){
        boolean band = true;
        int i = 0;
        List<Contenido> contenidos = getContenidos();

        while(i < contenidos.size() && band){
            if(!contenidos.get(i).pathIsBroken()) band = false;
        }

        return band;
    }

    public List<Contenido> getContenidos(){ return getListaElementos(); }
}
