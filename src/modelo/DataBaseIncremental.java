package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * La idea de una base de datos incremental es que cada elemento pueda ser accedido por su índice, que este sea único para cada elemento y no cambie mientras exista en la base de datos, y que cada vez que se añade un elemento aumente el índice del próximo que se va a añadir. Piensa en ello como una lista: el primer elemento tiene índice 0, el segundo tiene índice 1, y así sucesivamente. Para cada elemento que se añade, el índice aumenta, y de ese modo el siguiente elemento a añadir tendrá un índice mayor que el anterior. Sin embargo, en el momento en que debemos eliminar un elemento de la lista, surgen los problemas. Por ejemplo, si eliminamos el elemento 3, el que antes tenía el índice 4 ahora tiene el 3, el número 5 pasa a ser el 4, y así sucesivamente. Entonces, no podríamos asignarle a cada elemento un número al añadirlo y esperar que ese número siempre nos sirva para marcar su posición en el arreglo. Para resolver esto, lo que hacemos es emplear un índice incremental (indiceInc) que va a tomar siempre la posición donde debemos colocar el siguiente elemento. Así, en una lista vacía este índice sería cero, pero iría aumentando a medida que se añaden elementos, de forma que siempre es igual al tamaño (size) de la lista. Sin embargo, si eliminamos un elemento, por ejemplo, en la posición 3, el índice incremental apuntaría a esta posición. De esa forma, el próximo elemento que añadamos estará en la posición 3, y luego el índice se colocaría en el siguiente espacio vacío (o bien al final de la lista). Esta solución es similar a lo que hace un procesador para administrar la memoria paginada. Garantiza que mientras un elemento exista en la base de datos, su posición en el arreglo siempre será la misma.
 * 
 * El tipo genérico T de la lista es para poder diferenciar, en las clases heredadas, el tipo de objeto que implementan; por ejemplo, si es una lista de Contenido o una lista de Usuario. En realidad los métodos para manipular la lista son los mismos independientemente de qué tipo de objeto manejemos
 */

public abstract class DataBaseIncremental<T> implements Serializable{
    protected List<T> listaElementos;

    protected int indiceInc;

    protected DataBaseIncremental(){
        listaElementos = new ArrayList<>();
        indiceInc = 0;
    }

    /**
     * Como esta clase está diseñada para ser heredada, y como cada elemento de las clases heredadas tiene un tipo distinto de construirse y requiere distintos parámetros, pero todos se añaden a la lista de la misma manera, definimos aquí un método abstracto para construir el elemento a añadir que será llamado cada vez que queremos añadir el elemento. Debe pasársele el id que va a ocupar en la lista, y en args (un arreglo de cualquier cantidad de objetos que pueden ser de cualquier tipo) los parámetros del constructor
     */
    protected abstract T registrarElemento(int id, Object...args); // throws Exception

    /**
     * El algoritmo para añadir un elemento está explicado en gran parte en la descripción de la clase. Ante todo, se construye el objeto a añadir con el método propio de cada clase, y se coloca en la posición que marque el índice incremental. Si el índice es mayor o igual que el tamaño (size) de la lista, se utiliza el método add, ya que esta posición aún no existe (es al final del arreglo); de lo contrario se utiliza set para colocarlo en una posición específica. Luego se recorre la lista para volver a situar el índice incremental en la posición del próximo hueco o al final de la lista
     * @param args Los parámetros necesarios para construir el nuevo elemento, según el método de registro. Se le pasan directamente a este
     * @return El elemento recién añadido
     */
    protected T addElemento(Object...args){ // throws Exception 
        T nuevoElemento = registrarElemento(indiceInc, args);

        if(indiceInc >= listaElementos.size())
            listaElementos.add(nuevoElemento);
        else
            listaElementos.set(indiceInc, nuevoElemento);

        int i = indiceInc;

        while (i < listaElementos.size() && listaElementos.get(i) != null) i++;

        indiceInc = i;

        // if(i == indiceInc) indiceInc++;

        return nuevoElemento;
    }

    /**
     * Para eliminar un elemento, simplemente se coloca un null en la posición de este. Como el índice incremental siempre tiene que apuntar al primer hueco del arreglo, es decir, a la menor posición donde haya un null (porque de lo contrario dejaría huecos vacíos cada vez que se añade un elemento, recuerda que él siempre busca la posición del próximo hueco *hacia adelante*) entonces se fija a la dirección del elemento recién eliminado solo si está antes que el hueco que él marcaba hasta el momento. Además, para optimizar un poco la memoria, le decimos que si eliminó el último elemento de la lista, no solo ponga un null, sino que llame al método remove para acortar la lista efectivamente
     * @param id La posición del elemento a eliminar
     */
    protected void eliminarElemento(int id){
        listaElementos.set(id, null);

        if(id < indiceInc) indiceInc = id;

        if(id == listaElementos.size()-1) listaElementos.remove(id);
    }

    /**
     * Simplemente devuelve el elemento en la posición id
     * @param id
     * @return
     */
    protected T getElemento(int id){ return listaElementos.get(id); }
}
