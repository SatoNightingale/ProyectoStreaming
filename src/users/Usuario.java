package users;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import contenidos.Contenido;
import contenidos.Etiqueta;
import contenidos.ListaEtiquetas;

public class Usuario implements Serializable{
    protected String nombre;
    protected String password;
    protected Integer id;
    protected List<Creador> suscripciones; //List //ListProperty<Creador>
    protected ListaEtiquetas preferencias;

    public Usuario(int id, String nombre, String password){
        this.id = id;
        this.nombre = nombre;
        this.password = password;
        this.suscripciones =  new ArrayList<>();
        this.preferencias = new ListaEtiquetas();
    }

    public void listarSuscripciones(){
        System.out.println("Suscripciones de " + nombre + ":");

        for (Creador cr : suscripciones)
            System.out.println(cr.getNombre() + "(" + cr + ")");
    }

    /**
     * Calcula la afinidad de este usuario con un contenido, según una serie de criterios:<br>
     * 1. <b>Cantidad de etiquetas que comparte con el usuario</b>: se le asignan 10 puntos por cada etiqueta que comparta <br>
     * 2. <b>Peso total de las etiquetas del usuario que están en el contenido</b>: se toman del usuario todas las etiquetas que este contenido comparte, y se añade a la puntuación su valor multiplicado por 4 <br>
     * 3. <b>Peso total de las etiquetas del contenido que comparte con el usuario</b>: similar al anterior, solo que se suman los pesos de las etiquetas del contenido, no las del usuario, y se añade esta suma sin multiplicar a la puntuación <br>
     * 
     * @param contenido El contenido del que se va a calcular la afinidad con el usuario
     * @return Un valor double que representa la afinidad
     */
    public double afinidadUsuarioContenido(Contenido contenido){
        List<Etiqueta> preferences = preferencias.getList();

        // Crea una lista con todas las etiquetas que este contenido comparte con el usuario
        List<Etiqueta> matches = contenido.getTags().getList().stream()
            .filter(etiqueta -> preferences.contains(etiqueta))
            .toList();
        
        double pesoTotalUsuario = preferences.stream().filter(
            etiqueta -> matches.contains(etiqueta) // reúne todas las etiquetas del usuario que este contenido posee
        ).collect(Collectors.summingDouble(Etiqueta::getPeso)); // y suma sus pesos

        // Suma los pesos de todas las etiquetas que este contenido comparte con el usuario
        double pesoTotalContenido = matches.stream().collect(
            Collectors.summingDouble(Etiqueta::getPeso));

        return matches.size() * 4 + pesoTotalUsuario * 3 + pesoTotalContenido;
    }

    public boolean suscritoACreador(Creador c){ return suscripciones.contains(c); }

    // public String toString(){ return getNombre(); }

    public String getNombre(){ return nombre; }
    public String getPassword(){ return password; }
    public int getId(){ return id; }
    public ListaEtiquetas getPreferencias() { return preferencias; }
    public List<Creador> getSuscripciones() { return suscripciones; }
}
