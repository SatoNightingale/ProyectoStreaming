package exceptions;

public class NoSePudoGuardarDatosException extends Exception {
    public String getMessage(){
        return "No se pudo guardar los datos del programa";
    }
}
