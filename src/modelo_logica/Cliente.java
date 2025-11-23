package modelo_logica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ==================================================================
 * CLASE Cliente
 * ==================================================================
 * Representa a un cliente en el sistema. Esta clase "hereda" de la
 * clase `Persona`, lo que significa que automáticamente tiene los atributos
 * y métodos de `Persona` (nombre y teléfono) y añade los suyos propios.
 * 
 * PROPÓSITO:
 * Encapsular todos los datos y operaciones relacionados con un cliente,
 * incluyendo cómo se guarda y actualiza en la base de datos. Esto es un
 * claro ejemplo de "descentralización": la lógica del cliente está en la
 * clase Cliente, no dispersa por el sistema.
 */
// La palabra "extends" indica que Cliente es una "subclase" de Persona.
public class Cliente extends Persona {

    // --- ATRIBUTOS ---
    // Atributos específicos de la clase Cliente.
    private Long idCliente; // Identificador único para la base de datos. Es un Long para números grandes.
    private String direccion; // Dirección del cliente.
    private String ciudad;    // Ciudad de residencia del cliente.

    /**
     * --- CONSTRUCTOR (Para clientes que ya existen en la BD) ---
     * Se usa cuando cargamos un cliente desde la base de datos, ya que ya tiene un ID.
     * 
     * @param idCliente El ID del cliente obtenido de la BD.
     * @param nombre    El nombre del cliente.
     * @param telefono  El teléfono del cliente.
     * @param direccion La dirección del cliente.
     * @param ciudad    La ciudad del cliente.
     */
    public Cliente(Long idCliente, String nombre, String telefono, String direccion, String ciudad) {
        // "super(nombre, telefono)" llama al constructor de la clase padre (Persona)
        // para inicializar los atributos que se heredan de ella.
        super(nombre, telefono);
        this.idCliente = idCliente;
        this.direccion = direccion;
        this.ciudad = ciudad;
    }

    /**
     * --- CONSTRUCTOR (Para clientes nuevos) ---
     * Se usa cuando creamos un cliente nuevo desde la interfaz gráfica.
     * No recibe un `idCliente` porque la base de datos lo generará automáticamente.
     */
    public Cliente(String nombre, String telefono, String direccion, String ciudad) {
        super(nombre, telefono);
        this.direccion = direccion;
        this.ciudad = ciudad; 
        // this.idCliente se deja como null para indicar que es un cliente nuevo.
    }

    /**
     * --- MÉTODO guardar ---
     * Se encarga de la lógica para insertar un nuevo cliente en la base de datos.
     * Este método es un pilar de la descentralización: la propia clase Cliente
     * sabe cómo guardarse a sí misma.
     *
     * @param conn La conexión a la base de datos activa. Se pasa como parámetro
     *             para poder formar parte de una transacción más grande si es necesario.
     * @throws SQLException Si ocurre algún error durante la inserción en la BD.
     */
    public void guardar(Connection conn) throws SQLException {
        // Si el idCliente es null, significa que es un objeto nuevo y necesita ser insertado.
        if (this.idCliente == null) {
            // "try-with-resources" asegura que el PreparedStatement se cierre automáticamente.
            // "RETURNING id_cliente" es una cláusula de PostgreSQL que devuelve el ID recién generado.
            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO cliente (nombre_c, telefono_c, direccion_c, ciudad_c) VALUES (?, ?, ?, ?) RETURNING id_cliente")) {
                
                // Se asignan los valores a los '?' del SQL para evitar inyección SQL.
                pstmt.setString(1, getNombre()); // Obtenemos el nombre heredado de Persona
                pstmt.setLong(2, Long.parseLong(getTelefono())); // Convertimos el teléfono a número
                pstmt.setString(3, getDireccion());
                pstmt.setString(4, getCiudad());
                
                // executeQuery() se usa cuando la consulta devuelve un resultado (en este caso, el ID).
                ResultSet rs = pstmt.executeQuery();
                
                // Si se generó y devolvió un ID, lo leemos y lo asignamos al atributo idCliente.
                if (rs.next()) {
                    this.idCliente = rs.getLong(1);
                } else {
                    // Si no se devuelve un ID, algo salió mal y lanzamos un error.
                    throw new SQLException("Fallo al crear el cliente, no se obtuvo el ID.");
                }
            }
        }
        // Si el idCliente no es null, se asume que ya existe y no se hace nada.
    }

    /**
     * --- MÉTODO actualizar ---
     * Se encarga de la lógica para actualizar los datos de un cliente existente en la BD.
     *
     * @param conn La conexión a la base de datos activa.
     * @throws SQLException Si ocurre algún error durante la actualización.
     */
    public void actualizar(Connection conn) throws SQLException {
        // Prepara la sentencia SQL para actualizar (UPDATE).
        try (PreparedStatement pstmt = conn.prepareStatement("UPDATE cliente SET nombre_c = ?, telefono_c = ?, direccion_c = ?, ciudad_c = ? WHERE id_cliente = ?")) {
            
            // Asigna los nuevos valores a los '?' de la consulta.
            pstmt.setString(1, getNombre());
            pstmt.setLong(2, Long.parseLong(getTelefono()));
            pstmt.setString(3, getDireccion());
            pstmt.setString(4, getCiudad());
            pstmt.setLong(5, this.idCliente); // El ID del cliente se usa en el WHERE para saber cuál actualizar.
            
            // executeUpdate() se usa para sentencias que no devuelven un resultado (INSERT, UPDATE, DELETE).
            pstmt.executeUpdate();
        }
    }

    /**
     * --- GETTERS Y SETTERS ---
     * Métodos públicos para acceder y modificar los atributos específicos de Cliente.
     */

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getCiudad() {
        return ciudad;
    }
}
