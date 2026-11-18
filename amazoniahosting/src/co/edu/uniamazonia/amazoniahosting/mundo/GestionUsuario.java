package co.edu.uniamazonia.amazoniahosting.mundo;

import java.util.ArrayList;

public class GestionUsuario {

    private ArrayList<Usuario> users;
    private Usuario usuario;

    public GestionUsuario() {
        this.users = new ArrayList<>();
        this.usuario = null;
    }

    /** RF-01: false si es null, el id ya existe o el objeto es un Usuario generico sin rol. */
    public boolean registrarse(Usuario nuevo) {
        if (nuevo == null || existeUsuario(nuevo.getIdUsuario())) {
            return false;
        }
        if (!(nuevo instanceof Huesped) && !(nuevo instanceof Propietario)) {
            return false;
        }
        users.add(nuevo);
        return true;
    }

    /** RF-02: retorna el usuario autenticado o null si id/contrasena no coinciden. */
    public Usuario iniciarSesion(String idUsuario, String contrasena) {
        if (idUsuario == null || contrasena == null) {
            return null;
        }
        for (Usuario u : users) {
            if (u.getIdUsuario().equalsIgnoreCase(idUsuario.trim()) && u.getContrasena().equals(contrasena)) {
                this.usuario = u;
                return u;
            }
        }
        return null;
    }

    public void cerrarSesion() {
        this.usuario = null;
    }

    /** Requiere sesion activa; valida que ningun campo quede vacio antes de modificar. */
    public boolean gestionarPerfil(String nombre, String correo, String telefono) {
        if (usuario == null || esVacio(nombre) || esVacio(correo) || esVacio(telefono)) {
            return false;
        }
        usuario.setNombreUsuario(nombre);
        usuario.setCorreoUsuario(correo);
        usuario.setTelefonoUsuario(telefono);
        return true;
    }

    /** RF-09: false si no existe o tiene actividad vigente (publicaciones activas / reservas pendientes). */
    public boolean eliminarUsuario(String id) {
        Usuario u = buscarUsuario(id);
        if (u == null) {
            return false;
        }
        if (u instanceof Propietario && ((Propietario) u).tieneActividadActiva()) {
            return false;
        }
        if (u instanceof Huesped) {
            for (Reservacion r : ((Huesped) u).getReservaciones()) {
                if (r.getEstado() == EstadoReservacion.PENDIENTE) {
                    return false;
                }
            }
        }
        if (u == usuario) {
            usuario = null;
        }
        users.remove(u);
        return true;
    }

    public boolean existeUsuario(String id) {
        return buscarUsuario(id) != null;
    }

    public Usuario buscarUsuario(String id) {
        if (id == null) {
            return null;
        }
        for (Usuario u : users) {
            if (u.getIdUsuario().equalsIgnoreCase(id.trim())) {
                return u;
            }
        }
        return null;
    }

    private static boolean esVacio(String s) {
        return s == null || s.trim().isEmpty();
    }

    public ArrayList<Usuario> getUsers() {
        return new ArrayList<>(users);
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
