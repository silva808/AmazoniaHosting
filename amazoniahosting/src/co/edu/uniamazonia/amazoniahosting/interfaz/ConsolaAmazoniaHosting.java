package co.edu.uniamazonia.amazoniahosting.interfaz;

import co.edu.uniamazonia.amazoniahosting.mundo.GestionUsuario;
import co.edu.uniamazonia.amazoniahosting.mundo.GestorReporte;
import co.edu.uniamazonia.amazoniahosting.mundo.Huesped;
import co.edu.uniamazonia.amazoniahosting.mundo.Propiedad;
import co.edu.uniamazonia.amazoniahosting.mundo.Propietario;
import co.edu.uniamazonia.amazoniahosting.mundo.Publicacion;
import co.edu.uniamazonia.amazoniahosting.mundo.Reservacion;
import co.edu.uniamazonia.amazoniahosting.mundo.Resena;
import co.edu.uniamazonia.amazoniahosting.mundo.TipoPropiedad;
import co.edu.uniamazonia.amazoniahosting.mundo.Usuario;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Capa de interaccion: unica clase que lee teclado e imprime menus (RNF01). */
public class ConsolaAmazoniaHosting {

    private final GestionUsuario gestor;
    private final GestorReporte reportes;
    private final Scanner in;
    private final PrintStream out;
    private int contPropiedad;
    private int contPublicacion;

    public ConsolaAmazoniaHosting(GestionUsuario gestor, Scanner in, PrintStream out) {
        this.gestor = gestor;
        this.in = in;
        this.out = out;
        this.reportes = new GestorReporte("data", ";", null);
        this.contPropiedad = 1000;
        this.contPublicacion = 1000;
    }

    public void ejecutar() {
        try {
            boolean app = true;
            while (app) {
                out.println("\n----- AMAZONIA HOSTING -----");
                out.println("1. Registrarse\n2. Iniciar sesion\n3. Salir");
                String opc = leer("Seleccione una opcion: ");
                switch (opc) {
                    case "1" -> registrar();
                    case "2" -> iniciarSesion();
                    case "3" -> {
                        out.println("Gracias por usar Amazonia Hosting, vuelva pronto!");
                        app = false;
                    }
                    default -> out.println("Por favor ingrese una opcion valida.");
                }
            }
        } catch (NoSuchElementException e) {
            out.println("Entrada finalizada. Cerrando la aplicacion.");
        }
    }

    // ---------------- acceso ----------------
    private void registrar() {
        out.println("--- Registro de usuario ---");
        String id = leerNoVacio("Identificacion: ");
        if (gestor.existeUsuario(id)) {
            out.println("El numero de identificacion " + id + " ya esta registrado.");
            return;
        }
        String nombre = leerNoVacio("Nombre completo: ");
        String correo = leerNoVacio("Correo electronico: ");
        String telefono = leerNoVacio("Telefono: ");
        String clave = leerNoVacio("Contrasena: ");
        int rol = leerEntero("Tipo de usuario (1. Huesped, 2. Propietario): ", 1, 2);
        try {
            Usuario nuevo = (rol == 1)
                    ? new Huesped(id, nombre, correo, telefono, clave)
                    : new Propietario(id, nombre, correo, telefono, clave);
            out.println(gestor.registrarse(nuevo) ? "Usuario registrado exitosamente." : "No fue posible registrar el usuario.");
        } catch (IllegalArgumentException e) {
            out.println("Dato invalido: " + e.getMessage());
        }
    }

    private void iniciarSesion() {
        String id = leer("Identificacion: ");
        String clave = leer("Contrasena: ");
        Usuario u = gestor.iniciarSesion(id, clave);
        if (u == null) {
            out.println("Credenciales incorrectas.");
            return;
        }
        out.println("Bienvenido, " + u.getNombreUsuario() + "!");
        if (u instanceof Huesped) {
            menuHuesped((Huesped) u);
        } else if (u instanceof Propietario) {
            menuPropietario((Propietario) u);
        }
    }

