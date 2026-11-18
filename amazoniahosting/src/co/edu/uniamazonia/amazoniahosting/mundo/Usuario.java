package co.edu.uniamazonia.amazoniahosting.mundo;

import java.util.ArrayList;

/**
 * Clase padre de la jerarquia de usuarios (Huesped y Propietario).
 * No contiene lecturas por teclado ni impresiones (RNF01).
 */
public class Usuario {

    private String idUsuario;
    private String nombreUsuario;
    private String correoUsuario;
    private String telefonoUsuario;
    private String contrasena;

    public Usuario() {
        this.idUsuario = "noId";
        this.nombreUsuario = "noName";
        this.correoUsuario = "noEmail";
        this.telefonoUsuario = "noPhoneNumber";
        this.contrasena = "noPassword";
    }

    public Usuario(String idUsuario, String nombreUsuario, String correoUsuario,
            String telefonoUsuario, String contrasena) {
        this.idUsuario = exigirTexto(idUsuario, "El id del usuario");
        this.nombreUsuario = exigirTexto(nombreUsuario, "El nombre");
        this.correoUsuario = exigirTexto(correoUsuario, "El correo");
        this.telefonoUsuario = exigirTexto(telefonoUsuario, "El telefono");
        this.contrasena = exigirTexto(contrasena, "La contrasena");
    }

    /** Rol del usuario. Se sobreescribe en las subclases (polimorfismo). */
    public String getRol() {
        return "USUARIO";
    }

    /**
     * RF-05: filtra el catalogo recibido y devuelve las publicaciones ACTIVAS
     * cuyo id, titulo o tipo de propiedad coincide con el criterio.
     * Criterio vacio o nulo devuelve todas las activas. Nunca retorna null.
     */
    public ArrayList<Publicacion> buscarPublicacion(String criterio, ArrayList<Publicacion> catalogo) {
        ArrayList<Publicacion> resultado = new ArrayList<>();
        if (catalogo == null) {
            return resultado;
        }
        String buscado = (criterio == null) ? "" : criterio.trim().toLowerCase();
        for (Publicacion pub : catalogo) {
            if (pub == null || !pub.isEstadoPublicacion()) {
                continue;
            }
            boolean coincide = buscado.isEmpty()
                    || pub.getIdPublicacion().equalsIgnoreCase(buscado)
                    || pub.getTituloPublicacion().toLowerCase().contains(buscado);
            if (!coincide && pub.getPropiedad() != null && pub.getPropiedad().getTipoPropiedad() != null) {
                String tipo = pub.getPropiedad().getTipoPropiedad().name().toLowerCase().replace('_', ' ');
                coincide = tipo.contains(buscado);
            }
            if (coincide) {
                resultado.add(pub);
            }
        }
        return resultado;
    }

    /** No incluye la contrasena (RNF07). */
    @Override
    public String toString() {
        return getRol() + " " + idUsuario + " - " + nombreUsuario + " (" + correoUsuario + ", " + telefonoUsuario + ")";
    }

    private static String exigirTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " no puede estar vacio.");
        }
        return valor.trim();
    }

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

    public String getContrasena() {
        return contrasena;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = exigirTexto(idUsuario, "El id del usuario");
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = exigirTexto(nombreUsuario, "El nombre");
    }

    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = exigirTexto(correoUsuario, "El correo");
    }

    public void setTelefonoUsuario(String telefonoUsuario) {
        this.telefonoUsuario = exigirTexto(telefonoUsuario, "El telefono");
    }

    public void setContrasena(String contrasena) {
        this.contrasena = exigirTexto(contrasena, "La contrasena");
    }
}
