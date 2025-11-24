package modelo_logica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Cerrajero extends Persona {

    //Atributos - Caracteristicas - Propiedades
    private Long idCerrajero;


    //Métodos - Funciones
    public Cerrajero(Long idCerrajero, String nombre, String telefono) {
        super(nombre, telefono);
        this.idCerrajero = idCerrajero;
    }

    public Cerrajero(String nombre, String telefono) {
        super(nombre, telefono);
    }

    public void guardar(Connection conn) throws SQLException {
        if (this.idCerrajero == null) {
            long telefonoNum = getTelefono().isEmpty() ? 0 : Long.parseLong(getTelefono());

            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO cerrajero (nombre_ce, telefono_ce) VALUES (?, ?) RETURNING id_cerrajero")) {
                
                pstmt.setString(1, getNombre());
                pstmt.setLong(2, telefonoNum);
                
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    this.idCerrajero = rs.getLong(1);
                } else {
                    throw new SQLException("Fallo al crear el cerrajero, no se obtuvo el ID.");
                }
            }
        }
    }

    public Long getIdCerrajero() {
        return idCerrajero;
    }

    public void setIdCerrajero(Long idCerrajero) {
        this.idCerrajero = idCerrajero;
    }
}
