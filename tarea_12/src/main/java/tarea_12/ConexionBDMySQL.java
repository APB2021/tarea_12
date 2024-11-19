package tarea_12;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase para gestionar la conexión a la base de datos MySQL.
 */
public class ConexionBDMySQL {

    // Datos de conexión
    private static final String URL = "jdbc:mysql://localhost:3306/Alumnos24";
    private static final String USUARIO = "tarea_12";
    private static final String CONTRASENA = "tarea_12";

    /**
     * Obtiene una conexión a la base de datos MySQL.
     * 
     * @return Objeto Connection si la conexión fue exitosa; null en caso contrario.
     */
    public static Connection getConexion() {
        Connection conexion = null;
        try {
            // Establecer la conexión a la base de datos
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
        } catch (SQLException e) {
            // Mostrar un mensaje si ocurre un error al conectar
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
        }
        return conexion;
    }
}
