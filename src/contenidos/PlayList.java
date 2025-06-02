package contenidos;

import java.util.List;

public class PlayList {
    private List<Contenido> listaContenidos;
    // private MediaPlayer mediaPlayer;
    private int actual;

    public PlayList(List<Contenido> contenidos){ //, MediaPlayer mediaPlayer
        this.listaContenidos = contenidos;
        // this.mediaPlayer = mediaPlayer;
        actual = 0;
    }

    public Contenido contenidoActual(){
        return listaContenidos.get(actual);
    }

    public Contenido siguiente(){
        actual++;
        return listaContenidos.get(actual);
    }

    public Contenido anterior(){
        actual--;
        return listaContenidos.get(actual);
    }

    public Contenido reload(){
        actual = 0;
        return listaContenidos.get(actual);
    }

    public Contenido getLast(){
        actual = listaContenidos.size() - 1;
        return listaContenidos.get(actual);
    }

    /**
     * @return True si este es el último elemento de la playlist, false de lo contrario
     */
    public boolean finPlaylist(){
        return actual >= listaContenidos.size() - 1;
    }

    public boolean inicioPlayList(){
        return actual == 0;
    }
}
