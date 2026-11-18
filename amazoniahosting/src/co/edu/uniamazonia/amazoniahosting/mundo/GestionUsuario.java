package co.edu.uniamazonia.amazoniahosting.mundo;

import java.util.ArrayList;
import java.util.Scanner;

public class GestionUsuario {

    private ArrayList<Usuario> users;
    private Usuario usuario;

    private static Scanner scan = new Scanner(System.in);

    public GestionUsuario() {
        this.users = new ArrayList<>();
        this.usuario = null;
    }

    public GestionUsuario(ArrayList<Usuario> users) {
        this.users = users;
        this.usuario = null;
    }

    public void registrarse() {
        //declaracion de variables
        String newId = "", newName, newEmail, newPhone, newType = "";
        boolean aux = true;
        boolean idValid = false;

        //formulario
        System.out.println("--- Registro Usuario ---");
        while (!idValid) {
            System.out.print("Ingrese su numero de identificación: ");
            newId = scan.nextLine().trim();

            if (newId.isEmpty()) {
                System.out.println("Pr favor ingresar un numero de identificacion");
            } else if (existeUsuario(newId)) {
                System.out.println("El número de identificación " + newId + " ya está registrado.");
            } else {
                idValid = true;
            }
        }

        System.out.print("Ingrese su Nombre Completo: ");
        newName = scan.nextLine().trim();
        System.out.print("Ingrese su Correo Electronico: ");
        newEmail = scan.nextLine().trim();
        System.out.print("Ingrese su numero de telefono: ");
        newPhone = scan.nextLine().trim();
        System.out.print("""
                         Que tipo de usuario es usted? (Ingrese el numero de la opcion)
                         1. Turista 
                         2. Residente 
                         3. Propietario""");
        // Tipos de usuarios permitidos
        while (aux) {
            String newTypeAux = scan.next();
            switch (newTypeAux) {
                case "1" -> {
                    newType = "TURISTA";
                    aux = false;
                }
                case "2" -> {
                    newType = "RESIDENTE";
                    aux = false;
                }
                case "3" -> {
                    newType = "PROPIETARIO";
                    aux = false;
                }
                default -> {
                    System.out.println("Por favor elegir una opcion valida");
                }
            }

        }
        if (!newId.isEmpty() && !newName.isEmpty() && !newEmail.isEmpty() && !newPhone.isEmpty() && !newType.isEmpty()) {
            Usuario user = new Usuario(newId, newName, newEmail, newPhone, TipoUsuario.valueOf(newType));
            users.add(user);
            System.out.println("Usuario registrado exitosamente");
        }
        scan.nextLine();
    }

    public void gestionarPerfil() {// Validar que exista una sesión activa
        if (this.usuario == null) {
            System.out.println("Error: Debe iniciar sesión antes de gestionar su perfil.");
            return;
        }

        boolean aux = true;

        while (aux) { //Bucle para volver al inicio
            System.out.println("---Perfil--- ");
            System.out.println("Identificacion:     " + usuario.getIdUsuario());
            System.out.println("Nombre:     " + usuario.getNombreUsuario());
            System.out.println("Correo:     " + usuario.getCorreoUsuario());
            System.out.println("Telefono:   " + usuario.getTelefonoUsuario());
            System.out.println("\n¿Desea modificar sus datos?\n1.Si\n2.No");

            String option = scan.nextLine();

            switch (option) {
                case "1" -> {
                    String newNombre;
                    do {
                        System.out.print("Nuevo Nombre: ");
                        newNombre = scan.nextLine().trim();
                        if (newNombre.isEmpty()) {
                            System.out.println("El nombre no puede estar vacío.");
                        }
                    } while (newNombre.isEmpty());

                    String newCorreo;
                    do {
                        System.out.print("Nuevo Correo: ");
                        newCorreo = scan.nextLine().trim();
                        if (newCorreo.isEmpty()) {
                            System.out.println("El correo no puede estar vacío.");
                        }
                    } while (newCorreo.isEmpty());

                    String newTelefono;
                    do {
                        System.out.print("Nuevo Teléfono: ");
                        newTelefono = scan.nextLine().trim();
                        if (newTelefono.isEmpty()) {
                            System.out.println("El teléfono no puede estar vacío.");
                        }
                    } while (newTelefono.isEmpty());

                    // 1. Actualizamos el objeto actual (this)
                    usuario.setNombreUsuario(newNombre);
                    usuario.setCorreoUsuario(newCorreo);
                    usuario.setTelefonoUsuario(newTelefono);

                    System.out.println("¡Perfil y lista actualizados con éxito!");
                }
                case ("2") ->
                    aux = false;
                default ->
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }

    }

    public Usuario iniciarSesion() {
        System.out.println("\n--- Iniciar Sesión ---");
        System.out.print("Ingrese su ID de identificación: ");
        String idInput = scan.nextLine().trim();

        // Busca en el ArrayList estático 'users'
        for (Usuario u : users) {
            if (u.getIdUsuario().equalsIgnoreCase(idInput)) {
                System.out.println("¡Bienvenido de nuevo, " + u.getNombreUsuario() + "!");
                this.usuario = u;
                return u; // Retorna el objeto del usuario autenticado
            }
        }

        System.out.println("Error: El ID ingresado no está registrado.");
        return null; // Si no lo encuentra, retorna null
    }

    public boolean existeUsuario(String id) {
        for (Usuario u : users) {
            if (u.getIdUsuario().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Usuario> getUsers() {
        return users;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsers(ArrayList<Usuario> users) {
        this.users = users;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}