    // ---------------- menu huesped ----------------
    private void menuHuesped(Huesped h) {
        boolean sesion = true;
        while (sesion) {
            out.println("\n=== MENU HUESPED: " + h.getNombreUsuario().toUpperCase() + " ===");
            out.println("1. Gestionar perfil\n2. Buscar publicaciones\n3. Reservar alojamiento\n"
                    + "4. Ver mis reservas\n5. Modificar reserva\n6. Confirmar reserva\n"
                    + "7. Eliminar reserva\n8. Dar resena\n9. Cerrar sesion");
            String opc = leer("Seleccione una opcion: ");
            try {
                switch (opc) {
                    case "1" -> perfil();
                    case "2" -> buscar(h);
                    case "3" -> reservar(h);
                    case "4" -> listarReservas(h);
                    case "5" -> modificarReserva(h);
                    case "6" -> confirmarReserva(h);
                    case "7" -> eliminarReserva(h);
                    case "8" -> darResena(h);
                    case "9" -> {
                        gestor.cerrarSesion();
                        out.println("Sesion cerrada exitosamente.");
                        sesion = false;
                    }
                    default -> out.println("Por favor ingrese una opcion valida.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                out.println("No se pudo completar la operacion: " + e.getMessage());
            }
        }
    }

    private void reservar(Huesped h) {
        Publicacion pub = pedirPublicacion();
        if (pub == null) {
            return;
        }
        LocalDate ini = leerFecha("Fecha de inicio (AAAA-MM-DD): ");
        LocalDate fin = leerFecha("Fecha de fin (AAAA-MM-DD): ");
        int cant = leerEntero("Cantidad de huespedes: ", 1, 100);
        Reservacion r = h.reservarAlojamiento(pub, ini, fin, cant);
        out.println("Reserva creada: " + r.getIdReservacion() + " | noches: " + r.calcularCantidadNoche()
                + " | costo total: " + r.getCostoTotal() + " | estado: " + r.getEstado());
    }

    private void listarReservas(Huesped h) {
        ArrayList<Reservacion> rs = h.getReservaciones();
        if (rs.isEmpty()) {
            out.println("No tiene reservas registradas.");
            return;
        }
        for (Reservacion r : rs) {
            out.println(r.getIdReservacion() + " | " + r.getPublicacion().getTituloPublicacion() + " | "
                    + r.getFechaInicio() + " a " + r.getFechaFin() + " | " + r.getEstado() + " | " + r.getCostoTotal());
        }
    }

    private void modificarReserva(Huesped h) {
        Reservacion r = h.buscarReservacion(leer("ID de la reserva: "));
        if (r == null) {
            out.println("No se encontro la reserva.");
            return;
        }
        LocalDate ini = leerFecha("Nueva fecha de inicio (AAAA-MM-DD): ");
        LocalDate fin = leerFecha("Nueva fecha de fin (AAAA-MM-DD): ");
        int cant = leerEntero("Nueva cantidad de huespedes: ", 1, 100);
        h.modificarReserva(r, ini, fin, cant);
        out.println("Reserva actualizada. Nuevo costo total: " + r.getCostoTotal());
    }

    private void confirmarReserva(Huesped h) {
        Reservacion r = h.buscarReservacion(leer("ID de la reserva: "));
        if (r == null) {
            out.println("No se encontro la reserva.");
            return;
        }
        r.confirmar();
        out.println("Reserva confirmada (COMPLETADA).");
    }

    private void eliminarReserva(Huesped h) {
        out.println(h.eliminarReserva(leer("ID de la reserva: "))
                ? "Reserva eliminada." : "No se pudo eliminar (no existe o ya esta COMPLETADA).");
    }

    private void darResena(Huesped h) {
        Reservacion r = h.buscarReservacion(leer("ID de la reserva a resenar: "));
        if (r == null) {
            out.println("No se encontro la reserva.");
            return;
        }
        double cal = leerDecimal("Calificacion (1 a 5): ");
        String com = leer("Comentario: ");
        Resena rs = h.darResena(r, cal, com);
        out.println("Resena registrada: " + rs.getIdResena());
    }

    // ---------------- menu propietario ----------------
    private void menuPropietario(Propietario p) {
        boolean sesion = true;
        while (sesion) {
            out.println("\n=== MENU PROPIETARIO: " + p.getNombreUsuario().toUpperCase() + " ===");
            out.println("1. Gestionar perfil\n2. Buscar publicaciones\n3. Crear propiedad\n4. Crear publicacion\n"
                    + "5. Actualizar propiedad\n6. Modificar publicacion\n7. Eliminar propiedad\n"
                    + "8. Eliminar publicacion\n9. Generar reporte\n10. Mis propiedades y publicaciones\n11. Cerrar sesion");
            String opc = leer("Seleccione una opcion: ");
            try {
                switch (opc) {
                    case "1" -> perfil();
                    case "2" -> buscar(p);
                    case "3" -> crearPropiedad(p);
                    case "4" -> crearPublicacion(p);
                    case "5" -> actualizarPropiedad(p);
                    case "6" -> modificarPublicacion(p);
                    case "7" -> out.println(p.eliminarPropiedad(leer("ID de la propiedad: "))
                            ? "Propiedad eliminada." : "No se pudo eliminar (no existe o tiene publicacion activa).");
                    case "8" -> out.println(p.eliminarPublicacion(leer("ID de la publicacion: "))
                            ? "Publicacion eliminada." : "No se pudo eliminar (no existe o tiene reservas pendientes).");
                    case "9" -> reporte(p);
                    case "10" -> listarPropio(p);
                    case "11" -> {
                        gestor.cerrarSesion();
                        out.println("Sesion cerrada exitosamente.");
                        sesion = false;
                    }
                    default -> out.println("Por favor ingrese una opcion valida.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                out.println("No se pudo completar la operacion: " + e.getMessage());
            }
        }
    }

    private void crearPropiedad(Propietario p) {
        String ubicacion = leerNoVacio("Ubicacion: ");
        String descripcion = leerNoVacio("Descripcion: ");
        TipoPropiedad[] tipos = TipoPropiedad.values();
        for (int i = 0; i < tipos.length; i++) {
            out.println((i + 1) + ". " + tipos[i]);
        }
        TipoPropiedad tipo = tipos[leerEntero("Tipo de propiedad: ", 1, tipos.length) - 1];
        long precio = leerLong("Precio (mayor que 0): ");
        contPropiedad++;
        Propiedad prop = new Propiedad("PROP-" + contPropiedad, ubicacion, descripcion, precio, true, tipo);
        out.println(p.crearPropiedad(prop) ? "Propiedad creada: " + prop.getIdPropiedad() : "No se pudo crear la propiedad.");
    }

    private void crearPublicacion(Propietario p) {
        Propiedad prop = p.buscarPropiedad(leer("ID de la propiedad a publicar: "));
        if (prop == null) {
            out.println("No se encontro la propiedad.");
            return;
        }
        String titulo = leerNoVacio("Titulo: ");
        contPublicacion++;
        Publicacion pub = new Publicacion("PUB-" + contPublicacion, titulo, LocalDate.now(), true, prop);
        out.println(p.crearPublicacion(pub) ? "Publicacion creada: " + pub.getIdPublicacion() : "No se pudo crear la publicacion.");
    }

    private void actualizarPropiedad(Propietario p) {
        Propiedad prop = p.buscarPropiedad(leer("ID de la propiedad: "));
        if (prop == null) {
            out.println("No se encontro la propiedad.");
            return;
        }
        int op = leerEntero("1. Ubicacion y descripcion  2. Precio y disponibilidad: ", 1, 2);
        if (op == 1) {
            prop.actualizarDatos(leerNoVacio("Nueva ubicacion: "), leerNoVacio("Nueva descripcion: "));
        } else {
            long precio = leerLong("Nuevo precio: ");
            int disp = leerEntero("Disponible? (1. Si, 2. No): ", 1, 2);
            prop.actualizarDisponibilidad(disp == 1, precio);
        }
        out.println("Propiedad actualizada.");
    }

    private void modificarPublicacion(Propietario p) {
        Publicacion pub = p.buscarPublicacionPropia(leer("ID de la publicacion: "));
        if (pub == null) {
            out.println("No se encontro la publicacion.");
            return;
        }
        int op = leerEntero("1. Cambiar titulo  2. Activar/Inactivar: ", 1, 2);
        if (op == 1) {
            pub.modificarPublicacion(leerNoVacio("Nuevo titulo: "));
        } else {
            pub.cambiarEstado();
        }
        out.println("Publicacion actualizada. Estado: " + (pub.isEstadoPublicacion() ? "Activa" : "Inactiva"));
    }

    private void reporte(Propietario p) {
        out.println(reportes.generarReportePropietario(p));
        try {
            File f = reportes.exportarReporteArchivo(p);
            out.println("Reporte guardado en: " + f.getPath());
        } catch (IOException e) {
            out.println("No se pudo guardar el reporte: " + e.getMessage());
        }
    }

    private void listarPropio(Propietario p) {
        ArrayList<Propiedad> props = p.getPropiedades();
        ArrayList<Publicacion> pubs = p.getPublicaciones();
        out.println("Propiedades (" + props.size() + "):");
        for (Propiedad x : props) {
            out.println("  " + x.consultarPropiedad());
        }
        out.println("Publicaciones (" + pubs.size() + "):");
        for (Publicacion x : pubs) {
            out.println("  " + x.consultarPublicacion());
        }
    }

    // ---------------- comunes ----------------
    private void perfil() {
        Usuario u = gestor.getUsuario();
        out.println("Perfil: " + u);
        if (leerEntero("Modificar datos? (1. Si, 2. No): ", 1, 2) == 1) {
            boolean ok = gestor.gestionarPerfil(leer("Nuevo nombre: "), leer("Nuevo correo: "), leer("Nuevo telefono: "));
            out.println(ok ? "Perfil actualizado." : "Ningun campo puede quedar vacio. No se hicieron cambios.");
        }
    }

    private ArrayList<Publicacion> catalogo() {
        ArrayList<Publicacion> cat = new ArrayList<>();
        for (Usuario u : gestor.getUsers()) {
            if (u instanceof Propietario) {
                cat.addAll(((Propietario) u).getPublicaciones());
            }
        }
        return cat;
    }

    private void buscar(Usuario u) {
        String criterio = leer("Criterio de busqueda (Enter para ver todas): ");
        ArrayList<Publicacion> res = u.buscarPublicacion(criterio, catalogo());
        if (res.isEmpty()) {
            out.println("No se encontraron publicaciones.");
            return;
        }
        for (Publicacion pub : res) {
            out.println(pub.getIdPublicacion() + " | " + pub.getTituloPublicacion() + " | "
                    + (pub.getPropiedad() == null ? "-" : pub.getPropiedad().getTipoPropiedad()));
        }
        String id = leer("ID para ver el detalle (Enter para volver): ");
        if (!id.isEmpty()) {
            ArrayList<Publicacion> detalle = u.buscarPublicacion(id, res);
            out.println(detalle.isEmpty() ? "No se encontro esa publicacion." : detalle.get(0).consultarPublicacion());
        }
    }

    private Publicacion pedirPublicacion() {
        ArrayList<Publicacion> res = gestor.getUsuario().buscarPublicacion(leer("ID de la publicacion: "), catalogo());
        if (res.isEmpty()) {
            out.println("No se encontro la publicacion o esta inactiva.");
            return null;
        }
        return res.get(0);
    }

    // ---------------- lectura segura (RNF04) ----------------
    private String leer(String mensaje) {
        out.print(mensaje);
        return in.nextLine().trim();
    }

    private String leerNoVacio(String mensaje) {
        String s = leer(mensaje);
        while (s.isEmpty()) {
            out.println("Este campo no puede estar vacio.");
            s = leer(mensaje);
        }
        return s;
    }

    private int leerEntero(String mensaje, int min, int max) {
        while (true) {
            try {
                int v = Integer.parseInt(leer(mensaje));
                if (v >= min && v <= max) {
                    return v;
                }
                out.println("Ingrese un numero entre " + min + " y " + max + ".");
            } catch (NumberFormatException e) {
                out.println("Por favor ingrese un numero valido.");
            }
        }
    }

    private long leerLong(String mensaje) {
        while (true) {
            try {
                return Long.parseLong(leer(mensaje));
            } catch (NumberFormatException e) {
                out.println("Por favor ingrese un valor numerico valido.");
            }
        }
    }

    private double leerDecimal(String mensaje) {
        while (true) {
            try {
                return Double.parseDouble(leer(mensaje).replace(',', '.'));
            } catch (NumberFormatException e) {
                out.println("Por favor ingrese un numero valido.");
            }
        }
    }

    private LocalDate leerFecha(String mensaje) {
        while (true) {
            try {
                return LocalDate.parse(leer(mensaje));
            } catch (DateTimeParseException e) {
                out.println("Formato de fecha invalido. Use AAAA-MM-DD.");
            }
        }
    }
}
