package exceptions;

public class ArchivoNoSeleccionadoException extends Exception {
    public String getMessage(){
        return "Debes seleccionar un archivo de medios para subir";
    }
}
