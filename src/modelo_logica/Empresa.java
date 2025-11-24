package modelo_logica;

import controlador_persistencia.ConexionBD;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Empresa {

    //Atributos -Caracteristicas - Propiedades
    private String nombre;
    private List<Cerrajero> cerrajeros;
    private ConexionBD conexionBD;

    //Métodos - Funciones
    public Empresa(String nombre) {
        this.nombre = nombre;
        this.cerrajeros = new ArrayList<>();
        this.conexionBD = new ConexionBD();
    }

    public void cargarCerrajerosDesdeBD() throws SQLException {
        cerrajeros.clear();
        Connection conn = null;
        
        try {
            conn = conexionBD.getConnection();
            
            Statement stmt = conn.createStatement();
            
            ResultSet rs = stmt.executeQuery("SELECT MIN(id_cerrajero) as id_cerrajero, nombre_ce, telefono_ce FROM cerrajero GROUP BY nombre_ce, telefono_ce ORDER BY nombre_ce");
            
            while (rs.next()) {
                Cerrajero cerrajero = new Cerrajero(
                        rs.getLong("id_cerrajero"),
                        rs.getString("nombre_ce"),
                        String.valueOf(rs.getLong("telefono_ce"))
                );
                cerrajeros.add(cerrajero);
            }
        } finally {
            conexionBD.closeConnection(conn);
        }
    }
    
    public String getNombre() {
        return nombre;
    }

    public List<Cerrajero> getCerrajeros() {
        return cerrajeros;
    }
}
