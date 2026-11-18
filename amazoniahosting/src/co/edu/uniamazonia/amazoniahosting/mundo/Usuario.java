package co.edu.uniamazonia.amazoniahosting.mundo;

import java.util.ArrayList;
import java.util.Scanner;

public class Usuario {

    //Atributos
    private String idUsuario;
    private String nombreUsuario;
    private String correoUsuario;
    private String telefonoUsuario;
    private TipoUsuario tipoUsuario;

    private static ArrayList<Publicacion> publics = new ArrayList<>();
    private static ArrayList<Propiedad> propierts = new ArrayList<>();
    private static Scanner scan = new Scanner(System.in);

    //Metodos constructores
    public Usuario() {
        this.idUsuario = "noId";
        this.nombreUsuario = "noName";
        this.correoUsuario = "noEmail";
        this.telefonoUsuario = "noPhoneNumber";
    }

    public Usuario(String idUsuario, String nombreUsuario, String correoUsuario, String telefonoUsuario, TipoUsuario tipoUsuario) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.correoUsuario = correoUsuario;
        this.telefonoUsuario = telefonoUsuario;
        this.tipoUsuario = tipoUsuario;
    }

    //Metodos de Negocio
    public void buscarPublicacion() {
        if (publics == null || publics.isEmpty()) {
            System.out.println("\nNo hay publicaciones registradas en el sistema actualmente.");
            return;
        }

        System.out.println("--- PUBLICACIONES DISPONIBLES ---");
        System.out.println("\nID    |   Publicacion   |   Fecha de Publicacion");
        for (Publicacion pub : publics) {
            System.out.println(pub.getIdPublicacion() + "   |   " + pub.getTituloPublicacion() + "   |   " + pub.getFechaPublicacion());
        }

        System.out.println("\n¿Desea consultar el detalle de alguna publicación? \n1. Continuar \n2. Finalizar");
        String opc = scan.nextLine();

        if (!opc.equalsIgnoreCase("1")) {
            System.out.println("Búsqueda finalizada.");
            return;
        }

        System.out.print("Ingrese el ID de la publicación a buscar: ");
        String idIngresado = scan.nextLine().trim();

        if (idIngresado.isEmpty()) {
            System.out.println("Búsqueda finalizada.");
            return;
        }

        Publicacion publicacionEncontrada = null;
        for (Publicacion pub : publics) {
            if (pub.getIdPublicacion().equalsIgnoreCase(idIngresado)) {
                publicacionEncontrada = pub;
                break;
            }
        }

        if (publicacionEncontrada != null) {
            System.out.println("\n=================================");
            System.out.println("DETALLE DE LA PUBLICACIÓN");
            System.out.println("=================================");
            publicacionEncontrada.consultarPublicacion(); // Muestra todos los atributos
            System.out.println("=================================");
        } else {
            System.out.println("No se encontró ninguna publicación con el criterio: '" + idIngresado + "'.");
        }
    }

    public void crearPublicacion() {
        System.out.println("\n--- CREAR NUEVA PUBLICACIÓN ---");

        String titulo = "";
        while (titulo.isEmpty()) {
            System.out.print("Ingrese el título de la publicación: ");
            titulo = scan.nextLine().trim();
            if (titulo.isEmpty()) {
                System.out.println("El título no puede estar vacío. Por favor, ingréselo nuevamente.");
            }
        }

        String descripcion = "";
        while (descripcion.isEmpty()) {
            System.out.print("Ingrese la descripción de la publicación: ");
            descripcion = scan.nextLine().trim();
            if (descripcion.isEmpty()) {
                System.out.println("La descripción no puede estar vacía. Por favor, ingrésela nuevamente.");
            }
        }

        String fecha = "";
        while (fecha.isEmpty()) {
            System.out.print("Ingrese la fecha de la publicación (ej. DD-MM-AAAA): ");
            fecha = scan.nextLine().trim();
            if (fecha.isEmpty()) {
                System.out.println("La fecha no puede estar vacía. Por favor, ingrésela nuevamente.");
            }
        }

        Propiedad propEncontrada = null;
        while (propEncontrada == null) {
            System.out.println("\n--- SELECCIONE UNA PROPIEDAD PARA LA PUBLICACIÓN ---");
            System.out.println("ID Propiedad    |   Ubicación");
            for (Propiedad p : propierts) {
                System.out.println(p.getIdPropiedad() + "   |   " + p.getUbicacionPropiedad());
            }

            System.out.println("\nIngrese el ID de la propiedad: ");
            String idIngresado = scan.nextLine().trim();

            // 2. Buscar la propiedad en la lista
            propEncontrada = null;
            for (Propiedad p : propierts) {
                if (p.getIdPropiedad().equalsIgnoreCase(idIngresado)) {
                    propEncontrada = p;
                    break;
                }
            }

            if (propEncontrada == null) {
                System.out.println("No se encontró ninguna propiedad con ese ID.");
            }
        }

        Publicacion nuevaPublicacion = new Publicacion(titulo, descripcion, fecha, true, propEncontrada);
        publics.add(nuevaPublicacion);

        System.out.println("\n¡Publicación creada con éxito!");
        System.out.println("Se le ha asignado el ID: " + nuevaPublicacion.getIdPublicacion());
    }

    public void modificarPublicacion() {
        if (publics == null || publics.isEmpty()) {
            System.out.println("\nNo hay publicaciones registradas en el sistema para modificar.");
            return;
        }

        System.out.println("\n--- MODIFICAR PUBLICACIÓN ---");
        System.out.println("ID  |   Publicación");
        for (Publicacion pub : publics) {
            System.out.println(pub.getIdPublicacion() + "  |   " + pub.getTituloPublicacion());
        }

        System.out.print("\nIngrese el ID de la publicación que desea modificar (presione Enter para cancelar): ");
        String idIngresado = scan.nextLine().trim();

        if (idIngresado.isEmpty()) {
            System.out.println("Modificación cancelada.");
            return;
        }

        Publicacion publicacionEncontrada = null;
        for (Publicacion pub : publics) {
            if (pub.getIdPublicacion().equalsIgnoreCase(idIngresado)) {
                publicacionEncontrada = pub;
                break;
            }
        }

        if (publicacionEncontrada == null) {
            System.out.println("No se encontró ninguna publicación con el ID: '" + idIngresado + "'.");
            return;
        }

        int opcionMod = 0;
        while (opcionMod != 3) {
            System.out.println("\n--- EDITANDO: [" + publicacionEncontrada.getIdPublicacion() + "] " + publicacionEncontrada.getTituloPublicacion() + " ---");
            System.out.println("1. Actualizar Título y Descripción");
            System.out.println("2. Cambiar Estado (Activa/Inactiva)");
            System.out.println("3. Regresar al Menú Principal");
            System.out.print("Seleccione una opción: ");

            String entrada = scan.nextLine().trim();
            try {
                opcionMod = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
                continue;
            }

            switch (opcionMod) {
                case 1 -> {
                    // Pedir nuevos datos con validación básica
                    String nuevoTitulo = "";
                    while (nuevoTitulo.isEmpty()) {
                        System.out.print("Ingrese el nuevo título: ");
                        nuevoTitulo = scan.nextLine().trim();
                    }

                    String nuevaDesc = "";
                    while (nuevaDesc.isEmpty()) {
                        System.out.print("Ingrese la nueva descripción: ");
                        nuevaDesc = scan.nextLine().trim();
                    }

                    publicacionEncontrada.actualizarInformacion(nuevoTitulo, nuevaDesc);
                }

                case 2 ->
                    publicacionEncontrada.cambiarEstado();

                case 3 ->
                    System.out.println("Saliendo de la edición de esta publicación.");

                default ->
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }

    public void crearPropiedad() {
        System.out.println("\n--- CREAR NUEVA PROPIEDAD ---");

        String ubicacion = "";
        while (ubicacion.isEmpty()) {
            System.out.print("Ingrese la ubicación de la propiedad: ");
            ubicacion = scan.nextLine().trim();
            if (ubicacion.isEmpty()) {
                System.out.println("La ubicación no puede estar vacía. Intente nuevamente.");
            }
        }

        String descripcion = "";
        while (descripcion.isEmpty()) {
            System.out.print("Ingrese la descripción de la propiedad: ");
            descripcion = scan.nextLine().trim();
            if (descripcion.isEmpty()) {
                System.out.println("La descripción no puede estar vacía. Intente nuevamente.");
            }
        }

        TipoPropiedad tipoSeleccionado = null;
        while (tipoSeleccionado == null) {
            System.out.println("\n--- SELECCIONE EL TIPO DE PROPIEDAD ---");
            TipoPropiedad[] valores = TipoPropiedad.values();
            for (int i = 0; i < valores.length; i++) {
                System.out.println((i + 1) + ". " + valores[i]);
            }
            System.out.print("Seleccione una opción (1-" + valores.length + "): ");

            String entrada = scan.nextLine().trim();
            try {
                int opcionIndice = Integer.parseInt(entrada) - 1;
                if (opcionIndice >= 0 && opcionIndice < valores.length) {
                    tipoSeleccionado = valores[opcionIndice];
                } else {
                    System.out.println("Opción fuera de rango. Intente de nuevo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
            }
        }

        //Configuarcion de la disponibilidad
        System.out.println("\n--- CONFIGURACIÓN DE DISPONIBILIDAD INICIAL ---");

        String tipoDisp = "";
        while (tipoDisp.isEmpty()) {
            System.out.print("Ingrese el tipo de disponibilidad: ");
            tipoDisp = scan.nextLine().trim();
            if (tipoDisp.isEmpty()) {
                System.out.println("El tipo de disponibilidad no puede estar vacío.");
            }
        }

        long precio = 0;
        while (precio <= 0) {
            System.out.print("Ingrese el precio base: ");
            String entradaPrecio = scan.nextLine().trim();
            try {
                precio = Long.parseLong(entradaPrecio);
                if (precio <= 0) {
                    System.out.println("Error: El precio no permitido.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un valor numérico válido para el precio.");
            }
        }
        Disponibilidad nuevaDisponibilidad = new Disponibilidad(tipoDisp, precio, true);

        Propiedad nuevaPropiedad = new Propiedad(ubicacion, descripcion, tipoSeleccionado, this, nuevaDisponibilidad);
        propierts.add(nuevaPropiedad);

        System.out.println("\n¡Propiedad creada con éxito!");
        System.out.println("Se le ha asignado el ID: " + nuevaPropiedad.getIdPropiedad());
    }

    public void modificarPropiedad() {
        if (propierts == null || propierts.isEmpty()) {
            System.out.println("\nNo hay propiedades registradas en el sistema para modificar.");
            return;
        }

        System.out.println("\n--- MODIFICAR PROPIEDAD ---");
        System.out.println("ID Propiedad    |   Ubicación");
        for (Propiedad p : propierts) {
            System.out.println(p.getIdPropiedad() + "   |   " + p.getUbicacionPropiedad());
        }

        System.out.print("\nIngrese el ID de la propiedad que desea modificar (Enter para cancelar): ");
        String idIngresado = scan.nextLine().trim();

        if (idIngresado.isEmpty()) {
            System.out.println("Modificación cancelada.");
            return;
        }

        Propiedad propiedadEncontrada = null;
        for (Propiedad p : propierts) {
            if (p.getIdPropiedad().equalsIgnoreCase(idIngresado)) {
                propiedadEncontrada = p;
                break;
            }
        }

        if (propiedadEncontrada == null) {
            System.out.println("No se encontró ninguna propiedad con ese ID.");
            return;
        }

        int opcionMod = 0;
        while (opcionMod != 3) {
            System.out.println("\n--- EDITANDO PROPIEDAD: [" + propiedadEncontrada.getIdPropiedad() + "] ---");
            System.out.println("1. Actualizar Ubicación y Descripción");
            System.out.println("2. Actualizar Disponibilidad (Precio y Estado)");
            System.out.println("3. Regresar al Menú Principal");
            System.out.print("Seleccione una opción: ");

            String entrada = scan.nextLine().trim();
            try {
                opcionMod = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Por favor, ingrese un número válido.");
                continue;
            }

            switch (opcionMod) {
                case 1 -> {
                    String nuevaUbicacion = "";
                    while (nuevaUbicacion.isEmpty()) {
                        System.out.print("Ingrese la nueva ubicación: ");
                        nuevaUbicacion = scan.nextLine().trim();
                    }

                    String nuevaDesc = "";
                    while (nuevaDesc.isEmpty()) {
                        System.out.print("Ingrese la nueva descripción: ");
                        nuevaDesc = scan.nextLine().trim();
                    }

                    propiedadEncontrada.actualizarDatos(nuevaUbicacion, nuevaDesc);
                }
                case 2 -> {
                    Disponibilidad disp = propiedadEncontrada.getDisponibilidad();
                    
                    String nuevoTipo = "";
                    while (nuevoTipo.isEmpty()) {
                        System.out.print("Ingrese un nuevo tipo de disponibilidad: ");
                        nuevoTipo = scan.nextLine().trim();
                    }

                    long nuevoPrecio = 0;
                    while (nuevoPrecio <= 0) {
                        System.out.print("Ingrese el nuevo precio: ");
                        nuevoPrecio = scan.nextLong();
                        scan.nextLine();
                    }
                    
                    disp.actualizarDisponibilidad(nuevoTipo, nuevoPrecio);
                }
                case 3 ->
                    System.out.println("Saliendo de la edición de esta propiedad.");
                default ->
                    System.out.println("Opción no válida.");
            }
        }
    }

    /*Declaracion de metodos getters y setters*/
    public String getIdUsuario() {
        return idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getCorreoUsuario() {
        return correoUsuario;
    }

    public String getTelefonoUsuario() {
        return telefonoUsuario;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    public void setTelefonoUsuario(String telefonoUsuario) {
        this.telefonoUsuario = telefonoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

}
