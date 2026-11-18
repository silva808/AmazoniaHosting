package co.edu.uniamazonia.amazoniahosting.mundo;

public class Publicacion {
    public static int cont = 1;
    
    //Declaracion de Atributos
    private String idPublicacion;
    private String tituloPublicacion;
    private String descripcionPublicacion;
    private String fechaPublicacion;
    private boolean estadoPublicacion;
    private Propiedad propiedad;

    //Metodos Constructores
    public Publicacion(String tituloPublicacion, String descripcionPublicacion, String fechaPublicacion, boolean estadoPublicacion, Propiedad propiedad) {
        this.idPublicacion = "PUB-" + cont++;
        this.tituloPublicacion = tituloPublicacion;
        this.descripcionPublicacion = descripcionPublicacion;
        this.fechaPublicacion = fechaPublicacion;
        this.estadoPublicacion = estadoPublicacion;
        this.propiedad = propiedad;
    }

    public Publicacion() {
        this.idPublicacion = "PUB-" + cont++;
        this.tituloPublicacion = "Sin título";
        this.descripcionPublicacion = "Sin descripción";
        this.fechaPublicacion = "Sin fecha";
        this.estadoPublicacion = true;
        this.propiedad = null;
    }        
    
    //Metodos de Negocio
    public void consultarPublicacion() {
        System.out.println("ID:          " + this.idPublicacion);
        System.out.println("Título:      " + this.tituloPublicacion);
        System.out.println("Descripción: " + this.descripcionPublicacion);
        System.out.println("Fecha:       " + this.fechaPublicacion);
        System.out.println("Estado:      " + (this.estadoPublicacion ? "Activa" : "Inactiva"));
        if (this.propiedad != null) {
            this.propiedad.consultarPropiedad();
        } else {
            System.out.println("Sin propiedad asociada.");
        }
    }
    
    public void actualizarInformacion(String nuevoTitulo, String nuevaDescripcion) {
        this.tituloPublicacion = nuevoTitulo;
        this.descripcionPublicacion = nuevaDescripcion;
        System.out.println("¡Publicación " + this.idPublicacion + " actualizada con éxito!");
    }
    
    public void cambiarEstado() {
        this.estadoPublicacion = !this.estadoPublicacion;
        System.out.println("Estado de la publicación " + this.idPublicacion + " cambiado a: " 
                            + (this.estadoPublicacion ? "Activa" : "Inactiva"));
    }

    //Declaracion de Metodos Getters y Setters
    public String getIdPublicacion() {
        return idPublicacion;
    }

    public String getTituloPublicacion() {
        return tituloPublicacion;
    }

    public String getDescripcionPublicacion() {
        return descripcionPublicacion;
    }

    public String getFechaPublicacion() {
        return fechaPublicacion;
    }

    public boolean isEstadoPublicacion() {
        return estadoPublicacion;
    }
    
    public Propiedad getPropiedad() {
        return propiedad;
    }

    public void setIdPublicacion(String idPublicacionb) {
        this.idPublicacion = idPublicacionb;
    }

    public void setTituloPublicacion(String tituloPublicacion) {
        this.tituloPublicacion = tituloPublicacion;
    }

    public void setDescripcionPublicacion(String descripcionPublicacion) {
        this.descripcionPublicacion = descripcionPublicacion;
    }

    public void setFechaPublicacion(String fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public void setEstadoPublicacion(boolean estadoPublicacion) {
        this.estadoPublicacion = estadoPublicacion;
    }

    public void setPropiedad(Propiedad propiedad) {
        this.propiedad = propiedad;
    }
    
}