package tarea_12;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBDMySQL {

	// Datos de conexión
	private static final String URL = "jdbc:mysql://localhost:3306/Alumnos24";
	private static final String USUARIO = "tarea_12";
	private static final String CONTRASENA = "tarea_12";

	// Método para obtener la conexión
	public static Connection getConexion() {
		Connection conexion = null;
		try {
			// Intentar establecer la conexión a la base de datos
			conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
		} catch (SQLException e) {
			// Si ocurre un error, mostrar un mensaje con la excepción
			System.out.println("Error al conectar a la base de datos: " + e.getMessage());
		} finally {
			// Aquí podrías hacer algo si es necesario (como liberar recursos) aunque no se
			// utilizarán en este caso
		}
		return conexion;
	}
}
