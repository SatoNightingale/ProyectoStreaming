package controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public abstract class DataBaseIncremental<T> implements Serializable{
    // Originalmente iba a hacer esto con ObservableMap, pero es que es una candela tan grande y con tantas complicaciones que ni yo mismo la entiendo muy bien, y no es como para ponértelo a ti. Decidí hacer todo lo más sencillo posible

    // protected transient ObservableMap<Integer, T> mapaElementos;
    protected Map<Integer, T> mapaElementos;

    // private static final long serialVersionUID = 42L;

    protected int indiceInc;

    protected DataBaseIncremental(){
        mapaElementos = new HashMap<>();
        indiceInc = 0;
    }

    protected abstract T registrarElemento(int id, Object...args); // throws Exception

    protected T addElemento(Object...args){ // throws Exception 
        // El índice es incremental, por eso rellenamos los lugares vacíos que puedan quedar tras alguna eliminación
        int i = 0;
        while (i < indiceInc && mapaElementos.containsKey(i)) i++;

        if(i == indiceInc) indiceInc++;

        T nuevoElemento = registrarElemento(i, args);

        mapaElementos.put(i, nuevoElemento);

        return nuevoElemento;
    }

    protected void eliminarElemento(int id){
        mapaElementos.remove(id);
        if(id == indiceInc) indiceInc--;
    }

    // private void writeObject(ObjectOutputStream out) throws IOException{
    //     out.defaultWriteObject();
    //     Map<Integer, T> mapaBackup = new HashMap<>(mapaElementos);
    //     out.writeObject(mapaBackup);
    //     // writeAdditionalData(out);
    // }

    // private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException{
    //     in.defaultReadObject();
    //     @SuppressWarnings("unchecked")
    //     Map<Integer, T> mapaBackup = (Map<Integer, T>) in.readObject();
    //     mapaElementos = FXCollections.observableMap(mapaBackup);
    //     // readAdditionalData(in);
    // }

    // protected void writeAdditionalData(ObjectOutputStream out) throws IOException{}

    // protected void readAdditionalData(ObjectInputStream in) throws IOException{}

    protected T getElemento(int id){ return mapaElementos.get(id); }

    // public ObservableMap<Integer, T> mapaElementosProperty(){ return mapaElementos; }
}
