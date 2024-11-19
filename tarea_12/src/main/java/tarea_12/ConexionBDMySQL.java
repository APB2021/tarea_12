package tarea_12;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBDMySQL {

	// Datos de conexión a la base de datos
	private static final String URL = "jdbc:mysql://localhost:3306/Alumnos24";
	private static final String USER = "tarea_12";
	private static final String PASSWORD = "tarea_12";

	// Método para establecer la conexión
	public static Connection getConnection() throws SQLException {
		try {
			// Cargar el driver de MySQL
			Class.forName("com.mysql.cj.jdbc.Driver");
			// Establecer la conexión
			return DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (ClassNotFoundException e) {
			System.out.println("Error al cargar el driver de MySQL: " + e.getMessage());
			throw new SQLException("No se pudo conectar con la base de datos.");
		}
	}

	// Método para cerrar la conexión
	public static void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				System.out.println("Error al cerrar la conexión: " + e.getMessage());
			}
		}
	}
}
