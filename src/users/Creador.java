package users;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import contenidos.Contenido;
import contenidos.Etiqueta;
import contenidos.ListaEtiquetas;

public class Creador extends Usuario{
    private List<Contenido> contenidosSubidos;
    private List<Usuario> suscriptores;
    private ListaEtiquetas temas;

    public Creador(int id, String nombre, String password){
        super(id, nombre, password);
        this.suscriptores = new ArrayList<>();
        this.contenidosSubidos = new ArrayList<>();
        this.temas = new ListaEtiquetas();
    }

    public void listarSuscriptores(){
        System.out.println("Suscriptores de " + nombre + ":");
        
        for(Usuario user : suscriptores)
            System.out.println(user.getNombre() + "(" + user + ")");
    }

    public double popularidadGeneral(){
        double popularidad = suscripciones.size() * 2 + contenidosSubidos.size();

        if(!contenidosSubidos.isEmpty()){
            // Relacion likes / cantidad de contenidos
            popularidad += 4 * contenidosSubidos.stream().collect(
                Collectors.summingInt(Contenido::getLikes)) / contenidosSubidos.size();

            // Retención de atención: promedio del tiempo de reproducción de todos los contenidos
            popularidad += 10 * contenidosSubidos.stream().collect(
                Collectors.summingDouble(Contenido::promedioTiempoReproducido)) / contenidosSubidos.size();
        }

        return popularidad;
    }

    /**
     * Devuelve la afinidad entre los temas de este creador y las preferencias del usuario. Para esto se toman en cuenta los siguientes criterios, en orden de importancia: <br><br>
     * 1. Cantidad de etiquetas que ambos comparten<br>
     * 2. Peso total de las preferencias del usuario que son temas de este creador<br>
     * 3. Peso total de los temas de este creador que son preferencias del usuario<br>
     * @param usuario El usuario cuya afinidad con este creador se va a calcular
     * @return
     */
    public double afinidadCreadorUsuario(Usuario usuario){
        List<Etiqueta> userPreferences = preferencias.getList();

        // Crea una lista con todas las etiquetas que este creador comparte con el usuario
        List<Etiqueta> matches = getTemas().getList().stream()
            .filter(etiqueta -> userPreferences.contains(etiqueta))
            .toList();
        
        double pesoTotalUsuario = userPreferences.stream().filter(
            etiqueta -> matches.contains(etiqueta) // reúne todas las etiquetas del usuario que este creador posee
        ).collect(Collectors.summingDouble(Etiqueta::getPeso)); // y suma sus pesos

        // Suma los pesos de todas las etiquetas que este creador comparte con el usuario
        double pesoTotalCreador = matches.stream().collect(
            Collectors.summingDouble(Etiqueta::getPeso));

        return matches.size() * 4 + pesoTotalUsuario * 3 + pesoTotalCreador;
    }

    public List<Contenido> getContenidosSubidos() { return contenidosSubidos; }

    public List<Usuario> getSuscriptores() { return suscriptores; }

    public ListaEtiquetas getTemas() { return temas; }
}
