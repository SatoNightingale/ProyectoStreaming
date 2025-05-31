package contenidos;

import java.io.Serializable;

/**
 * Etiqueta que se añade como dato al Contenido. Cada etiqueta, dependiendo de donde esté, es distinta porque tiene un valor de peso distinto, pero si el nombre es el mismo se toma como del mismo tipo en una ListaEtiquetas
 */
public class Etiqueta implements Serializable, Comparable<Etiqueta>{
    private String nombre;                // El nombre de esta etiqueta, que comparte con todas las etiquetas del mismo tipo
    private double peso;                  // El peso de esta etiqueta para un usuario o contenido específico

    public Etiqueta(String nombre, double peso){
        this.nombre = nombre;
        this.peso = peso;
    }

    public Etiqueta(Etiqueta otraEtiqueta, double nuevoPeso){
        this.nombre = otraEtiqueta.nombre;
        this.peso = nuevoPeso;
    }

    public String getNombre(){ return nombre; }

    public double getPeso(){ return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    /**Para que dos etiquetas sean iguales, solo deben tener el mismo nombre
     */
    @Override
    public boolean equals(Object o){
        return o instanceof Etiqueta && ((Etiqueta) o).getNombre().equals(nombre);
    }

    @Override
    public int compareTo(Etiqueta et) {
        return Double.compare(peso, et.getPeso());
    }
}
