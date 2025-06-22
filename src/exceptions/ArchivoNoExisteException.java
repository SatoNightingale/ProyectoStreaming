package exceptions;

public class ArchivoNoExisteException extends Exception {
    private String ruta;

    public ArchivoNoExisteException(String ruta){
        this.ruta = ruta;
    }

    public String getMessage(){
        return "El archivo " + ruta + " no existe en el disco";
    }
}
