package modelo_logica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Cliente extends Persona {

    //Atributos - Caracteristicas - Propiedades
    private Long idCliente;
    private String direccion;
    private String ciudad;


    //Métodos - Funciones
    public Cliente(Long idCliente, String nombre, String telefono, String direccion, String ciudad) {
        super(nombre, telefono);
        this.idCliente = idCliente;
        this.direccion = direccion;
        this.ciudad = ciudad;
    }

    public Cliente(String nombre, String telefono, String direccion, String ciudad) {
        super(nombre, telefono);
        this.direccion = direccion;
        this.ciudad = ciudad;
    }

    public void guardar(Connection conn) throws SQLException {
        if (this.idCliente == null) {
            String sql = "INSERT INTO cliente (nombre_c, telefono_c, direccion_c, ciudad_c) VALUES (?, ?, ?, ?) RETURNING id_cliente";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, getNombre());
                pstmt.setLong(2, Long.parseLong(getTelefono()));
                pstmt.setString(3, this.direccion);
                pstmt.setString(4, this.ciudad);
                
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    this.idCliente = rs.getLong(1);
                } else {
                    throw new SQLException("Fallo al crear el cliente, no se obtuvo el ID.");
                }
            }
        } 
    }

    public void actualizar(Connection conn) throws SQLException {
        String sql = "UPDATE cliente SET nombre_c = ?, telefono_c = ?, direccion_c = ?, ciudad_c = ? WHERE id_cliente = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, getNombre());
            pstmt.setLong(2, Long.parseLong(getTelefono()));
            pstmt.setString(3, this.direccion);
            pstmt.setString(4, this.ciudad);
            pstmt.setLong(5, this.idCliente);
            pstmt.executeUpdate();
        }
    }

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

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
}
