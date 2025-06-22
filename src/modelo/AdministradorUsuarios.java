package modelo;

import java.util.List;
import java.util.Optional;

import exceptions.AdminPasswordInvalidException;
import exceptions.CampoVacioException;
import exceptions.UsuarioYaExisteException;
import users.Administrador;
import users.Creador;
import users.Usuario;

public class AdministradorUsuarios extends DataBaseIncremental<Usuario> {
    private final String adminPassword = "Heart-Shaped Box";

    public Usuario addUsuario(String nombre, String password, int tipoUsuario) {
        return super.addElemento(nombre, password, tipoUsuario);
    }

    protected Usuario construirElemento(int id, Object...args){
        String nombre = (String) args[0];
        String password = (String) args[1];
        int tipoUsuario = (int) args[2];

        Usuario nuevoUsuario;

        switch(tipoUsuario){
            case 1: //PROVEEDOR
                nuevoUsuario = new Creador(id, nombre, password); break;
            case 2: //ADMINISTRADOR
                nuevoUsuario = new Administrador(id, nombre, password); break;
            default:
                nuevoUsuario = new Usuario(id, nombre, password);
        }

        return nuevoUsuario;
    }

    public boolean usuarioExiste(String nombre){
        return listaElementos.stream().anyMatch(
            u -> u.getNombre().equals(nombre));
    }

    public boolean validarNuevoUsuario(String nombre, String password, String adminPassword, boolean comoAdmin) throws AdminPasswordInvalidException, UsuarioYaExisteException, CampoVacioException {
        if(nombre.isBlank() || password.isBlank()) throw new CampoVacioException();

        if(usuarioExiste(nombre)) throw new UsuarioYaExisteException();

        // Si el nuevo usuario va a ser un administrador, tiene que tener bien la contraseña de admin
        if(comoAdmin && adminPassword.equals(this.adminPassword)){
            throw new AdminPasswordInvalidException();
        }

        // Si pasa todas estas comprobaciones sin dar error, retorna verdadero
        return true;
    }

    public Usuario getUsuario(String nombre, String password){
        Optional<Usuario> opt = listaElementos.stream().filter(
            u -> u.getNombre().equals(nombre) && u.getPassword().equals(password)
        ).findFirst();

        return opt.isPresent()? opt.get() : null;
    }

    public void cambiarDatosUsuario(Usuario user, String nuevoNombre, String nuevaPassword){
        user.setNombre(nuevoNombre);
        user.setPassword(nuevaPassword);
    }

    public void eliminarUsuario(Usuario user){
        this.eliminarElemento(user.getId());
    }

    // public void listarUsuarios(){
    //     for (Usuario user : getUsuarios()) {
    //         System.out.println(user.getNombre() + "(" + user + ")");
    //     }
    // }

    public String getAdminPassword(){ return adminPassword; }

    public List<Usuario> getUsuarios(){ return getListaElementos(); }
}