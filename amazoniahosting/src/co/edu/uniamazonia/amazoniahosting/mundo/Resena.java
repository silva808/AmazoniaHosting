package co.edu.uniamazonia.amazoniahosting.mundo;

import java.time.LocalDate;

public class Resena {

    public static final double CALIFICACION_MINIMA = 1.0;
    public static final double CALIFICACION_MAXIMA = 5.0;

    private String idResena;
    private double calificacion;
    private String comentario;
    private LocalDate fechaResena;
    private Publicacion publicacion;

    public Resena(String idResena, double calificacion, String comentario, LocalDate fechaResena) {
        if (idResena == null || idResena.trim().isEmpty()) {
            throw new IllegalArgumentException("El id de la resena no puede estar vacio.");
        }
        if (fechaResena == null) {
            throw new IllegalArgumentException("La fecha de la resena es obligatoria.");
        }
        this.idResena = idResena.trim();
        this.calificacion = exigirCalificacion(calificacion);
        this.comentario = exigirComentario(comentario);
        this.fechaResena = fechaResena;
    }

    public void editarResena(double nuevaCalificacion, String nuevoComentario) {
        double cal = exigirCalificacion(nuevaCalificacion);
        String com = exigirComentario(nuevoComentario);
        this.calificacion = cal;
        this.comentario = com;
    }

    private static double exigirCalificacion(double valor) {
        if (Double.isNaN(valor) || valor < CALIFICACION_MINIMA || valor > CALIFICACION_MAXIMA) {
            throw new IllegalArgumentException("La calificacion debe estar entre 1 y 5.");
        }
        return valor;
    }

    private static String exigirComentario(String valor) {
        if (valor == null) {
            throw new IllegalArgumentException("El comentario no puede ser nulo.");
        }
        return valor.trim();
    }

    public String getIdResena() {
        return idResena;
    }

    public double getCalificacion() {
        return calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public LocalDate getFechaResena() {
        return fechaResena;
    }

    public Publicacion getPublicacion() {
        return publicacion;
    }

    public void setPublicacion(Publicacion publicacion) {
        this.publicacion = publicacion;
    }
}
