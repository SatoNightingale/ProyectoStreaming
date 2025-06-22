package exceptions;

public class UsuarioNoExisteException extends Exception {
    private String nombreUsuario;

    public UsuarioNoExisteException(String nombreUsuario){
        this.nombreUsuario = nombreUsuario;
    }

    public String getMessage(){
        return "El usuario " + nombreUsuario + " no existe";
    }
}
