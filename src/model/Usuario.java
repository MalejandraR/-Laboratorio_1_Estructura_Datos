package model;

/**
 * Modelo POJO que representa un usuario del sistema.
 * La cedula (CC) es la clave principal (indice) de cada registro.
 */
public class Usuario {

    private String nombre;
    private long cedula;
    private String correo;

    public Usuario(String nombre, long cedula, String correo) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long getCedula() {
        return cedula;
    }

    public void setCedula(long cedula) {
        this.cedula = cedula;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    @Override
    public String toString() {
        return "Nombre: " + nombre + "\nCC: " + cedula + "\nCorreo: " + correo;
    }
}
