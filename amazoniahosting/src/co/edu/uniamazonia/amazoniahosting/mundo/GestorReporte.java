package co.edu.uniamazonia.amazoniahosting.mundo;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Locale;

public class GestorReporte {

    private String rutaCarpeta;
    private String delimitadorCsv;
    private LocalDate fechaUltimoReporte;

    public GestorReporte(String rutaCarpeta, String delimitadorCsv, LocalDate fechaUltimoReporte) {
        if (rutaCarpeta == null || rutaCarpeta.trim().isEmpty()) {
            throw new IllegalArgumentException("La ruta de la carpeta no puede estar vacia.");
        }
        if (delimitadorCsv == null || delimitadorCsv.isEmpty()) {
            throw new IllegalArgumentException("El delimitador no puede estar vacio.");
        }
        this.rutaCarpeta = rutaCarpeta.trim();
        this.delimitadorCsv = delimitadorCsv;
        this.fechaUltimoReporte = fechaUltimoReporte;
    }

    /** Suma el costo de las reservaciones COMPLETADAS. Lista vacia o null retorna 0.0. */
    public double calcularTotalRecaudado(ArrayList<Reservacion> reservaciones) {
        double total = 0.0;
        if (reservaciones == null) {
            return total;
        }
        for (Reservacion r : reservaciones) {
            if (r != null && r.getEstado() == EstadoReservacion.COMPLETADA) {
                total += r.getCostoTotal();
            }
        }
        return total;
    }

    /** Promedio de calificaciones; lista vacia o null retorna 0.0 (sin dividir por cero). */
    public double calcularPromedioCalificaciones(ArrayList<Resena> resenas) {
        if (resenas == null || resenas.isEmpty()) {
            return 0.0;
        }
        double suma = 0.0;
        for (Resena r : resenas) {
            suma += r.getCalificacion();
        }
        return suma / resenas.size();
    }

    public EnumMap<EstadoReservacion, Integer> contarReservasPorEstado(ArrayList<Reservacion> reservaciones) {
        EnumMap<EstadoReservacion, Integer> conteo = new EnumMap<>(EstadoReservacion.class);
        for (EstadoReservacion e : EstadoReservacion.values()) {
            conteo.put(e, 0);
        }
        if (reservaciones != null) {
            for (Reservacion r : reservaciones) {
                if (r != null) {
                    conteo.put(r.getEstado(), conteo.get(r.getEstado()) + 1);
                }
            }
        }
        return conteo;
    }

    /** Publicacion con el menor promedio entre las que tienen resenas; null si ninguna tiene. */
    public Publicacion obtenerPublicacionMasCritica(ArrayList<Publicacion> publicaciones) {
        Publicacion critica = null;
        double menor = Double.MAX_VALUE;
        if (publicaciones == null) {
            return null;
        }
        for (Publicacion pub : publicaciones) {
            ArrayList<Resena> rs = pub.getResenas();
            if (rs.isEmpty()) {
                continue;
            }
            double promedio = calcularPromedioCalificaciones(rs);
            if (promedio < menor) {
                menor = promedio;
                critica = pub;
            }
        }
        return critica;
    }

    public String generarReportePropietario(Propietario propietario) {
        if (propietario == null) {
            throw new IllegalArgumentException("El propietario es obligatorio.");
        }
        ArrayList<Publicacion> pubs = propietario.getPublicaciones();
        ArrayList<Reservacion> reservas = new ArrayList<>();
        ArrayList<Resena> resenas = new ArrayList<>();
        for (Publicacion pub : pubs) {
            reservas.addAll(pub.getReservaciones());
            resenas.addAll(pub.getResenas());
        }
        Publicacion critica = obtenerPublicacionMasCritica(pubs);
        StringBuilder sb = new StringBuilder();
        sb.append("REPORTE DE PROPIETARIO - AMAZONIA HOSTING\n");
        sb.append("Propietario: ").append(propietario.getNombreUsuario())
                .append(" (").append(propietario.getIdUsuario()).append(")\n");
        sb.append("Fecha de generacion: ").append(LocalDate.now()).append("\n");
        sb.append("Publicaciones: ").append(pubs.size()).append("\n");
        sb.append(String.format(Locale.US, "Total recaudado (COMPLETADAS): %.2f%n", calcularTotalRecaudado(reservas)));
        sb.append(String.format(Locale.US, "Promedio de calificaciones: %.2f%n", calcularPromedioCalificaciones(resenas)));
        sb.append("Reservas por estado: ").append(contarReservasPorEstado(reservas)).append("\n");
        sb.append("Publicacion mas critica: ")
                .append(critica == null ? "ninguna (sin resenas)" : critica.getTituloPublicacion()).append("\n");
        sb.append("DETALLE\n");
        sb.append(String.join(delimitadorCsv, "idReservacion", "idPublicacion", "fechaInicio", "fechaFin",
                "estado", "costoTotal")).append("\n");
        for (Reservacion r : reservas) {
            sb.append(String.join(delimitadorCsv, r.getIdReservacion(), r.getPublicacion().getIdPublicacion(),
                    r.getFechaInicio().toString(), r.getFechaFin().toString(), r.getEstado().name(),
                    String.format(Locale.US, "%.2f", r.getCostoTotal()))).append("\n");
        }
        return sb.toString();
    }

    /** RF-10: escribe el reporte en rutaCarpeta/reporte_<id>_<fecha>.txt y actualiza fechaUltimoReporte. */
    public File exportarReporteArchivo(Propietario propietario) throws IOException {
        String contenido = generarReportePropietario(propietario);
        File carpeta = new File(rutaCarpeta);
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta: " + rutaCarpeta);
        }
        File archivo = new File(carpeta, "reporte_" + propietario.getIdUsuario() + "_" + LocalDate.now() + ".txt");
        try (PrintWriter pw = new PrintWriter(archivo, "UTF-8")) {
            pw.print(contenido);
        }
        this.fechaUltimoReporte = LocalDate.now();
        return archivo;
    }

    public String getRutaCarpeta() {
        return rutaCarpeta;
    }

    public String getDelimitadorCsv() {
        return delimitadorCsv;
    }

    public LocalDate getFechaUltimoReporte() {
        return fechaUltimoReporte;
    }

    public void setRutaCarpeta(String rutaCarpeta) {
        if (rutaCarpeta == null || rutaCarpeta.trim().isEmpty()) {
            throw new IllegalArgumentException("La ruta de la carpeta no puede estar vacia.");
        }
        this.rutaCarpeta = rutaCarpeta.trim();
    }

    public void setDelimitadorCsv(String delimitadorCsv) {
        if (delimitadorCsv == null || delimitadorCsv.isEmpty()) {
            throw new IllegalArgumentException("El delimitador no puede estar vacio.");
        }
        this.delimitadorCsv = delimitadorCsv;
    }

    public void setFechaUltimoReporte(LocalDate fechaUltimoReporte) {
        this.fechaUltimoReporte = fechaUltimoReporte;
    }
}
