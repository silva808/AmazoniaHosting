package amazoniahosting;

import co.edu.uniamazonia.amazoniahosting.mundo.TipoUsuario;
import co.edu.uniamazonia.amazoniahosting.mundo.GestionUsuario;
import co.edu.uniamazonia.amazoniahosting.mundo.Usuario;
import java.util.Scanner;

public class Amazoniahosting {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        boolean app = true;

        // 1. Instanciar el gestor ÚNICO que mantendrá la lista de usuarios en memoria
        GestionUsuario gestor = new GestionUsuario();

        // 2. Usuarios de prueba cargados directamente en el gestor
        Usuario user1 = new Usuario("1000000000", "Pepito Perez", "pepito@perez", "3100000000", TipoUsuario.PROPIETARIO);
        Usuario user2 = new Usuario("2000000000", "Juanito Alcachofa", "alcachofa@juanin", "3200000000", TipoUsuario.RESIDENTE);
        Usuario user3 = new Usuario("3000000000", "Benito Velasquez", "beni@vez", "3300000000", TipoUsuario.TURISTA);

        gestor.getUsers().add(user1);
        gestor.getUsers().add(user2);
        gestor.getUsers().add(user3);

        // 3. Bucle principal de la aplicación
        while (app) {
            System.out.println("\n----- AMAZONIA HOSTING -----");
            System.out.println("Bienvenido. ¿Qué desea hacer?\n1. Registrarse\n2. Iniciar Sesión\n3. Salir");
            System.out.print("Seleccione una opción: ");
            String opc = scan.nextLine().trim();

            switch (opc) {
                case "1" -> {
                    // Usamos la misma instancia del gestor
                    gestor.registrarse();
                }
                case "2" -> {
                    // Usamos la misma instancia para autenticar
                    Usuario usuarioLogueado = gestor.iniciarSesion();

                    // Si el login fue exitoso, desplegamos el menú para ese usuario
                    if (usuarioLogueado != null) {
                        desplegarMenuUsuario(gestor, usuarioLogueado, scan);
                    }
                }
                case "3" -> {
                    System.out.println("Gracias por usar Amazonia Hosting, ¡Vuelva pronto!");
                    app = false;
                }
                default -> System.out.println("Por favor ingrese una opción válida.");
            }
        }
    }

    // Método auxiliar estático para controlar el flujo de la terminal según el rol
    private static void desplegarMenuUsuario(GestionUsuario gestor, Usuario usuario, Scanner scan) {
        boolean enSesion = true;

        while (enSesion) {
            System.out.println("\n=================================");
            System.out.println("MENÚ USUARIO: " + usuario.getNombreUsuario().toUpperCase());
            System.out.println("ROL: " + usuario.getTipoUsuario());
            System.out.println("=================================");

            System.out.println("1. Gestionar Perfil");
            System.out.println("2. Buscar Publicaciones");

            // Opciones dinámicas según el tipo de usuario (enum)
            switch (usuario.getTipoUsuario()) {
                case PROPIETARIO -> {
                    System.out.println("3. Crear Publicación");
                    System.out.println("4. Modificar Propiedad");
                    System.out.println("5. Eliminar Publicación");
                    System.out.println("6. Modificar Publicación");
                    System.out.println("7. Cerrar Sesión");
                }
                case TURISTA -> {
                    System.out.println("3. Reservar Alojamiento");
                    System.out.println("4. Modificar Reserva");
                    System.out.println("5. Eliminar Reserva");
                    System.out.println("6. Cerrar Sesión");
                }
                case RESIDENTE -> {
                    //System.out.println("3. Consultar Propiedad Detallada");
                    System.out.println("3. Cerrar Sesión");
                }
            }

            System.out.print("Seleccione una opción: ");
            String opc = scan.nextLine().trim();

            // Ejecución de acciones en las entidades correspondientes del UML
            if (opc.equals("1")) {
                gestor.gestionarPerfil();
            } else if (opc.equals("2")) {
                //usuario.buscarPublicacion();
            } else if (usuario.getTipoUsuario() == TipoUsuario.PROPIETARIO) {
                switch (opc) {
                    case "3" -> usuario.crearPropiedad();
                    case "4" -> usuario.modificarPropiedad();
                    case "5" -> usuario.crearPublicacion();
                    case "6" -> usuario.modificarPublicacion();
                    /*case "5" -> usuario.eliminarPublicacion();*/
                }
            } else if (usuario.getTipoUsuario() == TipoUsuario.TURISTA) {
                switch (opc) {
                    /*case "3" -> usuario.reservarAlojamiento();
                    case "4" -> usuario.modificarReserva();
                    case "5" -> usuario.eliminarReserva();*/
                }
            } /*else if (usuario.getTipoUsuario() == TipoUsuario.RESIDENTE) {
                if (opc.equals("3")) {
                    //usuario.consultarPropiedad();
                }
            }*/

            // Evaluar el cierre de sesión
            if ((usuario.getTipoUsuario() == TipoUsuario.PROPIETARIO && opc.equals("7"))
                    || (usuario.getTipoUsuario() == TipoUsuario.TURISTA && opc.equals("6"))
                    || (usuario.getTipoUsuario() == TipoUsuario.RESIDENTE && opc.equals("3"))) {

                System.out.println("Sesión cerrada exitosamente.");
                gestor.setUsuario(null); // Limpiar sesión activa
                enSesion = false;
            }
        }
    }
}