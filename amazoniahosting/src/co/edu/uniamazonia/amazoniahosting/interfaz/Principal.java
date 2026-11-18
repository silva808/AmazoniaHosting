package co.edu.uniamazonia.amazoniahosting.interfaz;

import co.edu.uniamazonia.amazoniahosting.mundo.GestionUsuario;
import java.util.Scanner;

public class Principal {

    public static void main(String[] args) {
        GestionUsuario gestor = new GestionUsuario();
        CargadorDatos.cargar(gestor);
        Scanner scan = new Scanner(System.in);
        new ConsolaAmazoniaHosting(gestor, scan, System.out).ejecutar();
        scan.close();
    }
}
