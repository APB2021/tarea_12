package tarea_12;

import java.util.Scanner;

public class Menu {

    private static final Scanner sc = new Scanner(System.in);

    // Método para mostrar el menú de opciones
    public static void mostrarMenu() {
        System.out.println("Menú:");
        System.out.println("1. Insertar nuevo alumno");
        System.out.println("2. Ver lista de alumnos");
        System.out.println("3. Ver lista de grupos");
        System.out.println("4. Salir");
    }

    // Método para manejar las opciones del menú
    public static void gestionarMenu() {
        int opcion;
        boolean salir = false;

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
    }

    // Método para insertar un nuevo alumno (ejemplo de operación)
    private static void insertarAlumno() {
        // Lógica para insertar un nuevo alumno
        System.out.println("Insertar un nuevo alumno");
        // Aquí podrías pedir los datos al usuario y luego insertar el alumno en la base de datos
        // Por ejemplo:
        // Alumno alumno = new Alumno(...);
        // Llamar a un método para insertar el alumno en la base de datos
    }

    // Método para mostrar los alumnos (ejemplo de operación)
    private static void mostrarAlumnos() {
        System.out.println("Mostrar lista de alumnos");
        // Aquí podrías obtener los alumnos de la base de datos y mostrarlos
        // Por ejemplo:
        // List<Alumno> alumnos = obtenerAlumnosDeBD();
        // Imprimir la lista de alumnos
    }

    // Método para mostrar los grupos (ejemplo de operación)
    private static void mostrarGrupos() {
        System.out.println("Mostrar lista de grupos");
        // Aquí podrías obtener los grupos de la base de datos y mostrarlos
        // Por ejemplo:
        // List<Grupo> grupos = obtenerGruposDeBD();
        // Imprimir la lista de grupos
    }
}
