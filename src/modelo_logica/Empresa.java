package modelo_logica;

import controlador_persistencia.ConexionBD;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * ==================================================================
 * CLASE Empresa
 * ==================================================================
 * Representa a la empresa o negocio que provee los servicios de cerrajería.
 * 
 * PROPÓSITO:
 * Actúa como un contenedor principal para ciertos datos y lógicas de negocio
 * generales. En este caso, su responsabilidad principal es manejar la
 * colección de cerrajeros que pertenecen a la empresa.
 * 
 * A futuro, esta clase podría crecer para manejar otros aspectos del negocio,
 * como reportes, estadísticas, sucursales, etc.
 */
public class Empresa {

    // --- ATRIBUTOS ---
    private String nombre;               // El nombre de la empresa.
    private List<Cerrajero> cerrajeros;  // Una lista para almacenar todos los objetos Cerrajero de la empresa.
    private ConexionBD conexionBD;       // Una instancia para poder conectarse a la base de datos.

    /**
     * --- CONSTRUCTOR ---
     * Inicializa un nuevo objeto Empresa con su nombre y prepara la lista
     * de cerrajeros y el objeto de conexión.
     *
     * @param nombre El nombre de la empresa.
     */
    public Empresa(String nombre) {
        this.nombre = nombre;
        // Se inicializa la lista de cerrajeros como una lista vacía (ArrayList).
        this.cerrajeros = new ArrayList<>();
        // Se crea una nueva instancia del manejador de conexión a la BD.
        this.conexionBD = new ConexionBD();
    }

    /**
     * --- MÉTODO cargarCerrajerosDesdeBD ---
     * Se conecta a la base de datos para obtener la lista de todos los cerrajeros
     * únicos y los carga en la lista `cerrajeros` de la empresa.
     *
     * @throws SQLException Si ocurre un error al consultar la base de datos.
     */
    public void cargarCerrajerosDesdeBD() throws SQLException {
        // Primero, limpia la lista actual para no duplicar datos si se llama varias veces.
        cerrajeros.clear();
        Connection conn = null; // Declara la variable de conexión fuera del try para poder usarla en el finally.
        
        try {
            // Obtiene una conexión activa de la base de datos.
            conn = conexionBD.getConnection();
            
            // Un Statement se usa para ejecutar una consulta SQL simple sin parámetros.
            Statement stmt = conn.createStatement();
            
            // Se ejecuta la consulta SQL. 
            // "SELECT MIN(id_cerrajero) ..., nombre_ce, telefono_ce FROM cerrajero GROUP BY nombre_ce, telefono_ce ..."
            // Esta consulta es inteligente: agrupa a los cerrajeros por nombre y teléfono para no mostrar duplicados
            // en el ComboBox de la interfaz, en caso de que un cerrajero se haya insertado varias veces por error.
            ResultSet rs = stmt.executeQuery("SELECT MIN(id_cerrajero) as id_cerrajero, nombre_ce, telefono_ce FROM cerrajero GROUP BY nombre_ce, telefono_ce ORDER BY nombre_ce");
            
            // El bucle "while (rs.next())" recorre cada fila que la consulta devolvió.
            while (rs.next()) {
                // Por cada fila, crea un nuevo objeto Cerrajero con los datos obtenidos.
                Cerrajero cerrajero = new Cerrajero(
                        rs.getLong("id_cerrajero"),
                        rs.getString("nombre_ce"),
                        String.valueOf(rs.getLong("telefono_ce"))
                );
                // Añade el objeto Cerrajero recién creado a la lista de la empresa.
                cerrajeros.add(cerrajero);
            }
        } finally {
            // El bloque "finally" se ejecuta siempre, haya o no un error.
            // Es el lugar ideal para asegurarse de que la conexión a la BD se cierre correctamente.
            conexionBD.closeConnection(conn);
        }
    }
    
    /**
     * --- GETTERS ---
     * Métodos para obtener los atributos de la empresa.
     */

    public String getNombre() {
        return nombre;
    }

    public List<Cerrajero> getCerrajeros() {
        return cerrajeros;
    }
}
