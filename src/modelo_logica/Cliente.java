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
 */
public class Cliente extends Persona {

    private Long idCliente;
    private String direccion;
    private String ciudad;

    /**
     * --- CONSTRUCTOR (Para clientes existentes) ---
     */
    public Cliente(Long idCliente, String nombre, String telefono, String direccion, String ciudad) {
        super(nombre, telefono);
        this.idCliente = idCliente;
        this.direccion = direccion;
        this.ciudad = ciudad;
    }

    /**
     * --- CONSTRUCTOR (Para clientes nuevos) ---
     */
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

    // --- GETTERS Y SETTERS ---

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

    // *** MÉTODOS AÑADIDOS PARA CORREGIR EL ERROR ***
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
}
