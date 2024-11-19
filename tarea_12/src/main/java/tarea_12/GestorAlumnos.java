package tarea_12;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Clase para gestionar las operaciones relacionadas con la entidad Alumno.
 */
public class GestorAlumnos {

    private final Scanner sc = new Scanner(System.in);

    /**
     * Solicita al usuario los datos necesarios para crear un objeto Alumno.
     * 
     * @return Un objeto Alumno con los datos ingresados por el usuario.
     */
    public Alumno solicitarDatosAlumno() {
        String nombre, apellidos, ciclo, curso, nombreGrupo;
        char respuestaGenero;
        Date fechaNacimiento = null;

        System.out.println("Introduce el nombre del alumno:");
        nombre = sc.nextLine().toUpperCase().trim();

        System.out.println("Introduce los apellidos del alumno:");
        apellidos = sc.nextLine().toUpperCase().trim();

        // Validar género
        do {
            System.out.println("Introduce el género del alumno (M/F):");
            respuestaGenero = sc.nextLine().toUpperCase().charAt(0);  // Convertir a mayúscula
            if (respuestaGenero != 'M' && respuestaGenero != 'F') {
                System.out.println("Respuesta no válida. Introduce 'M' o 'F'.");
            }
        } while (respuestaGenero != 'M' && respuestaGenero != 'F');

        // Validar fecha de nacimiento
        do {
            System.out.println("Introduce la fecha de nacimiento (dd-MM-aaaa):");
            String fechaInput = sc.nextLine();
            try {
                SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
                formatoFecha.setLenient(false);  // Validación estricta
                fechaNacimiento = formatoFecha.parse(fechaInput);
            } catch (ParseException e) {
                System.out.println("Formato de fecha inválido. Intenta de nuevo.");
            }
        } while (fechaNacimiento == null);

        System.out.println("Introduce el ciclo del alumno:");
        ciclo = sc.nextLine().trim().toUpperCase();

        System.out.println("Introduce el curso del alumno:");
        curso = sc.nextLine().trim().toUpperCase();

        // Validar nombre del grupo
        do {
            System.out.println("Introduce el nombre del grupo del alumno:");
            nombreGrupo = sc.nextLine().toUpperCase();  // Convertir a mayúsculas
            if (!validarNombreGrupo(nombreGrupo)) {
                System.out.println("El nombre del grupo no es válido. Intenta de nuevo.");
            }
        } while (!validarNombreGrupo(nombreGrupo));

        // Crear el objeto Grupo
        Grupo grupo = new Grupo(nombreGrupo);

        // Crear y devolver el objeto Alumno
        return new Alumno(nombre, apellidos, respuestaGenero, fechaNacimiento, ciclo, curso, grupo);
    }

    /**
     * Inserta un nuevo alumno en la base de datos.
     * 
     * @param conexionBD La conexión a la base de datos.
     * @param alumno     El objeto Alumno que se desea insertar.
     * @return true si la inserción fue exitosa, false en caso contrario.
     */
    public boolean insertarAlumno(Connection conexionBD, Alumno alumno) {
        // Primero, obtener el numeroGrupo del grupo del alumno
        int numeroGrupo = obtenerNumeroGrupo(alumno.getGrupo().getNombreGrupo());

        if (numeroGrupo == -1) {
            System.out.println("Error: El grupo no existe en la base de datos.");
            return false;
        }

        String sql = "INSERT INTO alumnos (nombre, apellidos, genero, fechaNacimiento, ciclo, curso, numeroGrupo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement sentencia = conexionBD.prepareStatement(sql)) {
            sentencia.setString(1, alumno.getNombre());
            sentencia.setString(2, alumno.getApellidos());
            sentencia.setString(3, String.valueOf(alumno.getGenero()));  // Aseguramos que esté en mayúsculas
            sentencia.setDate(4, new java.sql.Date(alumno.getFechaNacimiento().getTime()));
            sentencia.setString(5, alumno.getCiclo());
            sentencia.setString(6, alumno.getCurso());
            sentencia.setInt(7, numeroGrupo);  // Usar el numeroGrupo obtenido

            int filasAfectadas = sentencia.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar el alumno: " + e.getMessage());
            return false;
        }
    }

    /**
     * Recupera el número del grupo a partir de su nombre.
     * 
     * @param nombreGrupo El nombre del grupo.
     * @return El numeroGrupo correspondiente o -1 si no existe.
     */
    private int obtenerNumeroGrupo(String nombreGrupo) {
        String sql = "SELECT numeroGrupo FROM grupos WHERE nombreGrupo = ?";
        try (Connection conexionBD = ConexionBDMySQL.getConexion();
             PreparedStatement sentencia = conexionBD.prepareStatement(sql)) {
            sentencia.setString(1, nombreGrupo);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getInt("numeroGrupo");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el numeroGrupo: " + e.getMessage());
        }
        return -1;  // Si no se encuentra el grupo, devolver -1
    }

    /**
     * Valida si un nombre de grupo existe en la base de datos.
     * 
     * @param nombreGrupo El nombre del grupo a validar.
     * @return true si el grupo existe, false en caso contrario.
     */
    public boolean validarNombreGrupo(String nombreGrupo) {
        String sql = "SELECT * FROM grupos WHERE nombreGrupo = ?";
        try (Connection conexionBD = ConexionBDMySQL.getConexion();
             PreparedStatement sentencia = conexionBD.prepareStatement(sql)) {
            sentencia.setString(1, nombreGrupo);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next();
            }
        } catch (SQLException e) {
            System.out.println("Error al validar el grupo: " + e.getMessage());
            return false;
        }
    }
}