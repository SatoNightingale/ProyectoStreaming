package exceptions;

public class CampoVacioException extends Exception{
    public String getMessage(){
        return "Los campos especificados no pueden estar vacíos";
    }
}
