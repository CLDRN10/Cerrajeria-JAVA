import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    // Atributos - Propiedades - Caracteristicas
    private Connection connection;
    private String url = "postgresql://uuxf0dkhinr6jhmhb8vq:n7qBZj8KPCrUrIam6MSfkwoNVqFzNd@bbuyudlfwnpkhqhq4gls-postgresql.services.clever-cloud.com:50013/bbuyudlfwnpkhqhq4gls"; // Cambia esto por tu URL
    private String usuario = "uuxf0dkhinr6jhmhb8vq"; // Cambia esto por tu usuario
    private String contrasena = "n7qBZj8KPCrUrIam6MSfkwoNVqFzNd"; // Cambia esto por tu contraseña

    // Métodos - Funciones
    public ConexionBD() {
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Cargar el driver de MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");
                // Establecer la conexión
                connection = DriverManager.getConnection(url, usuario, contrasena);
                System.out.println("Conexión exitosa a la base de datos.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión a la base de datos cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
