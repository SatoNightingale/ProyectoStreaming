package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import contenidos.Contenido;
import contenidos.Etiqueta;
import contenidos.ListaEtiquetas;
import exceptions.RutaInvalidaException;
import fileHandling.FileHandling;
import users.Administrador;
import users.Consumidor;
import users.Creador;
import users.Usuario;

public class Modelo implements Serializable{
    List<Usuario> listaUsuarios;
    List<Contenido> listaContenidos;
    
    // Puse esta contraseña porque me recordó a ti
    private final String adminPassword = "Heart-Shaped Box";

    private final String carpetaContenidos = "content";


    public Modelo(){
        listaUsuarios = new ArrayList<>();

        listaContenidos = new ArrayList<>();

        File contentDir = new File(carpetaContenidos);
        if(!contentDir.exists()) contentDir.mkdirs();
    }

    public Usuario addUsuario(String nombre, String password, int tipoUsuario){
        Usuario nuevoUsuario;

        switch(tipoUsuario){
            case 0: //CONSUMIDOR
                nuevoUsuario = new Consumidor(listaUsuarios.size(), nombre, password); break;
            case 1: //PROVEEDOR
                nuevoUsuario = new Creador(listaUsuarios.size(), nombre, password); break;
            case 2: //ADMINISTRADOR
                nuevoUsuario = new Administrador(listaUsuarios.size(), nombre, password); break;
            default: // TODO
                nuevoUsuario = new Usuario(listaUsuarios.size(), nombre, password);
        }

        return nuevoUsuario;
    }

    public boolean usuarioExiste(String nombre, String password){
        return listaUsuarios.stream().anyMatch(
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
        Optional<Usuario> opt = listaUsuarios.stream().filter(
            u -> u.getNombre().equals(nombre) && u.getPassword().equals(password)
        ).findFirst();

        return opt.isPresent()? opt.get() : null;
    }



    public Contenido addContenido(String mediaPath, String nombre, Creador creador, int tipoContenido, List<Etiqueta> etiquetas) throws IOException, RutaInvalidaException{
        String newPath = agregarMediaFile(mediaPath, nombre);

        Contenido nuevoContenido = null;

        switch(tipoContenido){
            case 0: // VIDEO
            //     nuevoContenido = new Consumidor(i, nombre, password); break;
            // case 1: // AUDIO
            //     nuevoContenido = new Creador(i, nombre, password); break;
            // case 2: // PODCAST
            //     nuevoContenido = new Administrador(i, nombre, password); break;
            default: // TODO
                try {
                    nuevoContenido = new Contenido(listaContenidos.size(), newPath, nombre, creador, new ListaEtiquetas(etiquetas));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
        }

        return nuevoContenido;
    }

    public String agregarMediaFile(String mediaPath, String nombre) throws IOException,RutaInvalidaException{
        String newPath = carpetaContenidos + File.separator + nombre;

        try {
            Path dest = Paths.get(newPath); //comprobar si es una ruta válida
            FileHandling.copiaRecursiva(mediaPath, newPath);
        } catch (InvalidPathException | FileNotFoundException e){
            throw new RutaInvalidaException();
        }
        
        return newPath;
    }


    public List<Usuario> getUsuarios() { return listaUsuarios; }
    public List<Contenido> getContenidos() { return listaContenidos; }
}
