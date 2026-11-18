# AmazoniaHosting (Entrega Final - Lógica y Algoritmos II)

Paquetes: `.mundo` (modelo, sin Scanner ni impresiones), `.interfaz` (consola), `.test` (pruebas).

## Cómo abrirlo en NetBeans
1. File > Open Project > carpeta `amazoniahosting` (usa JDK 17 o superior).
2. Ejecutar la app: clic derecho en `Principal.java` > Run File.
   Credenciales de prueba: 1000000000 (propietario) y 3000000000 (huésped), clave 1234.
3. Ejecutar pruebas de requerimientos: clic derecho en `PruebasRequerimientos.java` > Run File.
   (Ejecútelo con el proyecto abierto para que RNF01 y RNF10 encuentren la carpeta `src`.)
4. Matriz de 8 pruebas (Tabla 8): clic derecho en `MatrizPruebasConsola.java` > Run File.

## Por consola
    javac -encoding UTF-8 -d out $(find src -name "*.java")
    java -cp out co.edu.uniamazonia.amazoniahosting.test.PruebasRequerimientos
