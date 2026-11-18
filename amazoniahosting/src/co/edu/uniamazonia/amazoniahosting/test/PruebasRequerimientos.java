package co.edu.uniamazonia.amazoniahosting.test;

import co.edu.uniamazonia.amazoniahosting.interfaz.CargadorDatos;
import co.edu.uniamazonia.amazoniahosting.interfaz.ConsolaAmazoniaHosting;
import co.edu.uniamazonia.amazoniahosting.mundo.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Pruebas automaticas: una por cada requerimiento funcional (RF01-RF13) y no funcional (RNF01-RNF10).
 * Sin librerias externas. Ejecutar main(); termina con codigo 1 si alguna prueba falla.
 */
public class PruebasRequerimientos {

    private interface Prueba {
        void ejecutar() throws Exception;
    }

    private static class Omitida extends RuntimeException {
        Omitida(String motivo) {
            super(motivo);
        }
    }

    private static final Map<String, String> DESCRIPCIONES = new TreeMap<>();
    private static final Map<String, Prueba> PRUEBAS = new TreeMap<>();
    private static final Class<?>[] CLASES_MUNDO = {Usuario.class, Huesped.class, Propietario.class,
        GestionUsuario.class, Propiedad.class, Publicacion.class, Reservacion.class, Resena.class,
        GestorReporte.class};

