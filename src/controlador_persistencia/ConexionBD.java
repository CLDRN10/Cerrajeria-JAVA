package controlador_persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    //Atributos - Carateristicas - Propiedades
    private static final String HOST = "bbuyudlfwnpkhqhq4gls-postgresql.services.clever-cloud.com";
    private static final String PORT = "50013";
    private static final String DATABASE = "bbuyudlfwnpkhqhq4gls";
    private static final String USUARIO = "uuxf0dkhinr6jhmhb8vq";
    private static final String CONTRASENA = "n7qBZj8KPCrUrIam6MSfkwoNVqFzNd";

    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE;

    //Metodos - Funciones
    public Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error: No se encontró el driver de PostgreSQL.", e);
        }
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }

    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace(System.err);
            }
        }
    }
}
