package exceptions;

public class UsuarioYaExisteException extends Exception{
    public String getMessage(){
        return "Un usuario con ese nombre ya existe";
    }
}
