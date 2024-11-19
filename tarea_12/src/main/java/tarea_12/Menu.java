package tarea_12;

import java.sql.Connection;
import java.util.Scanner;

/**
 * Clase para gestionar el menú principal de la aplicación.
 */
public class Menu {

	private final Scanner sc = new Scanner(System.in);
	private final GestorAlumnos gestorAlumnos = new GestorAlumnos();
	private final GestorGrupos gestorGrupos = new GestorGrupos();

	/**
	 * Muestra el menú principal y permite seleccionar una opción.
	 */
	public void mostrarMenu() {
		int opcion;

		do {
			System.out.println("---- Menú Principal ----");
			System.out.println("1. Insertar nuevo alumno");
			System.out.println("2. Insertar nuevo grupo");
			System.out.println("3. Mostrar todos los alumnos");
			System.out.println("0. Salir");
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
					insertarNuevoGrupo();
					break;
				case 3:
					mostrarTodosLosAlumnos();
					break;
				case 0:
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
		} while (opcion != 0); // Modificado para que salga correctamente con la opción 0
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

	/**
	 * Permite insertar un nuevo grupo solicitando los datos al usuario y
	 * almacenándolos en la base de datos.
	 */
	private void insertarNuevoGrupo() {
		Connection conexionBD = null;
		String nombreGrupo;

		try {
			// Pedimos al usuario el nombre del nuevo grupo con validación
			while (true) {
				System.out.println("Introduce el nombre del nuevo grupo (una letra):");
				nombreGrupo = sc.nextLine().toUpperCase().trim();

				// Validamos que el nombre sea solo una letra
				if (nombreGrupo.length() == 1 && nombreGrupo.matches("[A-Za-z]")) {
					break; // Salimos del bucle si la validación es exitosa
				} else {
					System.out.println("El nombre del grupo debe ser una sola letra.");
				}
			}

			// Crear objeto Grupo
			Grupo grupo = new Grupo(nombreGrupo);

			// Obtener la conexión a la base de datos
			conexionBD = ConexionBDMySQL.getConexion();

			// Intentamos insertar el nuevo grupo
			if (gestorGrupos.insertarGrupo(conexionBD, grupo)) {
				System.out.println("Grupo insertado correctamente.");
			} else {
				System.out.println("Error al insertar el grupo.");
			}
		} catch (Exception e) {
			System.out.println("Ocurrió un error al insertar el grupo: " + e.getMessage());
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

	private void mostrarTodosLosAlumnos() {
		try (Connection conexionBD = ConexionBDMySQL.getConexion()) {
			if (gestorAlumnos.mostrarTodosLosAlumnos(conexionBD)) {
				System.out.println("Los alumnos se han mostrado correctamente.");
			} else {
				System.out.println("No se pudieron mostrar los alumnos.");
			}
		} catch (Exception e) {
			System.out.println("Ocurrió un error al mostrar los alumnos: " + e.getMessage());
		}
	}

}
