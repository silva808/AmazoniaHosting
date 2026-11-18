package co.edu.uniamazonia.amazoniahosting.mundo;

public class Propiedad {

    private static int contadorPropiedad = 1;
    private String idPropiedad;
    private String ubicacionPropiedad;
    private String descripcionPropiedad;
    private TipoPropiedad tipoPropiedad;
    private Usuario propietario;
    private Disponibilidad disponibilidad;

    public Propiedad(String ubicacionPropiedad, String descripcionPropiedad, TipoPropiedad tipoPropiedad, Usuario propietario, Disponibilidad disponibilidad) {
        this.idPropiedad = "PROP-" + contadorPropiedad++;
        this.ubicacionPropiedad = ubicacionPropiedad;
        this.descripcionPropiedad = descripcionPropiedad;
        this.tipoPropiedad = tipoPropiedad;
        this.propietario = propietario;
        this.disponibilidad = disponibilidad;
    }

    public Propiedad() {
        this.idPropiedad = "PROP-" + contadorPropiedad++;
        this.ubicacionPropiedad = "";
        this.descripcionPropiedad = "";
        this.tipoPropiedad = null;
        this.propietario = null;
        this.disponibilidad = null;
    }

    //Metodos de Negocio
    public void consultarPropiedad() {
        System.out.println("ID Propiedad:   " + this.idPropiedad);
        System.out.println("Ubicación:      " + this.ubicacionPropiedad);
        System.out.println("Descripción:    " + this.descripcionPropiedad);
        System.out.println("Tipo:           " + this.tipoPropiedad);
        System.out.println("Propietario:    " + propietario.getNombreUsuario());
    }

    public void actualizarDatos(String nuevaUbicacion, String nuevaDescripcion) {
        this.ubicacionPropiedad = nuevaUbicacion;
        this.descripcionPropiedad = nuevaDescripcion;
        System.out.println("¡Propiedad " + this.idPropiedad + " actualizada con éxito!");
    }

    //Declaracion de Metodos Getters y Setters
    public String getIdPropiedad() {
        return idPropiedad;
    }

    public String getUbicacionPropiedad() {
        return ubicacionPropiedad;
    }

    public String getDescripcionPropiedad() {
        return descripcionPropiedad;
    }

    public TipoPropiedad getTipoPropiedad() {
        return tipoPropiedad;
    }

    public Usuario getPropietario() {
        return propietario;
    }

    public Disponibilidad getDisponibilidad() {
        return disponibilidad;
    }

    public void setIdPropiedad(String idPropiedad) {
        this.idPropiedad = idPropiedad;
    }

    public void setUbicacionPropiedad(String ubicacionPropiedad) {
        this.ubicacionPropiedad = ubicacionPropiedad;
    }

    public void setDescripcionPropiedad(String descripcionPropiedad) {
        this.descripcionPropiedad = descripcionPropiedad;
    }

    public void setTipoPropiedad(TipoPropiedad tipoPropiedad) {
        this.tipoPropiedad = tipoPropiedad;
    }

    public void setPropietario(Usuario propietario) {
        this.propietario = propietario;
    }

    public void setDisponibilidad(Disponibilidad disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

}
