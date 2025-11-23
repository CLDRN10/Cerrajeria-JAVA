package modelo_logica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ==================================================================
 * CLASE Cerrajero
 * ==================================================================
 * Representa a un cerrajero en el sistema. Al igual que Cliente,
 * esta clase "hereda" de `Persona` para reutilizar los atributos
 * y métodos comunes (nombre y teléfono).
 * 
 * PROPÓSITO:
 * Encapsular toda la información y la lógica de negocio de un cerrajero,
 * principalmente su capacidad para ser guardado en la base de datos. La clase
 * se encarga de su propia persistencia, manteniendo el diseño descentralizado.
 */
public class Cerrajero extends Persona {

    // --- ATRIBUTOS ---
    private Long idCerrajero; // Identificador único del cerrajero para la base de datos.

    /**
     * --- CONSTRUCTOR (Para cerrajeros que ya existen en la BD) ---
     * Se utiliza para crear objetos de cerrajeros que ya han sido cargados
     * desde la base de datos y, por lo tanto, ya tienen un ID.
     *
     * @param idCerrajero El ID único del cerrajero.
     * @param nombre      El nombre del cerrajero.
     * @param telefono    El teléfono del cerrajero.
     */
    public Cerrajero(Long idCerrajero, String nombre, String telefono) {
        // Se llama al constructor de la clase padre (Persona) para inicializar el nombre y el teléfono.
        super(nombre, telefono);
        this.idCerrajero = idCerrajero;
    }

    /**
     * --- CONSTRUCTOR (Para cerrajeros nuevos) ---
     * Se utiliza para crear un nuevo cerrajero, por ejemplo, el "Otro" que se
     * introduce en el formulario. No tiene ID todavía porque la base de datos
     * se lo asignará.
     *
     * @param nombre   El nombre del nuevo cerrajero.
     * @param telefono El teléfono del nuevo cerrajero.
     */
    public Cerrajero(String nombre, String telefono) {
        super(nombre, telefono);
        // this.idCerrajero se deja como null para indicar que es un cerrajero nuevo.
    }

    /**
     * --- MÉTODO guardar ---
     * Define la lógica para insertar un nuevo cerrajero en la base de datos.
     * Solo actúa si el cerrajero es nuevo (es decir, si su idCerrajero es null).
     * Si ya tiene un ID, se asume que ya existe en la BD y no hace nada.
     * 
     * @param conn La conexión activa a la base de datos, para poder ser parte de transacciones.
     * @throws SQLException Si ocurre un error durante la inserción.
     */
    public void guardar(Connection conn) throws SQLException {
        // Solo se guarda si el cerrajero no tiene un ID asignado.
        if (this.idCerrajero == null) {
            // Manejo de teléfono opcional: si el string de teléfono está vacío, se inserta un 0.
            long telefonoNum = getTelefono().isEmpty() ? 0 : Long.parseLong(getTelefono());

            // "try-with-resources" para la gestión automática del PreparedStatement.
            // "RETURNING id_cerrajero" para obtener el ID generado por la BD.
            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO cerrajero (nombre_ce, telefono_ce) VALUES (?, ?) RETURNING id_cerrajero")) {
                
                // Se asignan los valores a los parámetros de la consulta SQL.
                pstmt.setString(1, getNombre());
                pstmt.setLong(2, telefonoNum);
                
                // Se ejecuta la consulta y se obtiene el resultado.
                ResultSet rs = pstmt.executeQuery();
                
                // Si la base de datos devolvió el ID generado, se lo asignamos al objeto.
                if (rs.next()) {
                    this.idCerrajero = rs.getLong(1);
                } else {
                    // Si no, se lanza una excepción indicando el problema.
                    throw new SQLException("Fallo al crear el cerrajero, no se obtuvo el ID.");
                }
            }
        }
    }

    /**
     * --- GETTERS Y SETTERS ---
     * Métodos públicos para acceder y modificar el ID del cerrajero.
     */

    public Long getIdCerrajero() {
        return idCerrajero;
    }

    public void setIdCerrajero(Long idCerrajero) {
        this.idCerrajero = idCerrajero;
    }
}
