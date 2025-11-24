package modelo_logica;

import controlador_persistencia.ConexionBD;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class Servicio {

    //Atributos - Características - Propiedades 
    private Integer idServicio;
    private Date fecha;
    private Time hora;
    private String tipo;
    private EstadoServicio estado;
    private BigDecimal monto;
    private String metodoPago;
    private Cliente cliente;
    private Cerrajero cerrajero;
    private static ConexionBD conexionBD = new ConexionBD();

    //Métodos - Funciones
    public Servicio(Date fecha, Time hora, String tipo, EstadoServicio estado, BigDecimal monto, String metodoPago, Cliente cliente, Cerrajero cerrajero) {
        this.fecha = fecha;
        this.hora = hora;
        this.tipo = tipo;
        this.estado = estado;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.cliente = cliente;
        this.cerrajero = cerrajero;
    }

    public Servicio(Integer id, Date fecha, Time hora, String tipo, EstadoServicio estado, BigDecimal monto, String metodoPago, Cliente cliente, Cerrajero cerrajero) {
        this(fecha, hora, tipo, estado, monto, metodoPago, cliente, cerrajero);
        this.idServicio = id;
    }

    public void guardar() throws SQLException {
        Connection conn = null;
        try {
            conn = conexionBD.getConnection();
            conn.setAutoCommit(false);
            
            cliente.guardar(conn);
            
            cerrajero.guardar(conn);
            
            String sql = "INSERT INTO servicio (fecha_s, hora_s, tipo_s, estado_s, monto_pago, metodo_pago, id_cliente, id_cerrajero) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pstmt.setDate(1, new java.sql.Date(fecha.getTime()));
                pstmt.setTime(2, hora);
                pstmt.setString(3, tipo);
                pstmt.setString(4, estado.getDescripcion());
                pstmt.setBigDecimal(5, monto);
                pstmt.setString(6, metodoPago);
                pstmt.setLong(7, cliente.getIdCliente());
                pstmt.setLong(8, cerrajero.getIdCerrajero());
                pstmt.executeUpdate();
                
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        this.idServicio = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("No se pudo obtener el ID del servicio.");
                    }
                }
            }
            conn.commit();
        } catch (SQLException ex) {
            if (conn != null) conn.rollback();
            throw ex;
        } finally {
            conexionBD.closeConnection(conn);
        }
    }

    public void actualizar() throws SQLException {
        Connection conn = null;
        try {
            conn = conexionBD.getConnection();
            conn.setAutoCommit(false);
            
            cliente.actualizar(conn);

            cerrajero.guardar(conn);
            
            String sql = "UPDATE servicio SET fecha_s=?, hora_s=?, tipo_s=?, estado_s=?, monto_pago=?, metodo_pago=?, id_cerrajero=? WHERE id_servicio=?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDate(1, new java.sql.Date(fecha.getTime()));
                pstmt.setTime(2, hora);
                pstmt.setString(3, tipo);
                pstmt.setString(4, estado.getDescripcion());
                pstmt.setBigDecimal(5, monto);
                pstmt.setString(6, metodoPago);
                pstmt.setLong(7, cerrajero.getIdCerrajero());
                pstmt.setInt(8, idServicio);
                pstmt.executeUpdate();
            }
            
            conn.commit();
        } catch (SQLException ex) {
            if (conn != null) conn.rollback();
            throw ex;
        } finally {
            conexionBD.closeConnection(conn);
        }
    }

    public static void eliminar(int id) throws SQLException {
        Connection conn = null;
        try {
            conn = conexionBD.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM servicio WHERE id_servicio = ?")) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
        } finally {
            conexionBD.closeConnection(conn);
        }
    }
    
    public static List<Vector<Object>> listarServicios(String filtroCliente) throws SQLException {
        List<Vector<Object>> servicios = new ArrayList<>();
        String sql = "SELECT s.id_servicio, s.fecha_s, s.tipo_s, c.nombre_c, ce.nombre_ce, s.estado_s, s.monto_pago " +
                     "FROM servicio s JOIN cliente c ON s.id_cliente = c.id_cliente JOIN cerrajero ce ON s.id_cerrajero = ce.id_cerrajero";
        if (filtroCliente != null && !filtroCliente.isEmpty()) {
            sql += " WHERE c.nombre_c ILIKE ?";
        }
        sql += " ORDER BY s.id_servicio DESC";
        
        Connection conn = null;
        try {
            conn = conexionBD.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            if (filtroCliente != null && !filtroCliente.isEmpty()) {
                pstmt.setString(1, "%" + filtroCliente + "%");
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Vector<Object> fila = new Vector<>();
                fila.add(rs.getInt("id_servicio"));
                fila.add(rs.getDate("fecha_s"));
                fila.add(rs.getString("tipo_s"));
                fila.add(rs.getString("nombre_c"));
                fila.add(rs.getString("nombre_ce"));
                fila.add(rs.getString("estado_s"));
                fila.add(rs.getBigDecimal("monto_pago"));
                servicios.add(fila);
            }
        } finally {
            conexionBD.closeConnection(conn);
        }
        return servicios;
    }

    public static Servicio cargarPorId(int idServicio) throws SQLException {
        String sql = "SELECT s.*, c.*, ce.* FROM servicio s " +
                     "JOIN cliente c ON s.id_cliente = c.id_cliente " +
                     "JOIN cerrajero ce ON s.id_cerrajero = ce.id_cerrajero " +
                     "WHERE s.id_servicio = ?";
        Connection conn = null;
        try {
            conn = conexionBD.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idServicio);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Cliente cliente = new Cliente(
                    rs.getLong("id_cliente"), rs.getString("nombre_c"),
                    String.valueOf(rs.getLong("telefono_c")), rs.getString("direccion_c"),
                    rs.getString("ciudad_c")
                );
                Cerrajero cerrajero = new Cerrajero(
                    rs.getLong("id_cerrajero"), rs.getString("nombre_ce"),
                    String.valueOf(rs.getLong("telefono_ce"))
                );
                return new Servicio(
                    rs.getInt("id_servicio"), rs.getDate("fecha_s"), rs.getTime("hora_s"),
                    rs.getString("tipo_s"), EstadoServicio.fromDescripcion(rs.getString("estado_s")),
                    rs.getBigDecimal("monto_pago"), rs.getString("metodo_pago"),
                    cliente, cerrajero
                );
            }
        } finally {
            conexionBD.closeConnection(conn);
        }
        return null;
    }

    public Integer getIdServicio() { return idServicio; }
    public Date getFecha() { return fecha; }
    public Time getHora() { return hora; }
    public String getTipo() { return tipo; }
    public EstadoServicio getEstado() { return estado; }
    public BigDecimal getMonto() { return monto; }
    public String getMetodoPago() { return metodoPago; }
    public Cliente getCliente() { return cliente; }
    public Cerrajero getCerrajero() { return cerrajero; }
    public void setCerrajero(Cerrajero cerrajero) { this.cerrajero = cerrajero; }
}
