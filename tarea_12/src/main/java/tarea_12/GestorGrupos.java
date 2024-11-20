package tarea_12;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Esta clase gestiona los grupos de alumnos en la base de datos y proporciona
 * funcionalidad para guardar toda la información de los grupos y sus alumnos en
 * un archivo XML.
 */
public class GestorGrupos {

	private final Scanner sc = new Scanner(System.in);

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

	/**
	 * Este método obtiene los grupos de alumnos desde la base de datos y crea una
	 * lista de objetos Grupo, donde cada grupo incluye una lista de sus respectivos
	 * alumnos.
	 * 
	 * @param conexionBD La conexión activa a la base de datos.
	 * @return Una lista de objetos Grupo, cada uno con su lista de alumnos.
	 */
	public List<Grupo> obtenerGruposYAlumnos(Connection conexionBD) {
		// Lista para almacenar todos los grupos con sus respectivos alumnos
		List<Grupo> listaGrupos = new ArrayList<>();

		// Consulta SQL para obtener los datos de los grupos
		String consultaGrupos = "SELECT numeroGrupo, nombreGrupo FROM grupos";
		try (Statement sentenciaGrupos = conexionBD.createStatement();
				ResultSet resultadoGrupos = sentenciaGrupos.executeQuery(consultaGrupos)) {

			// Procesar cada grupo encontrado en la base de datos
			while (resultadoGrupos.next()) {
				// Recuperamos el número y nombre del grupo desde el ResultSet
				int numeroGrupo = resultadoGrupos.getInt("numeroGrupo");
				String nombreGrupo = resultadoGrupos.getString("nombreGrupo");

				// Consulta SQL para obtener los alumnos de este grupo
				String consultaAlumnos = "SELECT nia, nombre, apellidos, genero, fechaNacimiento FROM alumnos WHERE numeroGrupo = ?";
				try (PreparedStatement sentenciaAlumnos = conexionBD.prepareStatement(consultaAlumnos)) {
					// Establecemos el número de grupo en la consulta
					sentenciaAlumnos.setInt(1, numeroGrupo);

					// Ejecutamos la consulta y obtenemos los resultados de los alumnos
					try (ResultSet resultadoAlumnos = sentenciaAlumnos.executeQuery()) {
						// Lista para almacenar los alumnos de este grupo
						List<Alumno> listaAlumnos = new ArrayList<>();

						// Procesamos cada alumno encontrado en el ResultSet
						while (resultadoAlumnos.next()) {
							// Creamos un objeto Alumno y asignamos los valores recuperados de la base de
							// datos
							Alumno alumno = new Alumno();
							alumno.setNia(resultadoAlumnos.getInt("nia"));
							alumno.setNombre(resultadoAlumnos.getString("nombre"));
							alumno.setApellidos(resultadoAlumnos.getString("apellidos"));
							// Convertimos el String de "genero" a un char
							alumno.setGenero(resultadoAlumnos.getString("genero").charAt(0));

							// Convertimos la fecha de nacimiento de String a Date
							String fechaNacimientoStr = resultadoAlumnos.getString("fechaNacimiento");
							try {
								// Formato esperado para la fecha en la base de datos (ajustar según el formato
								// real)
								SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
								Date fechaNacimiento = formatoFecha.parse(fechaNacimientoStr);
								alumno.setFechaNacimiento(fechaNacimiento);
							} catch (ParseException e) {
								e.printStackTrace(); // Manejo de error si el formato de la fecha no es válido
							}

							// Añadimos el alumno a la lista de alumnos del grupo
							listaAlumnos.add(alumno);
						}

						// Creamos el objeto Grupo y asignamos la lista de alumnos y el número de grupo
						Grupo grupo = new Grupo();
						grupo.setNumeroGrupo(numeroGrupo); // Asignamos el número del grupo
						grupo.setNombreGrupo(nombreGrupo); // Asignamos el nombre del grupo
						grupo.setAlumnos(listaAlumnos); // Asignamos la lista de alumnos al grupo

						// Añadimos el grupo con su lista de alumnos a la lista final de grupos
						listaGrupos.add(grupo);
					}
				}
			}
		} catch (SQLException e) {
			// En caso de error con la consulta a la base de datos, se imprime el error
			e.printStackTrace();
		}

		// Devolvemos la lista de grupos con sus respectivos alumnos
		return listaGrupos;
	}

