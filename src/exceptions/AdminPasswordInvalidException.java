package exceptions;

public class AdminPasswordInvalidException extends Exception {
    public String getMessage(){
        return "La contraseña de administrador especificada es incorrecta";
    }
}
