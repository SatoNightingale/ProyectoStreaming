package utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

public class FileHandling {
    public static void copiaRecursiva(String pathSrc, String pathDest) throws FileNotFoundException, IOException{
        File src = new File(pathSrc);
        File dest = new File(pathDest);

        if(!src.isDirectory()){
                DataInputStream is = new DataInputStream(new FileInputStream(src));
                DataOutputStream os = new DataOutputStream(new FileOutputStream(dest));

                is.transferTo(os);

                is.close();
                os.close();
        } else {
            if(!dest.isDirectory()) // Si src es un directorio, dest también debe serlo
                dest.mkdirs();

            for (String path : src.list())
                copiaRecursiva(
                    pathSrc + File.separator + path,
                    pathDest + File.separator + path);
        }
    }

    // public static void borrarDirectorio(String path) throws Exception{
    //     File toErase = new File(path);
        
    //     if(!toErase.isDirectory()){
    //         if(!toErase.delete())
    //             throw new Exception("No se pudo borrar el archivo: " + toErase.getAbsolutePath());
    //     } else {
    //         for (String dir : toErase.list()) {
    //             borrarDirectorio(path + File.separator + dir);
    //         }

    //         if(!toErase.delete())
    //             throw new Exception("No se pudo borrar el archivo: " + toErase.getAbsolutePath());
    //     }
    // }
}
