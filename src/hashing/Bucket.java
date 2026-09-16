package hashing;

import model.Usuario;

import java.util.ArrayList;
import java.util.List;

/**
 * Bucket del Extendible Hashing.
 * Cada bucket tiene una capacidad maxima (B), una lista interna de usuarios
 * y una profundidad local que indica cuantos bits del hash comparten
 * los registros que contiene.
 *
 * Cuando el bucket se llena y causa overflow, se realiza un split:
 * - Se incrementa la profundidad local.
 * - Se redistribuyen TODOS los registros usando el bit adicional.
 */
public class Bucket {

    /** Capacidad maxima de registros por bucket */
    private final int capacidadMaxima;

    /** Profundidad local: cuantos bits del hash caracterizan a este bucket */
    private int profundidadLocal;

    /** Lista interna de usuarios almacenados en este bucket */
    private final List<Usuario> usuarios;

    public Bucket(int capacidadMaxima, int profundidadLocal) {
        this.capacidadMaxima = capacidadMaxima;
        this.profundidadLocal = profundidadLocal;
        this.usuarios = new ArrayList<>();
    }

    /**
     * Verifica si el bucket tiene espacio disponible.
     * @return true si hay espacio, false si esta lleno.
     */
    public boolean tieneEspacio() {
        return usuarios.size() < capacidadMaxima;
    }

    /**
     * Inserta un usuario en este bucket.
     * PRE: el bucket tiene espacio (verificar con tieneEspacio() antes).
     */
    public void insertar(Usuario usuario) {
        if (!tieneEspacio()) {
            throw new IllegalStateException("Bucket lleno. Se requiere split antes de insertar.");
        }
        usuarios.add(usuario);
    }

    /**
     * Retorna todos los usuarios almacenados en este bucket.
     * Se usa durante el split para redistribuir TODOS los registros.
     */
    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    /**
     * Elimina todos los usuarios del bucket.
     * Se usa durante el split despues de redistribuir los registros.
     */
    public void limpiar() {
        usuarios.clear();
    }

    public int getProfundidadLocal() {
        return profundidadLocal;
    }

    public void setProfundidadLocal(int profundidadLocal) {
        this.profundidadLocal = profundidadLocal;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public int getTamano() {
        return usuarios.size();
    }

    @Override
    public String toString() {
        return "Bucket{LD=" + profundidadLocal + ", tamano=" + usuarios.size()
                + ", capacidad=" + capacidadMaxima + "}";
    }
}
