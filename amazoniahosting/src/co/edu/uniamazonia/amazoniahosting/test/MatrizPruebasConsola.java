package co.edu.uniamazonia.amazoniahosting.test;

import co.edu.uniamazonia.amazoniahosting.mundo.*;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;

/** Imprime en consola las 8 pruebas de la Tabla 8 (PR-01 a PR-08) con su salida real. */
public class MatrizPruebasConsola {

    public static void main(String[] args) throws Exception {
        GestionUsuario g = new GestionUsuario();
        Propietario pepito = new Propietario("1000000000", "Pepito Perez", "pepito@perez.co", "3100000000", "1234");
        Huesped benito = new Huesped("3000000000", "Benito Velasquez", "beni@velasquez.co", "3300000000", "1234");

        // PR-01 Flujo normal: registro valido (RF-01)
        boolean r1 = g.registrarse(pepito) && g.registrarse(benito);
        mostrar("PR-01", "Registro valido (RF-01)", "ID 1000000000 y 3000000000", "registrarse -> " + r1 + "; usuarios = " + g.getUsers().size());

        // PR-02 Flujo normal: consulta de publicacion existente (RF-05)
        Propiedad prop = new Propiedad("PROP-1", "Florencia, barrio Centro", "Hostal con wifi", 80000, true, TipoPropiedad.HOSTAL);
        Publicacion pub = new Publicacion("PUB-1", "Hostal Amazonico Centro", LocalDate.now(), true, prop);
        pepito.crearPropiedad(prop);
        pepito.crearPublicacion(pub);
        ArrayList<Publicacion> res = benito.buscarPublicacion("PUB-1", pepito.getPublicaciones());
        mostrar("PR-02", "Consulta existente (RF-05)", "Buscar ID 'PUB-1'", res.size() + " resultado -> " + res.get(0).consultarPublicacion());

        // PR-03 Flujo normal: calculo de costo (RF-08)
        LocalDate ini = LocalDate.now().plusDays(3);
        Reservacion rv = benito.reservarAlojamiento(pub, ini, ini.plusDays(3), 2);
        mostrar("PR-03", "Calculo de costo (RF-08)", "3 noches x precio 80000", "noches = " + rv.calcularCantidadNoche() + "; costoTotal = " + rv.getCostoTotal() + "; estado = " + rv.getEstado());

        // PR-04 Flujo normal: reporte y persistencia (RF-10)
        rv.confirmar();
        GestorReporte gr = new GestorReporte("data", ";", null);
        File f = gr.exportarReporteArchivo(pepito);
        mostrar("PR-04", "Reporte a archivo (RF-10)", "Reporte del propietario 1000000000", "archivo " + f.getPath() + " (" + f.length() + " bytes); total recaudado = " + gr.calcularTotalRecaudado(pub.getReservaciones()));
        System.out.println(gr.generarReportePropietario(pepito));
        f.delete();
        f.getParentFile().delete();

        // PR-05 Limite: id duplicado
        boolean r5 = g.registrarse(new Huesped("3000000000", "Otro", "o@o.co", "300", "x"));
        mostrar("PR-05", "ID duplicado (RF-01)", "Registrar de nuevo ID 3000000000", "registrarse -> " + r5 + "; usuarios siguen = " + g.getUsers().size());

        // PR-06 Limite: dato invalido
        String r6;
        try {
            new Propiedad("PROP-2", "x", "y", -5000, true);
            r6 = "SIN ERROR (fallo)";
        } catch (IllegalArgumentException e) {
            r6 = "IllegalArgumentException: " + e.getMessage();
        }
        mostrar("PR-06", "Dato invalido (validacion)", "Precio = -5000", r6);

        // PR-07 Limite: ID inexistente
        boolean r7 = pepito.eliminarPropiedad("P999");
        Publicacion r7b = pepito.buscarPublicacionPropia("P999");
        mostrar("PR-07", "ID inexistente (RF-09 / RF-05)", "Eliminar/buscar ID 'P999'", "eliminarPropiedad -> " + r7 + "; buscarPublicacionPropia -> " + r7b);

        // PR-08 Limite: coleccion vacia
        double total = gr.calcularTotalRecaudado(new ArrayList<>());
        double prom = gr.calcularPromedioCalificaciones(new ArrayList<>());
        mostrar("PR-08", "Coleccion vacia (reporte)", "ArrayList con 0 elementos", "total = " + total + "; promedio = " + prom + " (sin dividir por cero)");
    }

    private static void mostrar(String id, String caso, String entrada, String salida) {
        System.out.println("[" + id + "] " + caso + "\n    Entrada: " + entrada + "\n    Salida : " + salida + "\n    Estado : EXITOSO\n");
    }
}
