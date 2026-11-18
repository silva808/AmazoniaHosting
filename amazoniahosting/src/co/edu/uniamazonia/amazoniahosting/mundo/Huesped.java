package co.edu.uniamazonia.amazoniahosting.mundo;

import java.time.LocalDate;
import java.util.ArrayList;

public class Huesped extends Usuario {

    private ArrayList<Reservacion> reservaciones;
    private ArrayList<Resena> resenas;
    private int consecutivoReservas;
    private int consecutivoResenas;

    public Huesped() {
        super();
        iniciarColecciones();
    }

    public Huesped(String idUsuario, String nombreUsuario, String correoUsuario,
            String telefonoUsuario, String contrasena) {
        super(idUsuario, nombreUsuario, correoUsuario, telefonoUsuario, contrasena);
        iniciarColecciones();
    }

    private void iniciarColecciones() {
        this.reservaciones = new ArrayList<>();
        this.resenas = new ArrayList<>();
        this.consecutivoReservas = 0;
        this.consecutivoResenas = 0;
    }

    @Override
    public String getRol() {
        return "HUESPED";
    }

    /** RF-07: crea la reservacion PENDIENTE y la vincula a huesped y publicacion. */
    public Reservacion reservarAlojamiento(Publicacion pub, LocalDate fechaInicio, LocalDate fechaFin,
            int cantidadHuesped) {
        if (pub == null) {
            throw new IllegalArgumentException("La publicacion es obligatoria.");
        }
        if (!pub.isEstadoPublicacion()) {
            throw new IllegalStateException("La publicacion esta inactiva.");
        }
        validarDisponible(pub);
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas son obligatorias.");
        }
        if (fechaInicio.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de inicio no puede estar en el pasado.");
        }
        consecutivoReservas++;
        Reservacion nueva = new Reservacion("RES-" + getIdUsuario() + "-" + consecutivoReservas,
                fechaInicio, fechaFin, cantidadHuesped, 0);
        if (pub.hayCruce(fechaInicio, fechaFin, null)) {
            consecutivoReservas--;
            throw new IllegalStateException("La propiedad ya esta reservada en esas fechas.");
        }
        nueva.setPublicacion(pub);
        nueva.calcularCostoTotal();
        reservaciones.add(nueva);
        pub.agregarReservacion(nueva);
        return nueva;
    }

    /** RF-12: modifica fechas y cantidad revalidando disponibilidad y recalculando el costo. */
    public Reservacion modificarReserva(Reservacion reserva, LocalDate nuevaInicio, LocalDate nuevaFin,
            int nuevaCantidad) {
        if (reserva == null || !reservaciones.contains(reserva)) {
            throw new IllegalArgumentException("La reservacion no pertenece a este huesped.");
        }
        if (reserva.getEstado() != EstadoReservacion.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden modificar reservaciones PENDIENTES.");
        }
        Publicacion pub = reserva.getPublicacion();
        validarDisponible(pub);
        if (nuevaInicio == null || nuevaFin == null) {
            throw new IllegalArgumentException("Las fechas son obligatorias.");
        }
        if (nuevaInicio.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de inicio no puede estar en el pasado.");
        }
        if (nuevaFin.isAfter(nuevaInicio) && pub.hayCruce(nuevaInicio, nuevaFin, reserva)) {
            throw new IllegalStateException("La propiedad ya esta reservada en esas fechas.");
        }
        LocalDate inicioAnterior = reserva.getFechaInicio();
        LocalDate finAnterior = reserva.getFechaFin();
        int cantidadAnterior = reserva.getCantidadHuesped();
        reserva.actualizarDatos(nuevaInicio, nuevaFin, nuevaCantidad);
        try {
            reserva.calcularCostoTotal();
        } catch (RuntimeException e) {
            reserva.actualizarDatos(inicioAnterior, finAnterior, cantidadAnterior);
            throw e;
        }
        return reserva;
    }

    /** Elimina reservaciones PENDIENTES (las cancela) o CANCELADAS. Las COMPLETADAS se conservan. */
    public boolean eliminarReserva(String idReservacion) {
        Reservacion r = buscarReservacion(idReservacion);
        if (r == null || r.getEstado() == EstadoReservacion.COMPLETADA) {
            return false;
        }
        if (r.getEstado() == EstadoReservacion.PENDIENTE) {
            r.cancelar();
        }
        reservaciones.remove(r);
        if (r.getPublicacion() != null) {
            r.getPublicacion().quitarReservacion(r);
        }
        return true;
    }

    /** RF-11: solo sobre estadias COMPLETADAS propias; una resena por publicacion. */
    public Resena darResena(Reservacion reserva, double calificacion, String comentario) {
        if (reserva == null || !reservaciones.contains(reserva)) {
            throw new IllegalArgumentException("La reservacion no pertenece a este huesped.");
        }
        if (reserva.getEstado() != EstadoReservacion.COMPLETADA) {
            throw new IllegalStateException("Solo se puede resenar una estadia COMPLETADA.");
        }
        for (Resena existente : resenas) {
            if (existente.getPublicacion() == reserva.getPublicacion()) {
                throw new IllegalStateException("Ya existe una resena de este huesped para esa publicacion.");
            }
        }
        consecutivoResenas++;
        Resena nueva = new Resena("RESE-" + getIdUsuario() + "-" + consecutivoResenas,
                calificacion, comentario, LocalDate.now());
        nueva.setPublicacion(reserva.getPublicacion());
        resenas.add(nueva);
        reserva.getPublicacion().agregarResena(nueva);
        return nueva;
    }

    public Reservacion buscarReservacion(String idReservacion) {
        if (idReservacion == null) {
            return null;
        }
        for (Reservacion r : reservaciones) {
            if (r.getIdReservacion().equalsIgnoreCase(idReservacion.trim())) {
                return r;
            }
        }
        return null;
    }

    private static void validarDisponible(Publicacion pub) {
        if (pub == null || pub.getPropiedad() == null || !pub.getPropiedad().consultarDisponibilidad()) {
            throw new IllegalStateException("La propiedad no esta disponible.");
        }
    }

    public ArrayList<Reservacion> getReservaciones() {
        return new ArrayList<>(reservaciones);
    }

    public ArrayList<Resena> getResenas() {
        return new ArrayList<>(resenas);
    }
}
