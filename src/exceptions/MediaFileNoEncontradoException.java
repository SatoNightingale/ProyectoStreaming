package exceptions;

import contenidos.Contenido;

public class MediaFileNoEncontradoException extends RuntimeException {
    private Contenido contenido;
    private String ruta;

    public MediaFileNoEncontradoException(Contenido contenido, String ruta){
        this.contenido = contenido;
        this.ruta = ruta;
    }

    public String getMessage(){
        return "El archivo " + ruta + " correspondiente al contenido " + contenido.getNombre() + " no existe en el disco";
    }
}
