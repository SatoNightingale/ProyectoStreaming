package controllers;

import java.util.List;

import contenidos.Contenido;
import users.Administrador;
import users.Consumidor;
import users.Creador;
import users.Usuario;

public class Modelo {
    List<Usuario> usuarios;
    List<Contenido> contenidos;
    
    public Usuario addUsuario(String nombre, String password, int tipoUsuario){
        Usuario nuevoUsuario;

        switch(tipoUsuario){
            case 0: //CONSUMIDOR
                nuevoUsuario = new Consumidor(nombre, password); break;
            case 1: //PROVEEDOR
                nuevoUsuario = new Creador(nombre, password); break;
            case 2: //ADMINISTRADOR
                nuevoUsuario = new Administrador(nombre, password); break;
            default: // TODO
                nuevoUsuario = new Usuario(nombre, password);
        }

        return nuevoUsuario;
    }

    public List<Usuario> getUsuarios() { return usuarios; }
    public List<Contenido> getContenidos() { return contenidos; }
}
