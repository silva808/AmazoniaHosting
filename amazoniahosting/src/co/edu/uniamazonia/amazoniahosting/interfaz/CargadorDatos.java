package co.edu.uniamazonia.amazoniahosting.interfaz;

import co.edu.uniamazonia.amazoniahosting.mundo.GestionUsuario;
import co.edu.uniamazonia.amazoniahosting.mundo.Huesped;
import co.edu.uniamazonia.amazoniahosting.mundo.Propiedad;
import co.edu.uniamazonia.amazoniahosting.mundo.Propietario;
import co.edu.uniamazonia.amazoniahosting.mundo.Publicacion;
import co.edu.uniamazonia.amazoniahosting.mundo.TipoPropiedad;
import java.time.LocalDate;

/** Precarga estructurada de datos de prueba (Seccion 5.2 del informe). */
public class CargadorDatos {

    private CargadorDatos() {
    }

    /** Carga 2 usuarios, 3 propiedades y 3 publicaciones. Retorna cuantas publicaciones creo. */
    public static int cargar(GestionUsuario gestor) {
        Propietario pepito = new Propietario("1000000000", "Pepito Perez", "pepito@perez.co", "3100000000", "1234");
        Huesped benito = new Huesped("3000000000", "Benito Velasquez", "beni@velasquez.co", "3300000000", "1234");
        gestor.registrarse(pepito);
        gestor.registrarse(benito);

        Propiedad p1 = new Propiedad("PROP-1", "Florencia, barrio Centro", "Hostal con 6 habitaciones y wifi",
                80000, true, TipoPropiedad.HOSTAL);
        Propiedad p2 = new Propiedad("PROP-2", "Florencia, via al aeropuerto", "Cabana con zona de hamacas",
                150000, true, TipoPropiedad.CABANA);
        Propiedad p3 = new Propiedad("PROP-3", "Florencia, barrio Siete de Agosto", "Casa en venta de dos pisos",
                320000000, true, TipoPropiedad.VIVIENDA_EN_VENTA);
        pepito.crearPropiedad(p1);
        pepito.crearPropiedad(p2);
        pepito.crearPropiedad(p3);

        int creadas = 0;
        Propiedad[] props = {p1, p2, p3};
        String[] titulos = {"Hostal Amazonico Centro", "Cabana Rio Hacha", "Casa en venta Siete de Agosto"};
        for (int i = 0; i < props.length; i++) {
            Publicacion pub = new Publicacion("PUB-" + (i + 1), titulos[i], LocalDate.now(), true, props[i]);
            if (pepito.crearPublicacion(pub)) {
                creadas++;
            }
        }
        return creadas;
    }
}
