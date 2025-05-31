package contenidos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListaEtiquetas implements Serializable{
    List<Etiqueta> etiquetas;

    public ListaEtiquetas(List<Etiqueta> etiquetas){
        this.etiquetas = new ArrayList<>();

        addEtiquetas(etiquetas);
    }

    public ListaEtiquetas(Etiqueta...etiquetas){
        this.etiquetas = new ArrayList<>();

        addEtiquetas(Arrays.asList(etiquetas));
    }

    public void addEtiquetas(List<Etiqueta> etiquetas){
        for (Etiqueta et : etiquetas) {
            actualizarEtiqueta(et);
        }
    }

    public void actualizarEtiqueta(String nombre, double peso){
        Etiqueta etiqueta = new Etiqueta(nombre, peso);
        actualizarEtiqueta(etiqueta);
    }

    public void actualizarEtiqueta(Etiqueta etiqueta){
        if(etiquetas.contains(etiqueta)){ // Si la encuentra, solo actualizar su peso
            Etiqueta ee = etiquetas.get(etiquetas.indexOf(etiqueta));
            // ee.setPeso(etiqueta.getPeso());
            ee.setPeso(ee.getPeso() + etiqueta.getPeso());
        } else { // Si no la encuentra, añádesela
            etiquetas.add(etiqueta);
        }
    }

    public Etiqueta getEtiqueta(String nombre){
        Etiqueta ret = null;
        int i = 0;

        while(ret == null && i < etiquetas.size()){
            if(etiquetas.get(i).getNombre().equals(nombre))
                ret = etiquetas.get(i);
        }

        return ret;
    }

    public List<Etiqueta> getList(){ return etiquetas; }
}
