package co.edu.uniamazonia.amazoniahosting.mundo;

public class Propiedad {

    private String idPropiedad;
    private String ubicacionPropiedad;
    private String descripcionPropiedad;
    private long precio;
    private boolean estadoDisponibilidad;
    private TipoPropiedad tipoPropiedad;

    public Propiedad(String idPropiedad, String ubicacionPropiedad, String descripcionPropiedad,
            long precio, boolean estadoDisponibilidad) {
        this(idPropiedad, ubicacionPropiedad, descripcionPropiedad, precio, estadoDisponibilidad, null);
    }

    public Propiedad(String idPropiedad, String ubicacionPropiedad, String descripcionPropiedad,
            long precio, boolean estadoDisponibilidad, TipoPropiedad tipoPropiedad) {
        this.idPropiedad = exigirTexto(idPropiedad, "El id de la propiedad");
        this.ubicacionPropiedad = exigirTexto(ubicacionPropiedad, "La ubicacion");
        this.descripcionPropiedad = exigirTexto(descripcionPropiedad, "La descripcion");
        this.precio = exigirPrecio(precio);
        this.estadoDisponibilidad = estadoDisponibilidad;
        this.tipoPropiedad = tipoPropiedad;
    }

    public String consultarPropiedad() {
        return "ID: " + idPropiedad
                + " | Tipo: " + (tipoPropiedad == null ? "SIN TIPO" : tipoPropiedad)
                + " | Ubicacion: " + ubicacionPropiedad
                + " | Descripcion: " + descripcionPropiedad
                + " | Precio: " + precio
                + " | " + (estadoDisponibilidad ? "Disponible" : "No disponible");
    }

    public void actualizarDatos(String nuevaUbicacion, String nuevaDescripcion) {
        String ubicacion = exigirTexto(nuevaUbicacion, "La ubicacion");
        String descripcion = exigirTexto(nuevaDescripcion, "La descripcion");
        this.ubicacionPropiedad = ubicacion;
        this.descripcionPropiedad = descripcion;
    }

    public boolean consultarDisponibilidad() {
        return estadoDisponibilidad;
    }

    /** RF-06: unica via para cambiar estado y precio, validando el precio. */
    public void actualizarDisponibilidad(boolean nuevoEstado, long nuevoPrecio) {
        this.precio = exigirPrecio(nuevoPrecio);
        this.estadoDisponibilidad = nuevoEstado;
    }

    private static String exigirTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " no puede estar vacio.");
        }
        return valor.trim();
    }

    private static long exigirPrecio(long valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero.");
        }
        return valor;
    }

    public String getIdPropiedad() {
        return idPropiedad;
    }

    public String getUbicacionPropiedad() {
        return ubicacionPropiedad;
    }

    public String getDescripcionPropiedad() {
        return descripcionPropiedad;
    }

    public long getPrecio() {
        return precio;
    }

    public boolean isEstadoDisponibilidad() {
        return estadoDisponibilidad;
    }

    public TipoPropiedad getTipoPropiedad() {
        return tipoPropiedad;
    }

    public void setIdPropiedad(String idPropiedad) {
        this.idPropiedad = exigirTexto(idPropiedad, "El id de la propiedad");
    }

    public void setUbicacionPropiedad(String ubicacionPropiedad) {
        this.ubicacionPropiedad = exigirTexto(ubicacionPropiedad, "La ubicacion");
    }

    public void setDescripcionPropiedad(String descripcionPropiedad) {
        this.descripcionPropiedad = exigirTexto(descripcionPropiedad, "La descripcion");
    }

    public void setPrecio(long precio) {
        this.precio = exigirPrecio(precio);
    }

    public void setEstadoDisponibilidad(boolean estadoDisponibilidad) {
        this.estadoDisponibilidad = estadoDisponibilidad;
    }

    public void setTipoPropiedad(TipoPropiedad tipoPropiedad) {
        this.tipoPropiedad = tipoPropiedad;
    }
}