	/**
	 * Guarda todos los grupos y sus alumnos en un archivo XML llamado 'grupos.xml'.
	 * Si el archivo ya existe, solicita confirmación al usuario antes de
	 * sobrescribirlo.
	 * 
	 * @param conexionBD La conexión activa a la base de datos MySQL.
	 * @return true si el archivo se guarda correctamente, false si ocurre un error.
	 */
	public boolean guardarGruposEnXML(Connection conexionBD) {
		// Nombre del archivo XML donde se guardarán los grupos y sus alumnos
		String nombreArchivo = "grupos.xml";

		// Verificar si el archivo ya existe y pedir confirmación al usuario para
		// sobrescribirlo
		File archivoXML = new File(nombreArchivo);
		if (archivoXML.exists()) {
			// Solicitamos al usuario si desea sobrescribir el archivo

			System.out.print("El archivo " + nombreArchivo + " ya existe. ¿Deseas sobrescribirlo? (S/N): ");
			String respuesta = sc.nextLine();

			// Si la respuesta no es "s" o "S", no sobrescribimos el archivo
			if (!respuesta.equalsIgnoreCase("s")) {
				System.out.println("El archivo no se ha sobrescrito.");
				return false; // Salimos del método si no se desea sobrescribir
			}
		}

		// Inicializamos el DocumentBuilder para crear el archivo XML
		DocumentBuilderFactory documentoFactory = DocumentBuilderFactory.newInstance();
		try {
			// Creamos el documento XML
			DocumentBuilder documentoBuilder = documentoFactory.newDocumentBuilder();
			Document documentoXML = documentoBuilder.newDocument();

			// Creamos el elemento raíz <grupos>
			Element raizElement = documentoXML.createElement("grupos");
			documentoXML.appendChild(raizElement);

			// Consulta SQL para obtener todos los grupos de la base de datos
			String consultaGrupos = "SELECT * FROM grupos";
			try (PreparedStatement stmtGrupos = conexionBD.prepareStatement(consultaGrupos)) {
				ResultSet rsGrupos = stmtGrupos.executeQuery();

				// Procesamos cada grupo encontrado
				while (rsGrupos.next()) {
					// Obtenemos los datos de cada grupo
					int numeroGrupo = rsGrupos.getInt("numeroGrupo");
					String nombreGrupo = rsGrupos.getString("nombreGrupo");

					// Creamos el elemento <grupo> y lo añadimos al XML
					Element grupoElement = documentoXML.createElement("grupo");
					grupoElement.setAttribute("numeroGrupo", String.valueOf(numeroGrupo));
					grupoElement.setAttribute("nombreGrupo", nombreGrupo);
					raizElement.appendChild(grupoElement);

					// Consulta SQL para obtener los alumnos de cada grupo
					String consultaAlumnos = "SELECT * FROM alumnos WHERE numeroGrupo = ?";
					try (PreparedStatement stmtAlumnos = conexionBD.prepareStatement(consultaAlumnos)) {
						stmtAlumnos.setInt(1, numeroGrupo);
						ResultSet rsAlumnos = stmtAlumnos.executeQuery();

						// Procesamos cada alumno dentro del grupo
						while (rsAlumnos.next()) {
							// Obtenemos los detalles de cada alumno
							String nia = rsAlumnos.getString("nia");
							String nombreAlumno = rsAlumnos.getString("nombre");
							String apellidosAlumno = rsAlumnos.getString("apellidos");
							String genero = rsAlumnos.getString("genero");
							String fechaNacimiento = rsAlumnos.getString("fechaNacimiento");
							String curso = rsAlumnos.getString("curso");

							// Creamos el elemento <alumno> y añadimos los atributos correspondientes
							Element alumnoElement = documentoXML.createElement("alumno");
							alumnoElement.setAttribute("nia", nia);
							alumnoElement.setAttribute("nombre", nombreAlumno);
							alumnoElement.setAttribute("apellidos", apellidosAlumno);
							alumnoElement.setAttribute("genero", genero);
							alumnoElement.setAttribute("fechaNacimiento", fechaNacimiento);
							alumnoElement.setAttribute("curso", curso);

							// Añadimos el alumno al grupo en el XML
							grupoElement.appendChild(alumnoElement);
						}
					}
				}

				// Configuración del transformador para generar el archivo XML
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");

				// Creamos un archivo con el contenido XML generado
				DOMSource source = new DOMSource(documentoXML);
				StreamResult result = new StreamResult(new File(nombreArchivo));
				transformer.transform(source, result);

				// Mensaje de confirmación
				System.out.println("El archivo XML se ha guardado correctamente.");
				return true;
			} catch (SQLException e) {
				System.out.println("Error al consultar los grupos o los alumnos: " + e.getMessage());
				return false;
			}
		} catch (ParserConfigurationException | TransformerException e) {
			System.out.println("Error al generar el archivo XML: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Lee un archivo XML que contiene información sobre grupos y alumnos, y guarda
	 * los datos en las tablas correspondientes de la base de datos.
	 *
	 * @param rutaArchivo Ruta del archivo XML a procesar.
	 * @param conexionBD  Conexión activa a la base de datos.
	 * @return true si los datos fueron procesados e insertados correctamente, false en caso de error.
	 */
	public static boolean leerYGuardarGruposXML(String rutaArchivo, Connection conexionBD) {
	    // Validar si el archivo XML existe en la ruta especificada
	    File archivoXML = new File(rutaArchivo);
	    if (!archivoXML.exists()) {
	        System.err.println("El archivo XML no existe: " + rutaArchivo);
	        return false;
	    }

	    try {
	        // Crear un objeto DocumentBuilder para interpretar el contenido del archivo XML
	        DocumentBuilderFactory fabricaDocumentos = DocumentBuilderFactory.newInstance();
	        DocumentBuilder constructorDocumentos = fabricaDocumentos.newDocumentBuilder();
	        Document documentoXML = constructorDocumentos.parse(archivoXML);
	        documentoXML.getDocumentElement().normalize(); // Normalizamos el documento XML

	        // Obtenemos la lista de todos los elementos <grupo>
	        NodeList listaGrupos = documentoXML.getElementsByTagName("grupo");

	        // Consulta SQL para insertar grupos en la tabla 'grupos'
	        String sqlInsertarGrupo = "INSERT INTO grupos (nombreGrupo) VALUES (?)";
	        PreparedStatement consultaInsertarGrupo = conexionBD.prepareStatement(sqlInsertarGrupo, Statement.RETURN_GENERATED_KEYS);

	        // Consulta SQL para insertar alumnos en la tabla 'alumnos'
	        String sqlInsertarAlumno = "INSERT INTO alumnos (nombre, apellidos, genero, fechaNacimiento, curso, numeroGrupo) " +
	                                   "VALUES (?, ?, ?, ?, ?, ?)";
	        PreparedStatement consultaInsertarAlumno = conexionBD.prepareStatement(sqlInsertarAlumno);

	        // Recorremos la lista de elementos <grupo> del XML
	        for (int i = 0; i < listaGrupos.getLength(); i++) {
	            Node nodoGrupo = listaGrupos.item(i);

	            // Verificamos que el nodo actual sea un elemento
	            if (nodoGrupo.getNodeType() == Node.ELEMENT_NODE) {
	                Element elementoGrupo = (Element) nodoGrupo;

	                // Extraemos el atributo 'nombreGrupo' del elemento <grupo>
	                String nombreGrupo = elementoGrupo.getAttribute("nombreGrupo");

	                // Verificamos que el atributo 'nombreGrupo' no esté vacío
	                if (nombreGrupo != null && !nombreGrupo.trim().isEmpty()) {
	                    // Insertamos el grupo en la base de datos
	                    consultaInsertarGrupo.setString(1, nombreGrupo);
	                    consultaInsertarGrupo.executeUpdate();

	                    // Obtenemos el 'numeroGrupo' generado automáticamente por la base de datos
	                    ResultSet clavesGeneradas = consultaInsertarGrupo.getGeneratedKeys();
	                    int numeroGrupo = 0; // Inicializamos el número del grupo
	                    if (clavesGeneradas.next()) {
	                        numeroGrupo = clavesGeneradas.getInt(1);
	                    }

	                    // Procesar los elementos <alumno> dentro del grupo
	                    NodeList listaAlumnos = elementoGrupo.getElementsByTagName("alumno");
	                    for (int j = 0; j < listaAlumnos.getLength(); j++) {
	                        Node nodoAlumno = listaAlumnos.item(j);

	                        // Verificamos que el nodo actual sea un elemento
	                        if (nodoAlumno.getNodeType() == Node.ELEMENT_NODE) {
	                            Element elementoAlumno = (Element) nodoAlumno;

	                            // Extraer los atributos del alumno
	                            String nombreAlumno = elementoAlumno.getAttribute("nombre");
	                            String apellidosAlumno = elementoAlumno.getAttribute("apellidos");
	                            String generoAlumno = elementoAlumno.getAttribute("genero");
	                            String fechaNacimientoAlumno = elementoAlumno.getAttribute("fechaNacimiento");
	                            String cursoAlumno = elementoAlumno.getAttribute("curso");

	                            // Insertar el alumno en la base de datos
	                            consultaInsertarAlumno.setString(1, nombreAlumno);
	                            consultaInsertarAlumno.setString(2, apellidosAlumno);
	                            consultaInsertarAlumno.setString(3, generoAlumno);
	                            consultaInsertarAlumno.setDate(4, java.sql.Date.valueOf(fechaNacimientoAlumno));
	                            consultaInsertarAlumno.setString(5, cursoAlumno);
	                            consultaInsertarAlumno.setInt(6, numeroGrupo); // Asociamos el alumno con el grupo
	                            consultaInsertarAlumno.executeUpdate();

	                            // Mensaje informativo de inserción
	                            System.out.println("Alumno insertado: " + nombreAlumno + " " + apellidosAlumno);
	                        }
	                    }
	                } else {
	                    // Si 'nombreGrupo' está vacío, mostramos una advertencia
	                    System.out.println("Advertencia: Nombre del grupo vacío en el XML.");
	                }
	            }
	        }

	        // Mensaje informativo de éxito
	        System.out.println("Datos cargados correctamente desde el archivo XML.");
	        return true;
	    } catch (Exception e) {
	        // Mostrar error en caso de que algo falle
	        System.err.println("Error al procesar el archivo XML o insertar los datos: " + e.getMessage());
	        e.printStackTrace();
	        return false;
	    }
	}

}