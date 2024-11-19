package tarea_12;

import java.sql.Connection;
import java.util.Scanner;

/**
 * Clase para gestionar el menú principal de la aplicación.
 */
public class Menu {

    private static final Scanner sc = new Scanner(System.in);
    private GestorAlumnos gestorAlumnos;

    /**
     * Constructor de la clase Menu.
     */
    public Menu() {
        this.gestorAlumnos = new GestorAlumnos();
    }

    /**
     * Muestra el menú principal y permite seleccionar una opción.
     */
    public void mostrarMenu() {
        int opcion;

        do {
            System.out.println("---- Menú Principal ----");
            System.out.println("1. Insertar nuevo alumno");
            System.out.println("2. Salir");
            System.out.println("-------------------------");
            System.out.print("Selecciona una opción: ");

            try {
                opcion = sc.nextInt();
                sc.nextLine(); // Limpiar buffer

                switch (opcion) {
                    case 1:
                        insertarNuevoAlumno();
                        break;
                    case 2:
                        System.out.println("Saliendo del programa...");
                        break;
                    default:
                        System.out.println("Opción no válida. Intenta de nuevo.");
                }
            } catch (Exception e) {
                System.out.println("Entrada no válida. Por favor, introduce un número.");
                sc.nextLine(); // Limpiar el buffer en caso de error
                opcion = 0; // Reiniciar la opción para evitar salir del bucle
            }
        } while (opcion != 2);
    }

    /**
     * Permite insertar un nuevo alumno solicitando los datos al usuario y
     * almacenándolos en la base de datos.
     */
    private void insertarNuevoAlumno() {
        Connection conexionBD = null;

        try {
            Alumno alumno = gestorAlumnos.solicitarDatosAlumno();
            conexionBD = ConexionBDMySQL.getConexion();

            if (gestorAlumnos.insertarAlumno(conexionBD, alumno)) {
                System.out.println("Alumno insertado correctamente.");
            } else {
                System.out.println("Error al insertar el alumno.");
            }
        } catch (Exception e) {
            System.out.println("Ocurrió un error al insertar el alumno: " + e.getMessage());
        } finally {
            try {
                if (conexionBD != null && !conexionBD.isClosed()) {
                    conexionBD.close();
                    System.out.println("Conexión a la base de datos cerrada.");
                }
            } catch (Exception e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}
