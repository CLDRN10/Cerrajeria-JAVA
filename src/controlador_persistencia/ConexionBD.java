
package controlador_persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ==================================================================
 * CLASE ConexionBD
 * ==================================================================
 * Gestiona la conexión con la base de datos PostgreSQL.
 * Centraliza los detalles de la conexión (URL, usuario, contraseña)
 * para que sean fáciles de modificar en un solo lugar.
 */
public class ConexionBD {

    // --- CREDENCIALES DE LA BASE DE DATOS EN CLEVER CLOUD ---
    // Extraídas de la URL proporcionada en el error.
    private static final String HOST = "bbuyudlfwnpkhqhq4gls-postgresql.services.clever-cloud.com";
    private static final String PORT = "50013";
    private static final String DATABASE = "bbuyudlfwnpkhqhq4gls";
    private static final String USUARIO = "uuxf0dkhinr6jhmhb8vq";
    private static final String CONTRASENA = "n7qBZj8KPCrUrIam6MSfkwoNVqFzNd";

    // --- URL DE CONEXIÓN JDBC ---
    // El formato correcto es "jdbc:postgresql://HOST:PORT/DATABASE"
    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE;

    /**
     * --- MÉTODO getConnection ---
     * Intenta establecer y devolver una conexión con la base de datos.
     * Carga el driver de PostgreSQL antes de intentar la conexión.
     * @return Un objeto Connection si la conexión es exitosa.
     * @throws SQLException Si ocurre un error al conectar (ej: credenciales incorrectas, BD no disponible).
     */
    public Connection getConnection() throws SQLException {
        try {
            // Esto asegura que el driver de PostgreSQL esté cargado en memoria.
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            // Este error ocurriría si el archivo .jar del driver no estuviera en el proyecto.
            throw new SQLException("Error: No se encontró el driver de PostgreSQL.", e);
        }
        // Intenta la conexión usando las credenciales y la URL formateada.
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }

    /**
     * --- MÉTODO closeConnection ---
     * Cierra la conexión de forma segura, verificando que no sea nula.
     * @param conn La conexión a cerrar.
     */
    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                // Imprime el error en la consola si falla el cierre.
                // En una app real, esto podría ir a un sistema de logs.
                ex.printStackTrace(System.err);
            }
        }
    }
}
