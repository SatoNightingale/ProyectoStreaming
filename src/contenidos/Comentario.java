package contenidos;

import java.io.Serializable;

import users.Usuario;

public class Comentario implements Serializable{
    protected Usuario autor;
    protected String texto;

    public Comentario(Usuario autor, String texto){
        this.autor = autor;
        this.texto = texto;
    }

    public Usuario getAutor(){ return autor; }
    public String getTexto(){ return texto; }
}
