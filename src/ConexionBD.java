import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    // Atributos
    private Connection connection;
    // La URL debe tener el formato jdbc:postgresql://host:port/database
    private String url = "jdbc:postgresql://bbuyudlfwnpkhqhq4gls-postgresql.services.clever-cloud.com:50013/bbuyudlfwnpkhqhq4gls";
    private String usuario = "uuxf0dkhinr6jhmhb8vq";
    private String contrasena = "n7qBZj8KPCrUrIam6MSfkwoNVqFzNd";

    public ConexionBD() {
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // La carga del driver es automática con JDBC 4.0+ si la dependencia está en el pom.xml
                // Class.forName("org.postgresql.Driver"); // No es necesario
                
                // Establecer la conexión
                connection = DriverManager.getConnection(url, usuario, contrasena);
                System.out.println("Conexión exitosa a la base de datos PostgreSQL.");
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            e.printStackTrace();
            // Relanzamos la excepción para que el código que llama se entere del problema
            throw new RuntimeException(e);
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
