package exceptions;

public class IncorrectPasswordException extends Exception{
    public String getMessage(){
        return "La contraseña especificada es incorrecta";
    }
}
