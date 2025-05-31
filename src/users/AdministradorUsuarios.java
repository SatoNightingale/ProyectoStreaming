package users;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import controllers.DataBaseIncremental;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class AdministradorUsuarios extends DataBaseIncremental<Usuario> implements Serializable {
    private final String adminPassword = "Heart-Shaped Box";

    // private static final long serialVersionUID = 42L;

    public Usuario addUsuario(String nombre, String password, int tipoUsuario){
        return this.addElemento(nombre, password, tipoUsuario);
    }

    protected Usuario registrarElemento(int id, Object...args){
        String nombre = (String) args[0];
        String password = (String) args[1];
        int tipoUsuario = (int) args[2];

        Usuario nuevoUsuario;

        switch(tipoUsuario){
            case 0: //CONSUMIDOR
                nuevoUsuario = new Consumidor(id, nombre, password); break;
            case 1: //PROVEEDOR
                nuevoUsuario = new Creador(id, nombre, password); break;
            case 2: //ADMINISTRADOR
                nuevoUsuario = new Administrador(id, nombre, password); break;
            default: // TODO
                nuevoUsuario = new Usuario(id, nombre, password);
        }

        return nuevoUsuario;
    }

    public boolean usuarioExiste(String nombre, String password){
        return mapaElementos.values().stream().anyMatch(
            u -> u.getNombre().equals(nombre) && u.getPassword().equals(password));
    }

    public boolean validarNuevoUsuario(String nombre, String password, String adminPassword){
        return
            !nombre.isEmpty() &&
            !password.isEmpty() &&
            !usuarioExiste(nombre, password) && (
                adminPassword.equals(this.adminPassword) || 
                adminPassword.equals("")
            );
    }

    public Usuario getUsuario(String nombre, String password){
        Optional<Usuario> opt = mapaElementos.values().stream().filter(
            u -> u.getNombre().equals(nombre) && u.getPassword().equals(password)
        ).findFirst();

        return opt.isPresent()? opt.get() : null;
    }

    public Usuario getUsuario(int id){
        return mapaElementos.get(id);
    }

    // protected void writeAdditionalData(ObjectOutputStream out) throws IOException{}

    // protected void readAdditionalData(ObjectInputStream in) throws IOException{}

    // Aquí hay que especificar métodos personalizados de serialización y deserialización para cargar las propiedades de javafx, que no son serializables
    // private void writeObject(ObjectOutputStream out) throws IOException{
    //     out.defaultWriteObject();

    //     Map<Integer, Usuario> mapaBackup = new HashMap<>(mapaElementos);
    //     out.writeObject(mapaBackup);
    // }

    // private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException{
    //     in.defaultReadObject();
    //     @SuppressWarnings("unchecked")
    //     Map<Integer, Usuario> mapaBackup = (Map<Integer, Usuario>) in.readObject();
    //     mapaElementos = FXCollections.observableMap(mapaBackup);
    // }

    public void listarUsuarios(){
        for (Usuario user : getUsuarios()) {
            System.out.println(user.getNombre() + "(" + user + ")");
        }
    }

    public String getAdminPassword(){ return adminPassword; }

    // public Map<Integer, Usuario> getMapaUsuarios(){ return (Map<Integer, Usuario>) mapaElementos; }
    public List<Usuario> getUsuarios(){ return mapaElementos.values().stream().toList(); }
    // public ObservableMap<Integer, Usuario> mapaUsuariosProperty(){ return mapaElementos; }
}