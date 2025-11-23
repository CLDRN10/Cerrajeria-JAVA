package controlador_persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ==================================================================
 * PAQUETE controlador_persistencia
 * ==================================================================
 * Este paquete contiene las clases cuya única responsabilidad es
 * interactuar con la capa de "persistencia", es decir, la base de datos.
 * Separa la lógica de conexión de la lógica de negocio.
 * 
 * ==================================================================
 * CLASE ConexionBD
 * ==================================================================
 * Gestiona de forma centralizada la creación y el cierre de conexiones
 * a la base de datos PostgreSQL.
 * 
 * PROPÓSITO:
 * Abstraer los detalles de la conexión (URL, usuario, contraseña) en un solo lugar.
 * Si en el futuro la base de datos cambiara o se moviera a otro servidor,
 * solo habría que modificar esta clase.
 */
public class ConexionBD {

    // --- CONSTANTES DE CONEXIÓN ---
    // Se definen como constantes (`private static final`) porque son valores
    // fijos que no deben cambiar durante la ejecución del programa.
    private static final String URL = "jdbc:postgresql://localhost:5432/cerrajeria_bd";
    private static final String USUARIO = "postgres";
    private static final String CONTRASENA = "123456";

    /**
     * --- MÉTODO getConnection ---
     * Establece y devuelve una nueva conexión con la base de datos.
     * Cada vez que una clase del modelo (como `Servicio`) necesita realizar una
     * operación en la BD, llama a este método para obtener un "canal" de comunicación.
     *
     * @return Un objeto `Connection` que representa la sesión con la base de datos.
     * @throws SQLException Si no se puede establecer la conexión (ej: BD no disponible,
     *                      credenciales incorrectas, etc.).
     */
    public Connection getConnection() throws SQLException {
        // DriverManager es una clase de Java (del driver JDBC) que gestiona los drivers de BD.
        // El método `getConnection` intenta establecer la conexión usando la URL, usuario y contraseña.
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }

    /**
     * --- MÉTODO closeConnection ---
     * Cierra de forma segura una conexión a la base de datos.
     * Es CRUCIAL cerrar siempre las conexiones después de usarlas para liberar
     * recursos tanto en la aplicación como en el servidor de la base de datos.
     *
     * @param conn El objeto `Connection` que se desea cerrar.
     */
    public void closeConnection(Connection conn) {
        // Se verifica que la conexión no sea nula y que no esté ya cerrada.
        if (conn != null) {
            try {
                // El método `close()` libera los recursos de la conexión.
                conn.close();
            } catch (SQLException ex) {
                // Si ocurre un error al cerrar, se imprime en la consola para diagnóstico.
                // No se relanza la excepción porque cerrar la conexión es una operación
                // de limpieza, y un fallo aquí no debería detener el flujo principal.
                System.err.println("Error al cerrar la conexión a la BD: " + ex.getMessage());
            }
        }
    }
}
