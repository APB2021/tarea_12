package tarea_12;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GestorGrupos {

	/**
	 * Inserta un nuevo grupo en la base de datos.
	 *
	 * @param conexionBD La conexión a la base de datos.
	 * @param grupo      El objeto Grupo que se desea insertar.
	 * @return true si la inserción fue exitosa, false en caso contrario.
	 */
	public boolean insertarGrupo(Connection conexionBD, Grupo grupo) {
		String sql = "INSERT INTO grupos (nombreGrupo) VALUES (?)";

		try (PreparedStatement sentencia = conexionBD.prepareStatement(sql)) {
			sentencia.setString(1, grupo.getNombreGrupo().toUpperCase()); // Convertir nombre a mayúsculas

			int filasAfectadas = sentencia.executeUpdate();
			return filasAfectadas > 0;
		} catch (SQLException e) {
			System.out.println("Error al insertar el grupo: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Muestra todos los grupos disponibles en la base de datos.
	 * 
	 * @param conexionBD la conexión activa a la base de datos.
	 * @return true si se muestran los grupos correctamente, false si no hay grupos
	 *         o hay un error.
	 */
	public boolean mostrarTodosLosGrupos(Connection conexionBD) {
		String sql = "SELECT nombreGrupo FROM grupos";
		try (Statement sentencia = conexionBD.createStatement(); ResultSet resultado = sentencia.executeQuery(sql)) {
			boolean hayGrupos = false;
			while (resultado.next()) {
				hayGrupos = true;
				System.out.println("- " + resultado.getString("nombreGrupo"));
			}
			return hayGrupos;
		} catch (SQLException e) {
			System.out.println("Error al mostrar los grupos: " + e.getMessage());
			return false;
		}
	}
}