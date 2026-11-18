package co.edu.uniamazonia.amazoniahosting.mundo;

public class Disponibilidad {
    //Declaracion de Atributos
    private int idDisponibilidad;
    private String tipoDisponibilidad;
    private long precio;
    private boolean estadoDisponibilidad;

    //Declaracion de Metodos Constructores
    public Disponibilidad(String tipoDisponibilidad, long precio, boolean estadoDisponibilidad) {
        this.tipoDisponibilidad = tipoDisponibilidad;
        this.precio = precio;
        this.estadoDisponibilidad = estadoDisponibilidad;
    }

    public Disponibilidad() {
        this.tipoDisponibilidad = "No Disponibilidad";
        this.precio = 0;
        this.estadoDisponibilidad = true;
    }

    //Metodos de Negocio
    public void consultarDisponibilidad() {
        System.out.println("Tipo de disponibilidad:      " + this.tipoDisponibilidad);
        System.out.println("precio:    " + this.precio);
        System.out.println("Disponibilidad:      " + (this.estadoDisponibilidad ? "Libre" : "Ocupado"));
    }
    
    public void actualizarDisponibilidad(String nuevoTipo, long nuevoPrecio){
        this.tipoDisponibilidad = nuevoTipo;
        this.precio = nuevoPrecio;
        System.out.println("¡Cambios realizados con éxito!");
    }
    
    //Declaracion de Metodos Getters y Setters
    public int getIdDisponibilidad() {
        return idDisponibilidad;
    }

    public String getTipoDisponibilidad() {
        return tipoDisponibilidad;
    }

    public long getPrecio() {
        return precio;
    }

    public boolean isEstadoDisponibilidad() {
        return estadoDisponibilidad;
    }

    public void setIdDisponibilidad(int idDisponibilidad) {
        this.idDisponibilidad = idDisponibilidad;
    }

    public void setTipoDisponibilidad(String tipoDisponibilidad) {
        this.tipoDisponibilidad = tipoDisponibilidad;
    }

    public void setPrecio(long precio) {
        this.precio = precio;
    }

    public void setEstadoDisponibilidad(boolean estadoDisponibilidad) {
        this.estadoDisponibilidad = estadoDisponibilidad;
    }
    
}
