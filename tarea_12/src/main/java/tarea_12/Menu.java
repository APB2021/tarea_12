package tarea_12;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Menu {

    private static final Scanner sc = new Scanner(System.in);
    private static Connection conexionBD = null;

    // Método para mostrar el menú de opciones
    public static void mostrarMenu() {
        System.out.println("Menú:");
        System.out.println("1. Insertar nuevo alumno");
        System.out.println("2. Ver lista de alumnos");
        System.out.println("3. Ver lista de grupos");
        System.out.println("4. Salir");
    }

    // Método para gestionar las opciones del menú
    public static void gestionarMenu() {
        int opcion;
        boolean salir = false;

        // Establecer la conexión a la base de datos al inicio del menú
        conexionBD = ConexionBDMySQL.getConexion();
        
        // Asegurarse de que la conexión se haya establecido correctamente
        if (conexionBD == null) {
            System.out.println("Error al establecer la conexión a la base de datos.");
            return;  // Salir del método si no hay conexión
        } else {
            System.out.println("Conexión a la base de datos establecida correctamente.");
        }

        // Bucle principal del menú
        while (!salir) {
            mostrarMenu();
            System.out.print("Seleccione una opción: ");
            opcion = sc.nextInt();
            sc.nextLine();  // Limpiar el buffer del scanner

            switch (opcion) {
                case 1:
                    insertarAlumno();
                    break;
                case 2:
                    mostrarAlumnos();
                    break;
                case 3:
                    mostrarGrupos();
                    break;
                case 4:
                    System.out.println("Saliendo...");
                    salir = true;
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
        }

        // Cerrar la conexión cuando el usuario salga
        try {
            if (conexionBD != null && !conexionBD.isClosed()) {
                conexionBD.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }

    // Método para insertar un nuevo alumno (ejemplo de operación)
    private static void insertarAlumno() {
        System.out.println("Insertar un nuevo alumno");
        // Lógica para insertar un nuevo alumno en la base de datos
    }

    // Método para mostrar los alumnos (ejemplo de operación)
    private static void mostrarAlumnos() {
        System.out.println("Mostrar lista de alumnos");
        // Lógica para mostrar los alumnos desde la base de datos
    }

    // Método para mostrar los grupos (ejemplo de operación)
    private static void mostrarGrupos() {
        System.out.println("Mostrar lista de grupos");
        // Lógica para mostrar los grupos desde la base de datos
    }
}
