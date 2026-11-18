package co.edu.uniamazonia.amazoniahosting.mundo;

import java.util.ArrayList;

public class Propietario extends Usuario {

    private ArrayList<Propiedad> propiedades;
    private ArrayList<Publicacion> publicaciones;

    public Propietario() {
        super();
        this.propiedades = new ArrayList<>();
        this.publicaciones = new ArrayList<>();
    }

    public Propietario(String idUsuario, String nombreUsuario, String correoUsuario,
            String telefonoUsuario, String contrasena) {
        super(idUsuario, nombreUsuario, correoUsuario, telefonoUsuario, contrasena);
        this.propiedades = new ArrayList<>();
        this.publicaciones = new ArrayList<>();
    }

    @Override
    public String getRol() {
        return "PROPIETARIO";
    }

    /** RF-04: false si p es null o el id ya existe. */
    public boolean crearPropiedad(Propiedad p) {
        if (p == null || buscarPropiedad(p.getIdPropiedad()) != null) {
            return false;
        }
        propiedades.add(p);
        return true;
    }

    /** La propiedad de la publicacion debe pertenecer a este propietario. */
    public boolean crearPublicacion(Publicacion pub) {
        if (pub == null || pub.getPropiedad() == null || buscarPublicacionPropia(pub.getIdPublicacion()) != null) {
            return false;
        }
        if (buscarPropiedad(pub.getPropiedad().getIdPropiedad()) != pub.getPropiedad()) {
            return false;
        }
        publicaciones.add(pub);
        return true;
    }

    /** RF-09: false si no existe o tiene una publicacion activa asociada. */
    public boolean eliminarPropiedad(String id) {
        Propiedad p = buscarPropiedad(id);
        if (p == null) {
            return false;
        }
        for (Publicacion pub : publicaciones) {
            if (pub.getPropiedad() == p && pub.isEstadoPublicacion()) {
                return false;
            }
        }
        publicaciones.removeIf(pub -> pub.getPropiedad() == p);
        propiedades.remove(p);
        return true;
    }

    /** RF-09: false si no existe o tiene reservaciones PENDIENTES. */
    public boolean eliminarPublicacion(String id) {
        Publicacion pub = buscarPublicacionPropia(id);
        if (pub == null || pub.tieneReservacionesPendientes()) {
            return false;
        }
        publicaciones.remove(pub);
        return true;
    }

    public Propiedad buscarPropiedad(String id) {
        if (id == null) {
            return null;
        }
        for (Propiedad p : propiedades) {
            if (p.getIdPropiedad().equalsIgnoreCase(id.trim())) {
                return p;
            }
        }
        return null;
    }

    public Publicacion buscarPublicacionPropia(String id) {
        if (id == null) {
            return null;
        }
        for (Publicacion pub : publicaciones) {
            if (pub.getIdPublicacion().equalsIgnoreCase(id.trim())) {
                return pub;
            }
        }
        return null;
    }

    public boolean tieneActividadActiva() {
        for (Publicacion pub : publicaciones) {
            if (pub.isEstadoPublicacion()) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Propiedad> getPropiedades() {
        return new ArrayList<>(propiedades);
    }

    public ArrayList<Publicacion> getPublicaciones() {
        return new ArrayList<>(publicaciones);
    }
}
