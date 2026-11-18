package co.edu.uniamazonia.amazoniahosting.mundo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reservacion {

    private String idReservacion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int cantidadHuesped;
    private double costoTotal;
    private EstadoReservacion estado;
    private Publicacion publicacion;

    public Reservacion(String idReservacion, LocalDate fechaInicio, LocalDate fechaFin,
            int cantidadHuesped, double costoTotal) {
        if (idReservacion == null || idReservacion.trim().isEmpty()) {
            throw new IllegalArgumentException("El id de la reservacion no puede estar vacio.");
        }
        validarRango(fechaInicio, fechaFin);
        validarCantidad(cantidadHuesped);
        if (costoTotal < 0) {
            throw new IllegalArgumentException("El costo total no puede ser negativo.");
        }
        this.idReservacion = idReservacion.trim();
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cantidadHuesped = cantidadHuesped;
        this.costoTotal = costoTotal;
        this.estado = EstadoReservacion.PENDIENTE;
    }

    public long calcularCantidadNoche() {
        return ChronoUnit.DAYS.between(fechaInicio, fechaFin);
    }

    /** RF-08: costo = noches x precio de la propiedad de la publicacion. */
    public double calcularCostoTotal() {
        if (publicacion == null || publicacion.getPropiedad() == null) {
            throw new IllegalStateException("La reservacion no tiene una publicacion con propiedad asociada.");
        }
        this.costoTotal = (double) calcularCantidadNoche() * publicacion.getPropiedad().getPrecio();
        return costoTotal;
    }

    public void confirmar() {
        if (estado != EstadoReservacion.PENDIENTE) {
            throw new IllegalStateException("Solo se puede confirmar una reservacion PENDIENTE.");
        }
        this.estado = EstadoReservacion.COMPLETADA;
    }

    public void cancelar() {
        if (estado != EstadoReservacion.PENDIENTE) {
            throw new IllegalStateException("Solo se puede cancelar una reservacion PENDIENTE.");
        }
        this.estado = EstadoReservacion.CANCELADA;
    }

    /** Cambia fechas y cantidad validando todo antes de modificar el estado interno. */
    public void actualizarDatos(LocalDate nuevaInicio, LocalDate nuevaFin, int nuevaCantidad) {
        validarRango(nuevaInicio, nuevaFin);
        validarCantidad(nuevaCantidad);
        this.fechaInicio = nuevaInicio;
        this.fechaFin = nuevaFin;
        this.cantidadHuesped = nuevaCantidad;
    }

    private static void validarRango(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null) {
            throw new IllegalArgumentException("Las fechas son obligatorias.");
        }
        if (!fin.isAfter(inicio)) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la de inicio.");
        }
    }

    private static void validarCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de huespedes debe ser mayor que cero.");
        }
    }

    public String getIdReservacion() {
        return idReservacion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public int getCantidadHuesped() {
        return cantidadHuesped;
    }

    public double getCostoTotal() {
        return costoTotal;
    }

    public EstadoReservacion getEstado() {
        return estado;
    }

    public Publicacion getPublicacion() {
        return publicacion;
    }

    public void setPublicacion(Publicacion publicacion) {
        this.publicacion = publicacion;
    }
}