    // ------------------------------------------------------------ utilidades
    private static void afirmar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }

    private static void lanza(Class<? extends Throwable> tipo, Runnable accion, String mensaje) {
        try {
            accion.run();
        } catch (Throwable t) {
            if (tipo.isInstance(t)) {
                return;
            }
            throw new AssertionError(mensaje + " (se esperaba " + tipo.getSimpleName() + " y llego " + t.getClass().getSimpleName() + ")");
        }
        throw new AssertionError(mensaje + " (no se lanzo " + tipo.getSimpleName() + ")");
    }

    private static Propietario propietario(String id) {
        return new Propietario(id, "Prop " + id, id + "@mail.co", "3000000000", "clave");
    }

    private static Huesped huesped(String id) {
        return new Huesped(id, "Hues " + id, id + "@mail.co", "3111111111", "clave");
    }

    /** Propietario con 1 propiedad y 1 publicacion activa. */
    private static Publicacion publicar(Propietario p, String n, long precio) {
        Propiedad prop = new Propiedad("PROP-" + n, "Florencia", "Hostal de prueba", precio, true, TipoPropiedad.HOSTAL);
        Publicacion pub = new Publicacion("PUB-" + n, "Hostal Prueba " + n, LocalDate.now(), true, prop);
        afirmar(p.crearPropiedad(prop) && p.crearPublicacion(pub), "No se pudo preparar el escenario");
        return pub;
    }

    private static LocalDate hoy(int dias) {
        return LocalDate.now().plusDays(dias);
    }

    private static String simular(String entrada) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GestionUsuario g = new GestionUsuario();
        CargadorDatos.cargar(g);
        Scanner sc = new Scanner(new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8)));
        new ConsolaAmazoniaHosting(g, sc, new PrintStream(bos, true, StandardCharsets.UTF_8)).ejecutar();
        return new String(bos.toByteArray(), StandardCharsets.UTF_8);
    }

    private static Path carpetaFuentesMundo() {
        String[] candidatos = {"src", "../src", "amazoniahosting/src"};
        for (String c : candidatos) {
            Path p = Paths.get(c, "co", "edu", "uniamazonia", "amazoniahosting", "mundo");
            if (Files.isDirectory(p)) {
                return p;
            }
        }
        return null;
    }

    private static void reg(String id, String descripcion, Prueba prueba) {
        DESCRIPCIONES.put(id, descripcion);
        PRUEBAS.put(id, prueba);
    }

    // ------------------------------------------------------------ definicion
    private static void definir() {
        reg("RF01", "Registro de usuarios y asignacion de rol", () -> {
            GestionUsuario g = new GestionUsuario();
            afirmar(g.registrarse(huesped("1")), "Debe registrar un huesped valido");
            afirmar(g.registrarse(propietario("2")), "Debe registrar un propietario valido");
            afirmar(!g.registrarse(huesped("1")), "Debe rechazar un id duplicado");
            afirmar(!g.registrarse(null), "Debe rechazar null");
            afirmar(!g.registrarse(new Usuario()), "Debe rechazar un usuario sin rol");
            lanza(IllegalArgumentException.class, () -> new Huesped("", "n", "c", "t", "p"), "Id vacio");
            lanza(IllegalArgumentException.class, () -> new Huesped("9", "  ", "c", "t", "p"), "Nombre vacio");
            afirmar(g.getUsers().size() == 2, "Solo deben quedar 2 usuarios");
            afirmar(g.buscarUsuario("1") instanceof Huesped && g.buscarUsuario("2") instanceof Propietario, "Roles asignados por herencia");
        });
        reg("RF02", "Autenticacion e inicio de sesion", () -> {
            GestionUsuario g = new GestionUsuario();
            g.registrarse(huesped("1"));
            afirmar(g.iniciarSesion("1", "clave") != null && g.getUsuario() != null, "Credenciales correctas deben abrir sesion");
            g.cerrarSesion();
            afirmar(g.getUsuario() == null, "cerrarSesion debe limpiar la sesion");
            afirmar(g.iniciarSesion("1", "mala") == null, "Contrasena incorrecta debe retornar null");
            afirmar(g.iniciarSesion("999", "clave") == null, "Id inexistente debe retornar null");
            afirmar(g.iniciarSesion(null, null) == null, "Entradas nulas deben retornar null sin excepcion");
            afirmar(g.getUsuario() == null, "Un login fallido no debe dejar sesion");
        });
        reg("RF03", "Control de acceso segun rol", () -> {
            GestionUsuario g = new GestionUsuario();
            afirmar(!g.gestionarPerfil("a", "b", "c"), "Sin sesion no se puede gestionar el perfil");
            Huesped h = huesped("1");
            Propietario p = propietario("2");
            g.registrarse(h);
            g.registrarse(p);
            afirmar("HUESPED".equals(h.getRol()) && "PROPIETARIO".equals(p.getRol()), "getRol polimorfico");
            Usuario u = p;
            afirmar("PROPIETARIO".equals(u.getRol()), "Despacho dinamico por referencia Usuario");
            boolean huespedTieneCrear = false;
            for (Method m : Huesped.class.getMethods()) {
                if (m.getName().equals("crearPublicacion") || m.getName().equals("crearPropiedad")) {
                    huespedTieneCrear = true;
                }
            }
            afirmar(!huespedTieneCrear, "Huesped no debe poder crear propiedades/publicaciones");
            g.iniciarSesion("1", "clave");
            afirmar(g.gestionarPerfil("Nuevo", "n@m.co", "300"), "Con sesion debe permitir editar el perfil");
            afirmar(!g.gestionarPerfil("", "n@m.co", "300"), "No debe aceptar campos vacios");
        });
        reg("RF04", "Registro de propiedad sin duplicados ni valores negativos", () -> {
            Propietario p = propietario("1");
            Propiedad a = new Propiedad("P1", "Centro", "Casa", 50000, true, TipoPropiedad.HOSTAL);
            afirmar(p.crearPropiedad(a), "Debe agregar una propiedad nueva");
            afirmar(!p.crearPropiedad(new Propiedad("P1", "Otra", "Otra", 10, true)), "Debe rechazar id repetido");
            afirmar(!p.crearPropiedad(null), "Debe rechazar null");
            lanza(IllegalArgumentException.class, () -> new Propiedad("P2", "x", "y", -5000, true), "Precio negativo");
            lanza(IllegalArgumentException.class, () -> new Propiedad("P3", "x", "y", 0, true), "Precio cero");
            afirmar(p.getPropiedades().size() == 1, "Solo debe haber una propiedad");
        });
        reg("RF05", "Busqueda exacta y filtrado por criterio", () -> {
            Propietario p = propietario("1");
            Publicacion a = publicar(p, "1", 80000);
            Publicacion b = publicar(p, "2", 90000);
            b.modificarPublicacion("Cabana del rio");
            b.getPropiedad().setTipoPropiedad(TipoPropiedad.CABANA);
            Publicacion c = publicar(p, "3", 70000);
            c.cambiarEstado();
            ArrayList<Publicacion> cat = p.getPublicaciones();
            Usuario u = huesped("9");
            afirmar(u.buscarPublicacion("PUB-1", cat).size() == 1, "Busqueda exacta por id");
            afirmar(u.buscarPublicacion("rio", cat).get(0) == b, "Filtro por titulo");
            afirmar(u.buscarPublicacion("cabana", cat).get(0) == b, "Filtro por tipo");
            afirmar(u.buscarPublicacion("", cat).size() == 2, "Sin criterio: solo las activas");
            afirmar(u.buscarPublicacion("PUB-3", cat).isEmpty(), "Las inactivas no se listan");
            afirmar(u.buscarPublicacion("zzz", cat).isEmpty(), "Sin coincidencias: lista vacia");
            afirmar(u.buscarPublicacion("x", null).isEmpty(), "Catalogo null: lista vacia");
            afirmar(a.isEstadoPublicacion(), "Escenario intacto");
        });
        reg("RF06", "Actualizacion controlada de estado y precio", () -> {
            Propiedad prop = new Propiedad("P1", "Centro", "Casa", 50000, true);
            prop.actualizarDisponibilidad(false, 65000);
            afirmar(!prop.consultarDisponibilidad() && prop.getPrecio() == 65000, "Debe actualizar estado y precio");
            lanza(IllegalArgumentException.class, () -> prop.actualizarDisponibilidad(true, -1), "Precio negativo");
            afirmar(!prop.consultarDisponibilidad() && prop.getPrecio() == 65000, "Un intento invalido no debe alterar el estado");
            Publicacion pub = new Publicacion("PUB-1", "T", LocalDate.now(), true);
            pub.cambiarEstado();
            afirmar(!pub.isEstadoPublicacion(), "cambiarEstado alterna a inactiva");
            pub.cambiarEstado();
            afirmar(pub.isEstadoPublicacion(), "cambiarEstado alterna a activa");
            lanza(IllegalArgumentException.class, () -> pub.modificarPublicacion("  "), "Titulo vacio");
        });
        reg("RF07", "Registro de transaccion (reservacion) y vinculos", () -> {
            Propietario p = propietario("1");
            Publicacion pub = publicar(p, "1", 100000);
            Huesped h = huesped("2");
            Reservacion r = h.reservarAlojamiento(pub, hoy(5), hoy(8), 2);
            afirmar(r.getEstado() == EstadoReservacion.PENDIENTE, "Nace PENDIENTE");
            afirmar(h.getReservaciones().contains(r) && pub.getReservaciones().contains(r), "Vinculada a huesped y publicacion");
            afirmar(r.getCostoTotal() == 300000.0, "3 noches x 100000");
            lanza(IllegalStateException.class, () -> huesped("3").reservarAlojamiento(pub, hoy(6), hoy(9), 1), "Cruce de fechas");
            lanza(IllegalArgumentException.class, () -> h.reservarAlojamiento(pub, hoy(20), hoy(19), 1), "Fin antes de inicio");
            lanza(IllegalArgumentException.class, () -> h.reservarAlojamiento(pub, hoy(-3), hoy(2), 1), "Inicio en el pasado");
            lanza(IllegalArgumentException.class, () -> h.reservarAlojamiento(null, hoy(1), hoy(2), 1), "Publicacion nula");
            pub.getPropiedad().actualizarDisponibilidad(false, 100000);
            lanza(IllegalStateException.class, () -> h.reservarAlojamiento(pub, hoy(30), hoy(31), 1), "Propiedad no disponible");
            r.confirmar();
            afirmar(r.getEstado() == EstadoReservacion.COMPLETADA, "confirmar -> COMPLETADA");
            lanza(IllegalStateException.class, r::cancelar, "No se cancela una COMPLETADA");
        });
        reg("RF08", "Calculo de noches y costo total", () -> {
            Propietario p = propietario("1");
            Publicacion pub = publicar(p, "1", 80000);
            Reservacion r = new Reservacion("R1", hoy(1), hoy(5), 2, 0);
            afirmar(r.calcularCantidadNoche() == 4, "4 noches");
            lanza(IllegalStateException.class, r::calcularCostoTotal, "Sin publicacion no puede calcular");
            r.setPublicacion(pub);
            afirmar(r.calcularCostoTotal() == 320000.0, "4 x 80000");
            pub.getPropiedad().setPrecio(100000);
            afirmar(r.calcularCostoTotal() == 400000.0, "Recalcula con el nuevo precio");
            lanza(IllegalArgumentException.class, () -> new Reservacion("R2", hoy(3), hoy(3), 1, 0), "0 noches");
            Usuario[] usuarios = {huesped("1"), propietario("2"), new Usuario()};
            String[] esperados = {"HUESPED", "PROPIETARIO", "USUARIO"};
            for (int i = 0; i < usuarios.length; i++) {
                afirmar(esperados[i].equals(usuarios[i].getRol()), "Sobreescritura de getRol");
            }
        });
        reg("RF09", "Eliminacion segura", () -> {
            Propietario p = propietario("1");
            Publicacion pub = publicar(p, "1", 50000);
            afirmar(!p.eliminarPropiedad("PROP-1"), "No elimina propiedad con publicacion activa");
            afirmar(!p.eliminarPropiedad("NOEXISTE"), "Id inexistente retorna false");
            Huesped h = huesped("2");
            Reservacion r = h.reservarAlojamiento(pub, hoy(2), hoy(4), 1);
            afirmar(!p.eliminarPublicacion("PUB-1"), "No elimina publicacion con reservas pendientes");
            afirmar(h.eliminarReserva(r.getIdReservacion()), "Eliminar reserva pendiente la cancela y la quita");
            afirmar(pub.getReservaciones().isEmpty(), "Se desvincula de la publicacion");
            afirmar(!h.eliminarReserva("XXX"), "Reserva inexistente retorna false");
            Reservacion r2 = h.reservarAlojamiento(pub, hoy(2), hoy(4), 1);
            r2.confirmar();
            afirmar(!h.eliminarReserva(r2.getIdReservacion()), "Una COMPLETADA no se elimina");
            afirmar(p.eliminarPublicacion("PUB-1"), "Elimina publicacion sin reservas pendientes");
            afirmar(p.eliminarPropiedad("PROP-1"), "Elimina propiedad sin publicaciones");
            GestionUsuario g = new GestionUsuario();
            Propietario q = propietario("5");
            publicar(q, "7", 1000);
            g.registrarse(q);
            afirmar(!g.eliminarUsuario("5"), "No elimina propietario con publicaciones activas");
            afirmar(!g.eliminarUsuario("404"), "Usuario inexistente retorna false");
            afirmar(g.eliminarUsuario("5") == false && q.eliminarPublicacion("PUB-7") && g.eliminarUsuario("5"),
                    "Tras limpiar publicaciones si puede eliminarse");
        });
        reg("RF10", "Reporte consolidado y persistencia en archivo", () -> {
            Propietario p = propietario("1");
            Publicacion pub = publicar(p, "1", 100000);
            Huesped h = huesped("2");
            Reservacion r1 = h.reservarAlojamiento(pub, hoy(2), hoy(4), 1);
            r1.confirmar();
            h.reservarAlojamiento(pub, hoy(10), hoy(11), 1);
            h.darResena(r1, 2.0, "Regular");
            File dir = Files.createTempDirectory("ah_rep").toFile();
            GestorReporte gr = new GestorReporte(dir.getPath(), ";", null);
            afirmar(gr.calcularTotalRecaudado(pub.getReservaciones()) == 200000.0, "Total solo de COMPLETADAS");
            afirmar(gr.calcularPromedioCalificaciones(pub.getResenas()) == 2.0, "Promedio");
            afirmar(gr.contarReservasPorEstado(pub.getReservaciones()).get(EstadoReservacion.PENDIENTE) == 1, "Conteo PENDIENTE");
            afirmar(gr.obtenerPublicacionMasCritica(p.getPublicaciones()) == pub, "Publicacion mas critica");
            String texto = gr.generarReportePropietario(p);
            afirmar(texto.contains("Total recaudado") && texto.contains("idReservacion;idPublicacion"), "Contenido del reporte");
            File f = gr.exportarReporteArchivo(p);
            afirmar(f.exists() && f.length() > 0, "El archivo .txt debe existir");
            afirmar(Files.readString(f.toPath(), StandardCharsets.UTF_8).equals(texto), "Archivo = reporte");
            afirmar(gr.getFechaUltimoReporte() != null, "Actualiza fechaUltimoReporte");
            lanza(IllegalArgumentException.class, () -> gr.generarReportePropietario(null), "Propietario nulo");
            f.delete();
            dir.delete();
            Propietario vacio = propietario("3");
            afirmar(gr.calcularTotalRecaudado(new ArrayList<>()) == 0.0, "Lista vacia: total 0.0");
            afirmar(gr.calcularPromedioCalificaciones(new ArrayList<>()) == 0.0, "Lista vacia: promedio 0.0 sin dividir por cero");
            afirmar(gr.obtenerPublicacionMasCritica(vacio.getPublicaciones()) == null, "Sin publicaciones: null");
            afirmar(gr.generarReportePropietario(vacio).contains("ninguna"), "Reporte de propietario sin actividad");
        });
        reg("RF11", "Calificacion y resenas de estadias completadas", () -> {
            Propietario p = propietario("1");
            Publicacion pub = publicar(p, "1", 50000);
            Huesped h = huesped("2");
            Reservacion r = h.reservarAlojamiento(pub, hoy(1), hoy(3), 1);
            lanza(IllegalStateException.class, () -> h.darResena(r, 5, "ok"), "Estadia no completada");
            r.confirmar();
            lanza(IllegalArgumentException.class, () -> h.darResena(r, 6, "ok"), "Calificacion > 5");
            lanza(IllegalArgumentException.class, () -> h.darResena(r, 0.5, "ok"), "Calificacion < 1");
            lanza(IllegalArgumentException.class, () -> h.darResena(r, 3, null), "Comentario nulo");
            Resena rs = h.darResena(r, 4.5, "Muy bueno");
            afirmar(pub.getResenas().contains(rs) && rs.getPublicacion() == pub, "Resena vinculada a la publicacion");
            lanza(IllegalStateException.class, () -> h.darResena(r, 3, "otra"), "Resena duplicada");
            rs.editarResena(3.0, "Cambie de opinion");
            afirmar(rs.getCalificacion() == 3.0 && rs.getComentario().equals("Cambie de opinion"), "editarResena");
            lanza(IllegalArgumentException.class, () -> rs.editarResena(9, "x"), "Edicion invalida");
            afirmar(rs.getCalificacion() == 3.0, "Edicion invalida no altera la resena");
            Reservacion ajena = huesped("3").reservarAlojamiento(pub, hoy(20), hoy(21), 1);
            ajena.confirmar();
            lanza(IllegalArgumentException.class, () -> h.darResena(ajena, 5, "x"), "Reserva de otro huesped");
        });
        reg("RF12", "Modificacion de reservas con validacion de disponibilidad", () -> {
            Propietario p = propietario("1");
            Publicacion pub = publicar(p, "1", 100000);
            Huesped h = huesped("2");
            Reservacion r = h.reservarAlojamiento(pub, hoy(5), hoy(7), 1);
            h.modificarReserva(r, hoy(5), hoy(10), 3);
            afirmar(r.getCostoTotal() == 500000.0 && r.getCantidadHuesped() == 3, "Recalcula costo y cantidad");
            Reservacion otra = huesped("3").reservarAlojamiento(pub, hoy(12), hoy(14), 1);
            lanza(IllegalStateException.class, () -> h.modificarReserva(r, hoy(11), hoy(13), 1), "Cruce con otra reserva");
            afirmar(r.getFechaFin().equals(hoy(10)), "El intento fallido no altera la reserva");
            lanza(IllegalArgumentException.class, () -> h.modificarReserva(r, hoy(9), hoy(8), 1), "Fechas invertidas");
            afirmar(r.getFechaInicio().equals(hoy(5)) && r.getFechaFin().equals(hoy(10)), "Fechas intactas tras error");
            lanza(IllegalArgumentException.class, () -> h.modificarReserva(otra, hoy(20), hoy(21), 1), "Reserva ajena");
            r.confirmar();
            lanza(IllegalStateException.class, () -> h.modificarReserva(r, hoy(5), hoy(6), 1), "Solo PENDIENTES");
        });
        reg("RF13", "Administracion de propiedades y publicaciones por propietario", () -> {
            Propietario p = propietario("1");
            Publicacion a = publicar(p, "1", 80000);
            Publicacion b = publicar(p, "2", 90000);
            afirmar(p.getPropiedades().size() == 2 && p.getPublicaciones().size() == 2, "Lista lo que le pertenece");
            afirmar(p.buscarPropiedad("prop-1") == a.getPropiedad(), "buscarPropiedad ignora mayusculas");
            afirmar(p.buscarPublicacionPropia("PUB-2") == b, "buscarPublicacionPropia");
            afirmar(a.consultarPublicacion().contains("PUB-1") && a.consultarPublicacion().contains("Hostal"), "consultarPublicacion");
            afirmar(a.getPropiedad().consultarPropiedad().contains("80000"), "consultarPropiedad");
            afirmar(a.getPropiedad().consultarDisponibilidad(), "consultarDisponibilidad");
            Propiedad ajena = new Propiedad("PROP-X", "Otro", "Otro", 1000, true);
            afirmar(!p.crearPublicacion(new Publicacion("PUB-X", "T", LocalDate.now(), true, ajena)), "No publica propiedades que no son suyas");
            afirmar(!p.crearPublicacion(new Publicacion("PUB-Y", "T", LocalDate.now(), true)), "No publica sin propiedad");
            p.getPropiedades().clear();
            afirmar(p.getPropiedades().size() == 2, "Las listas devueltas son copias (encapsulamiento)");
        });

        // ------------------------------ no funcionales
        reg("RNF01", "Separacion de capas: .mundo sin Scanner ni impresiones", () -> {
            Path dir = carpetaFuentesMundo();
            if (dir == null) {
                throw new Omitida("No se encontro src/.../mundo desde " + System.getProperty("user.dir"));
            }
            String[] prohibidos = {"Scanner", "System.in", "System.out", "System.err", "import co.edu.uniamazonia.amazoniahosting.interfaz"};
            int revisados = 0;
            try (var archivos = Files.list(dir)) {
                for (Path f : (Iterable<Path>) archivos.filter(x -> x.toString().endsWith(".java"))::iterator) {
                    String contenido = Files.readString(f, StandardCharsets.UTF_8);
                    revisados++;
                    for (String s : prohibidos) {
                        afirmar(!contenido.contains(s), f.getFileName() + " contiene '" + s + "'");
                    }
                }
            }
            afirmar(revisados >= 10, "Se esperaban al menos 10 archivos en .mundo");
        });
        reg("RNF02", "Encapsulamiento estricto: atributos privados, sin estaticos mutables", () -> {
            for (Class<?> c : CLASES_MUNDO) {
                for (Field f : c.getDeclaredFields()) {
                    if (f.isSynthetic()) {
                        continue;
                    }
                    int m = f.getModifiers();
                    boolean constante = Modifier.isStatic(m) && Modifier.isFinal(m);
                    afirmar(Modifier.isPrivate(m) || (constante && Modifier.isPublic(m)),
                            c.getSimpleName() + "." + f.getName() + " no es private");
                    afirmar(!Modifier.isStatic(m) || constante, c.getSimpleName() + "." + f.getName() + " es estatico mutable");
                }
            }
        });
        reg("RNF03", "Trazabilidad UML v2.0 -> Java (clases, atributos, metodos, herencia)", () -> {
            String[][] uml = {
                {"Usuario", "idUsuario:String", "nombreUsuario:String", "correoUsuario:String", "telefonoUsuario:String", "contrasena:String", "#buscarPublicacion"},
                {"GestionUsuario", "#registrarse", "#gestionarPerfil", "#iniciarSesion", "#cerrarSesion", "#eliminarUsuario"},
                {"Huesped", "#reservarAlojamiento", "#modificarReserva", "#eliminarReserva", "#darResena"},
                {"Propietario", "#crearPropiedad", "#crearPublicacion", "#eliminarPropiedad", "#eliminarPublicacion"},
                {"Reservacion", "idReservacion:String", "fechaInicio:LocalDate", "fechaFin:LocalDate", "cantidadHuesped:int", "costoTotal:double",
                    "#calcularCantidadNoche", "#calcularCostoTotal", "#confirmar", "#cancelar"},
                {"Resena", "idResena:String", "calificacion:double", "comentario:String", "fechaResena:LocalDate", "#editarResena"},
                {"Publicacion", "idPublicacion:String", "tituloPublicacion:String", "fechaPublicacion:LocalDate", "estadoPublicacion:boolean",
                    "#consultarPublicacion", "#modificarPublicacion", "#cambiarEstado"},
                {"Propiedad", "idPropiedad:String", "ubicacionPropiedad:String", "descripcionPropiedad:String", "precio:long", "estadoDisponibilidad:boolean",
                    "#consultarPropiedad", "#actualizarDatos", "#consultarDisponibilidad", "#actualizarDisponibilidad"},
                {"GestorReporte", "rutaCarpeta:String", "delimitadorCsv:String", "fechaUltimoReporte:LocalDate",
                    "#calcularTotalRecaudado", "#calcularPromedioCalificaciones", "#contarReservasPorEstado", "#obtenerPublicacionMasCritica",
                    "#generarReportePropietario", "#exportarReporteArchivo"}
            };
            String pkg = "co.edu.uniamazonia.amazoniahosting.mundo.";
            for (String[] fila : uml) {
                Class<?> c = Class.forName(pkg + fila[0]);
                for (int i = 1; i < fila.length; i++) {
                    String item = fila[i];
                    if (item.startsWith("#")) {
                        boolean existe = false;
                        for (Method m : c.getDeclaredMethods()) {
                            existe |= m.getName().equals(item.substring(1)) && Modifier.isPublic(m.getModifiers());
                        }
                        afirmar(existe, fila[0] + " no tiene el metodo publico " + item.substring(1));
                    } else {
                        String[] par = item.split(":");
                        Field f = c.getDeclaredField(par[0]);
                        afirmar(f.getType().getSimpleName().equals(par[1]), fila[0] + "." + par[0] + " deberia ser " + par[1]);
                    }
                }
            }
            afirmar(Usuario.class.isAssignableFrom(Huesped.class) && Huesped.class.getSuperclass() == Usuario.class, "Huesped extends Usuario");
            afirmar(Propietario.class.getSuperclass() == Usuario.class, "Propietario extends Usuario");
            afirmar(Class.forName(pkg + "EstadoReservacion").getEnumConstants().length == 3, "EstadoReservacion: 3 valores");
            afirmar(Class.forName(pkg + "TipoPropiedad").getEnumConstants().length == 9, "TipoPropiedad: 9 valores");
            // Atributo agregado respecto al UML actual: Propiedad.tipoPropiedad (ver informe: actualizar UML)
            afirmar(Propiedad.class.getDeclaredField("tipoPropiedad").getType() == TipoPropiedad.class, "Propiedad.tipoPropiedad");
        });
        reg("RNF04", "Robustez: entradas nulas/erroneas no producen NullPointerException", () -> {
            Usuario u = huesped("1");
            GestionUsuario g = new GestionUsuario();
            GestorReporte gr = new GestorReporte("data", ";", null);
            Propietario p = propietario("2");
            Huesped h = huesped("3");
            Runnable[] peligrosas = {
                () -> g.registrarse(null), () -> g.iniciarSesion(null, null), () -> g.eliminarUsuario(null),
                () -> g.buscarUsuario(null), () -> g.gestionarPerfil(null, null, null),
                () -> u.buscarPublicacion(null, null), () -> p.crearPropiedad(null), () -> p.crearPublicacion(null),
                () -> p.eliminarPropiedad(null), () -> p.eliminarPublicacion(null), () -> p.buscarPropiedad(null),
                () -> h.eliminarReserva(null), () -> h.buscarReservacion(null),
                () -> gr.calcularTotalRecaudado(null), () -> gr.calcularPromedioCalificaciones(null),
                () -> gr.contarReservasPorEstado(null), () -> gr.obtenerPublicacionMasCritica(null)
            };
            for (int i = 0; i < peligrosas.length; i++) {
                try {
                    peligrosas[i].run();
                } catch (NullPointerException e) {
                    throw new AssertionError("NullPointerException en la llamada #" + i);
                }
            }
            lanza(IllegalArgumentException.class, () -> h.reservarAlojamiento(null, null, null, 0), "Reserva con todo nulo");
            lanza(IllegalArgumentException.class, () -> h.darResena(null, 3, "x"), "Resena sobre reserva nula");
            lanza(IllegalArgumentException.class, () -> h.modificarReserva(null, null, null, 1), "Modificar reserva nula");
            String salida = simular("abc\n99\n2\n\n\n3\n");
            afirmar(salida.contains("opcion valida") && salida.contains("Credenciales incorrectas") && salida.contains("Gracias"),
                    "La consola debe tolerar basura y continuar");
            String salida2 = simular("2\n3000000000\n1234\n3\nBENITO\nx\n9\n3\n");
            afirmar(!salida2.contains("Exception"), "La consola no debe mostrar excepciones");
            String corta = simular("2\n3000000000\n");
            afirmar(corta.contains("Entrada finalizada"), "Fin de entrada inesperado se maneja");
        });
        reg("RNF05", "Usabilidad: menus claros por rol y mensajes de ayuda (flujo simulado)", () -> {
            String s = simular("2\n3000000000\n1234\n2\nhostal\n\n9\n3\n");
            afirmar(s.contains("1. Registrarse") && s.contains("2. Iniciar sesion") && s.contains("3. Salir"), "Menu principal");
            afirmar(s.contains("MENU HUESPED") && s.contains("Buscar publicaciones") && s.contains("Cerrar sesion"), "Menu del huesped");
            afirmar(s.contains("Hostal Amazonico Centro"), "La busqueda por 'hostal' muestra resultados");
            afirmar(s.contains("Sesion cerrada exitosamente"), "Mensaje de cierre de sesion");
            String p = simular("2\n1000000000\n1234\n11\n3\n");
            afirmar(p.contains("MENU PROPIETARIO") && p.contains("Generar reporte") && p.contains("Crear publicacion"), "Menu del propietario");
            String reg = simular("1\n555\nAna\nana@mail.co\n300\nsecreta\n1\n3\n");
            afirmar(reg.contains("Usuario registrado exitosamente"), "Registro guiado desde la consola");
            // Complemento manual: prueba de navegacion con usuarios reales (ver informe, RNF05)
        });
        reg("RNF06", "Rendimiento: busqueda y calculo con volumen alto", () -> {
            Propietario p = propietario("1");
            ArrayList<Publicacion> cat = new ArrayList<>();
            for (int i = 0; i < 20000; i++) {
                Propiedad prop = new Propiedad("P" + i, "Florencia", "Prop " + i, 50000 + i, true, TipoPropiedad.HOSTAL);
                cat.add(new Publicacion("PUB-" + i, "Hostal numero " + i, LocalDate.now(), true, prop));
            }
            long t0 = System.nanoTime();
            ArrayList<Publicacion> res = p.buscarPublicacion("numero 19999", cat);
            long msBusqueda = (System.nanoTime() - t0) / 1_000_000;
            afirmar(res.size() == 1, "Debe encontrar 1 coincidencia");
            afirmar(msBusqueda < 500, "Busqueda en 20.000 publicaciones tardo " + msBusqueda + " ms (limite 500)");
            Reservacion r = new Reservacion("R", hoy(1), hoy(4), 1, 0);
            r.setPublicacion(cat.get(0));
            t0 = System.nanoTime();
            for (int i = 0; i < 200000; i++) {
                r.calcularCostoTotal();
            }
            long msCalculo = (System.nanoTime() - t0) / 1_000_000;
            afirmar(msCalculo < 500, "200.000 calculos de costo tardaron " + msCalculo + " ms (limite 500)");
        });
        reg("RNF07", "Seguridad: datos protegidos y acceso restringido por permisos", () -> {
            Huesped h = new Huesped("1", "Ana", "ana@m.co", "300", "claveSuperSecreta");
            afirmar(!h.toString().contains("claveSuperSecreta"), "toString no debe exponer la contrasena");
            String salida = simular("2\n3000000000\n1234\n1\n2\n9\n3\n");
            afirmar(!salida.contains("claveSecreta") && !salida.contains("Contrasena: 1234") && !salida.contains("1234 -"), "La consola no imprime contrasenas");
            GestionUsuario g = new GestionUsuario();
            g.registrarse(h);
            afirmar(g.iniciarSesion("1", "claveSuperSecreta") != null, "Login correcto");
            g.cerrarSesion();
            afirmar(!g.gestionarPerfil("x", "y", "z"), "Tras cerrar sesion no se editan datos");
            Propietario p = propietario("2");
            Publicacion pub = publicar(p, "1", 1000);
            Huesped intruso = huesped("3");
            Reservacion ajena = h.reservarAlojamiento(pub, hoy(1), hoy(2), 1);
            lanza(IllegalArgumentException.class, () -> intruso.modificarReserva(ajena, hoy(3), hoy(4), 1), "Modificar reserva ajena");
            afirmar(!intruso.eliminarReserva(ajena.getIdReservacion()), "Eliminar reserva ajena");
            afirmar(!p.crearPublicacion(new Publicacion("PUB-9", "t", LocalDate.now(), true,
                    new Propiedad("PX", "u", "d", 5, true))), "Publicar propiedad de otro");
            String p2 = simular("2\n3000000000\n1234\n10\n11\n3\n");
            afirmar(p2.contains("opcion valida"), "El huesped no accede a opciones de propietario");
        });
        reg("RNF08", "Disponibilidad: el sistema arranca y opera de forma continua", () -> {
            for (int i = 0; i < 50; i++) {
                String s = simular("2\n3000000000\n1234\n2\n\n\n9\n3\n");
                afirmar(s.contains("Gracias"), "Sesion #" + i + " no termino correctamente");
            }
            GestionUsuario g = new GestionUsuario();
            afirmar(CargadorDatos.cargar(g) == 3 && g.getUsers().size() == 2, "La precarga debe crear 3 publicaciones y 2 usuarios");
            // Complemento: disponibilidad 24/7 de un servidor real queda fuera del alcance de la version de consola.
        });
        reg("RNF09", "Compatibilidad/portabilidad: ejecuta en cualquier SO con JDK >= 17 sin rutas absolutas", () -> {
            afirmar(Runtime.version().feature() >= 17, "Se requiere Java 17 o superior");
            File dir = Files.createTempDirectory("ah_port").toFile();
            GestorReporte gr = new GestorReporte(new File(dir, "data").getPath(), ";", null);
            Propietario p = propietario("1");
            File f = gr.exportarReporteArchivo(p);
            afirmar(f.getParentFile().getName().equals("data") && f.exists(), "Crea la carpeta de datos si no existe");
            f.delete();
            f.getParentFile().delete();
            dir.delete();
            afirmar(new GestorReporte("data", ";", null).getRutaCarpeta().equals("data"), "Ruta por defecto relativa (data/)");
        });
        reg("RNF10", "Mantenibilidad: clases con responsabilidades definidas y bajo acoplamiento", () -> {
            for (Class<?> c : CLASES_MUNDO) {
                int publicos = 0;
                for (Method m : c.getDeclaredMethods()) {
                    if (Modifier.isPublic(m.getModifiers()) && !m.getName().startsWith("get") && !m.getName().startsWith("set")
                            && !m.getName().startsWith("is")) {
                        publicos++;
                    }
                }
                afirmar(publicos <= 12, c.getSimpleName() + " tiene " + publicos + " metodos de negocio (maximo 12)");
            }
            Path dir = carpetaFuentesMundo();
            if (dir == null) {
                throw new Omitida("Sin fuentes disponibles para revisar imports");
            }
            try (var archivos = Files.list(dir)) {
                for (Path f : (Iterable<Path>) archivos.filter(x -> x.toString().endsWith(".java"))::iterator) {
                    String c = Files.readString(f, StandardCharsets.UTF_8);
                    afirmar(!c.contains(".interfaz.") && !c.contains(".test."), f.getFileName() + " depende de otras capas");
                    afirmar(c.lines().count() <= 400, f.getFileName() + " supera las 400 lineas");
                }
            }
        });
    }

    // ------------------------------------------------------------ ejecucion
    public static void main(String[] args) {
        definir();
        int ok = 0;
        int fallos = 0;
        int omitidas = 0;
        System.out.println("=== PRUEBAS DE REQUERIMIENTOS - AMAZONIA HOSTING ===");
        System.out.printf("%-7s %-78s %s%n", "ID", "Requerimiento", "Resultado");
        for (Map.Entry<String, Prueba> e : PRUEBAS.entrySet()) {
            String estado;
            try {
                e.getValue().ejecutar();
                estado = "EXITOSO";
                ok++;
            } catch (Omitida o) {
                estado = "OMITIDA (" + o.getMessage() + ")";
                omitidas++;
            } catch (Throwable t) {
                estado = "FALLO -> " + t.getClass().getSimpleName() + ": " + t.getMessage();
                fallos++;
            }
            System.out.printf("%-7s %-78s %s%n", e.getKey(), DESCRIPCIONES.get(e.getKey()), estado);
        }
        System.out.println("-----------------------------------------------------------------");
        System.out.println("Exitosas: " + ok + " | Fallidas: " + fallos + " | Omitidas: " + omitidas + " | Total: " + PRUEBAS.size());
        if (fallos > 0) {
            System.exit(1);
        }
    }
}
