package co.edu.uniamazonia.amazoniahosting.mundo;

import java.time.LocalDate;
import java.util.ArrayList;

public class Publicacion {

    private String idPublicacion;
    private String tituloPublicacion;
    private LocalDate fechaPublicacion;
    private boolean estadoPublicacion;
    private Propiedad propiedad;
    private ArrayList<Resena> resenas;
    private ArrayList<Reservacion> reservaciones;

    public Publicacion(String idPublicacion, String tituloPublicacion, LocalDate fechaPublicacion,
            boolean estadoPublicacion) {
        if (idPublicacion == null || idPublicacion.trim().isEmpty()) {
            throw new IllegalArgumentException("El id de la publicacion no puede estar vacio.");
        }
        if (fechaPublicacion == null) {
            throw new IllegalArgumentException("La fecha de la publicacion es obligatoria.");
        }
        this.idPublicacion = idPublicacion.trim();
        this.tituloPublicacion = exigirTitulo(tituloPublicacion);
        this.fechaPublicacion = fechaPublicacion;
        this.estadoPublicacion = estadoPublicacion;
        this.propiedad = null;
        this.resenas = new ArrayList<>();
        this.reservaciones = new ArrayList<>();
    }

    public Publicacion(String idPublicacion, String tituloPublicacion, LocalDate fechaPublicacion,
            boolean estadoPublicacion, Propiedad propiedad) {
        this(idPublicacion, tituloPublicacion, fechaPublicacion, estadoPublicacion);
        this.propiedad = propiedad;
    }

    public String consultarPublicacion() {
        return "ID: " + idPublicacion
                + " | Titulo: " + tituloPublicacion
                + " | Fecha: " + fechaPublicacion
                + " | Estado: " + (estadoPublicacion ? "Activa" : "Inactiva")
                + " | Propiedad: " + (propiedad == null ? "sin propiedad asociada" : propiedad.consultarPropiedad());
    }

    public void modificarPublicacion(String nuevoTitulo) {
        this.tituloPublicacion = exigirTitulo(nuevoTitulo);
    }

    public void cambiarEstado() {
        this.estadoPublicacion = !this.estadoPublicacion;
    }

    /** True si el rango [inicio, fin) se cruza con una reservacion no cancelada (excepto 'excluir'). */
    public boolean hayCruce(LocalDate inicio, LocalDate fin, Reservacion excluir) {
        for (Reservacion r : reservaciones) {
            if (r == excluir || r.getEstado() == EstadoReservacion.CANCELADA) {
                continue;
            }
            if (inicio.isBefore(r.getFechaFin()) && fin.isAfter(r.getFechaInicio())) {
                return true;
            }
        }
        return false;
    }

    public boolean tieneReservacionesPendientes() {
        for (Reservacion r : reservaciones) {
            if (r.getEstado() == EstadoReservacion.PENDIENTE) {
                return true;
            }
        }
        return false;
    }

    public void agregarReservacion(Reservacion r) {
        if (r != null && !reservaciones.contains(r)) {
            reservaciones.add(r);
        }
    }

    public void quitarReservacion(Reservacion r) {
        reservaciones.remove(r);
    }

    public void agregarResena(Resena r) {
        if (r != null && !resenas.contains(r)) {
            resenas.add(r);
        }
    }

    private static String exigirTitulo(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El titulo no puede estar vacio.");
        }
        return valor.trim();
    }

    public String getIdPublicacion() {
        return idPublicacion;
    }

    public String getTituloPublicacion() {
        return tituloPublicacion;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public boolean isEstadoPublicacion() {
        return estadoPublicacion;
    }

    public Propiedad getPropiedad() {
        return propiedad;
    }

    public ArrayList<Resena> getResenas() {
        return new ArrayList<>(resenas);
    }

    public ArrayList<Reservacion> getReservaciones() {
        return new ArrayList<>(reservaciones);
    }

    public void setIdPublicacion(String idPublicacion) {
        if (idPublicacion == null || idPublicacion.trim().isEmpty()) {
            throw new IllegalArgumentException("El id de la publicacion no puede estar vacio.");
        }
        this.idPublicacion = idPublicacion.trim();
    }

    public void setTituloPublicacion(String tituloPublicacion) {
        this.tituloPublicacion = exigirTitulo(tituloPublicacion);
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        if (fechaPublicacion == null) {
            throw new IllegalArgumentException("La fecha de la publicacion es obligatoria.");
        }
        this.fechaPublicacion = fechaPublicacion;
    }

    public void setEstadoPublicacion(boolean estadoPublicacion) {
        this.estadoPublicacion = estadoPublicacion;
    }

    public void setPropiedad(Propiedad propiedad) {
        this.propiedad = propiedad;
    }
}
