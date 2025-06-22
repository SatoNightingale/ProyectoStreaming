package exceptions;

public class FaltanCamposObligatoriosException extends Exception{
    private String[] camposPorLlenar;

    public FaltanCamposObligatoriosException(String...camposPorLlenar){
        this.camposPorLlenar = camposPorLlenar;
    }

    public String getMessage(){
        String campos = String.join(", ", camposPorLlenar);

        return "Faltan campos obligatorios. Por favor, llene los campos siguientes: " + campos;
    }
}
