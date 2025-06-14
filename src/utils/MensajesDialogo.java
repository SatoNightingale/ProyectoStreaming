package utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class MensajesDialogo {
    public static ButtonType mostrarError(String mensaje){
        Alert mensajeError = new Alert(Alert.AlertType.ERROR, mensaje, ButtonType.CLOSE);

        mensajeError.showAndWait();
        
        return mensajeError.getResult();
    }

    public static ButtonType mostrarInfo(String mensaje){
        Alert mensajeInfo = new Alert(Alert.AlertType.INFORMATION, mensaje, ButtonType.CLOSE);

        mensajeInfo.showAndWait();

        return mensajeInfo.getResult();
    }

    public static ButtonType mostrarEleccion(String mensaje){
        Alert mensajeEleccion = new Alert(Alert.AlertType.WARNING, mensaje, ButtonType.YES, ButtonType.NO);

        mensajeEleccion.showAndWait();

        return mensajeEleccion.getResult();
    }
}
