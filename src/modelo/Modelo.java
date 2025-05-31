package modelo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import contenidos.Contenido;
import contenidos.Etiqueta;
import exceptions.RutaInvalidaException;
import users.Creador;
import users.Usuario;

/**
 * Esta clase es más bien para poder serializar juntas el AdministradorUsuario y el AdministradorContenido y que mantengan las referencias.
 * Además, se encarga de todo el proceso de lectura/escritura. Diría que se ocupa de la persistencia de datos
 */
public class Modelo implements Serializable{
    private AdministradorUsuarios adminUsuarios;
    private AdministradorContenido adminContenidos;
    	
    public Modelo(){
        this.adminUsuarios = new AdministradorUsuarios();
        this.adminContenidos = new AdministradorContenido();
    }


    public Usuario addUsuario(String nombre, String password, int tipoUsuario){
        return adminUsuarios.addUsuario(nombre, password, tipoUsuario);
    }

    public boolean usuarioExiste(String nombre, String password){
        return adminUsuarios.usuarioExiste(nombre, password);
    }

    public boolean validarNuevoUsuario(String nombre, String password, String adminPassword){
        return adminUsuarios.validarNuevoUsuario(nombre, password, adminPassword);
    }

    public Usuario getUsuario(String nombre, String password){
        return adminUsuarios.getUsuario(nombre, password);
    }

    public Usuario getUsuario(int id){
        return adminUsuarios.getElemento(id);
    }

    public String getAdminPassword(){ return adminUsuarios.getAdminPassword(); }



    public Contenido addContenido(String mediaPath, String nombre, Creador creador, int tipoContenido, List<Etiqueta> etiquetas) throws IOException, RutaInvalidaException{
        return adminContenidos.addContenido(mediaPath, nombre, creador, tipoContenido, etiquetas);
    }

    public Contenido getContenido(int id){
        return adminContenidos.getElemento(id);
    }

    public void eliminarContenido(int id){
        adminContenidos.eliminarElemento(id);
    }



    public static Modelo cargarModelo() throws IOException, ClassNotFoundException{
        File inputFile = new File("data.bin");
        Modelo modelo;

        try{
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(inputFile));
            modelo = (Modelo) is.readObject();
            is.close();
        
        } catch (IOException e){
            if(inputFile.exists()){
                inputFile.delete();
                System.out.println("Error al leer el archivo de datos");
            } else
                System.out.println("No se encontró el archivo de datos");

            modelo = new Modelo();
        }

        return modelo;
    }

    // public void cargarBasesDatos(){
    //     File usersDBFile = new File("usuariosDB.bin");
    //     File contenidosDBFile = new File("contenidosDB.bin");

    //     try {
    //         if(!usersDBFile.exists() || !contenidosDBFile.exists())
    //             throw new DatabasesNoEncontradasException();

    //         intentarLeerDatos(usersDBFile, contenidosDBFile);

    //     } catch (DatabasesNoEncontradasException | InvalidClassException e) {
    //         if(usersDBFile.exists())
    //             usersDBFile.delete();
    //         if(contenidosDBFile.exists())
    //             contenidosDBFile.delete();

    //         this.adminUsuarios = new AdministradorUsuarios();
    //         this.adminContenidos = new AdministradorContenido();
    //     } catch (ClassNotFoundException | IOException e) {
    //         // TODO Auto-generated catch block
    //         e.printStackTrace();
    //     }
    // }

    // private void intentarLeerDatos(File userDBFile, File contentDBFile) throws FileNotFoundException, IOException, ClassNotFoundException{
    //     ObjectInputStream inUserDB = new ObjectInputStream(new FileInputStream(userDBFile));
    //     ObjectInputStream inContentDB = new ObjectInputStream(new FileInputStream(contentDBFile));

    //     adminUsuarios = (AdministradorUsuarios) inUserDB.readObject();
    //     adminContenidos = (AdministradorContenido) inContentDB.readObject();

    //     // System.out.println("adminUsuarios size at load: " + adminUsuarios.getUsuarios().size());
    //     // System.out.println("adminContenidos size at load: " + adminContenidos.getContenidos().size());

    //     adminUsuarios.listarUsuarios();
    //     adminContenidos.listarContenidos();

    //     inUserDB.close();
    //     inContentDB.close();
    // }

    
    public void guardarDatos() throws FileNotFoundException, IOException{
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("data.bin"));
        os.writeObject(this);
        os.close();
    }



    public List<Usuario> getUsuarios() { return adminUsuarios.getUsuarios(); }
    public List<Contenido> getContenidos() { return adminContenidos.getContenidos(); }
}
