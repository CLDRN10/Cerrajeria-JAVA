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

/**
 * ==================================================================
 * CLASE Servicio
 * ==================================================================
 * Es la clase central del modelo de negocio. Representa un servicio
 * de cerrajería prestado a un cliente por un cerrajero.
 * 
 * PROPÓSITO:
 * Orquesta las operaciones más complejas de la aplicación (CRUD: Crear, Leer, Actualizar, Eliminar).
 * Contiene la lógica para guardar, actualizar, eliminar y consultar servicios, interactuando
 * con las clases Cliente y Cerrajero y manejando las transacciones de la base de datos para
 * asegurar la integridad de los datos (o todo se guarda, o no se guarda nada).
 */
public class Servicio {

    // --- ATRIBUTOS ---
    private Integer idServicio;      // ID único del servicio.
    private Date fecha;              // Fecha en que se realizó el servicio.
    private Time hora;               // Hora del servicio.
    private String tipo;             // Descripción del tipo de servicio (ej: "Apertura de puerta").
    private EstadoServicio estado;   // Estado actual del servicio (PENDIENTE, EN PROGRESO, etc.).
    private BigDecimal monto;        // El costo del servicio. BigDecimal es ideal para dinero.
    private String metodoPago;       // Cómo se pagó (ej: "Efectivo", "Nequi").
    private Cliente cliente;         // El objeto Cliente asociado a este servicio.
    private Cerrajero cerrajero;     // El objeto Cerrajero que realizó el servicio.
    private static ConexionBD conexionBD = new ConexionBD(); // Objeto para la conexión a la BD.

    /**
     * --- CONSTRUCTOR (Para servicios nuevos) ---
     * Usado al crear un servicio desde el formulario. No recibe ID.
     */
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

    /**
     * --- CONSTRUCTOR (Para servicios existentes) ---
     * Usado al cargar un servicio desde la BD. Recibe un ID.
     * Llama al otro constructor usando "this(...)" para reutilizar la inicialización de los otros campos.
     */
    public Servicio(Integer id, Date fecha, Time hora, String tipo, EstadoServicio estado, BigDecimal monto, String metodoPago, Cliente cliente, Cerrajero cerrajero) {
        this(fecha, hora, tipo, estado, monto, metodoPago, cliente, cerrajero);
        this.idServicio = id;
    }

    /**
     * --- MÉTODO guardar ---
     * Orquesta el proceso de guardar un servicio COMPLETO. Esto implica guardar el cliente,
     * el cerrajero (si son nuevos) y finalmente el servicio, todo en una única "transacción".
     * @throws SQLException Si algo falla, la transacción se deshace (rollback).
     */
    public void guardar() throws SQLException {
        Connection conn = null;
        try {
            conn = conexionBD.getConnection();
            // Inicia una transacción. Desactiva el auto-commit para agrupar las operaciones.
            conn.setAutoCommit(false);
            
            // 1. Delega al objeto cliente la responsabilidad de guardarse a sí mismo.
            cliente.guardar(conn);
            
            // 2. Delega al objeto cerrajero la responsabilidad de guardarse a sí mismo.
            cerrajero.guardar(conn);
            
            // 3. Guarda el servicio, usando los IDs que ahora tienen el cliente y el cerrajero.
            String sql = "INSERT INTO servicio (fecha_s, hora_s, tipo_s, estado_s, monto_pago, metodo_pago, id_cliente, id_cerrajero) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pstmt.setDate(1, new java.sql.Date(fecha.getTime()));
                pstmt.setTime(2, hora);
                pstmt.setString(3, tipo);
                pstmt.setString(4, estado.getDescripcion());
                pstmt.setBigDecimal(5, monto);
                pstmt.setString(6, metodoPago);
                pstmt.setLong(7, cliente.getIdCliente()); // Usa el ID del cliente ya guardado.
                pstmt.setLong(8, cerrajero.getIdCerrajero()); // Usa el ID del cerrajero ya guardado.
                pstmt.executeUpdate();
                
                // Recupera el ID del servicio recién insertado.
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        this.idServicio = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("No se pudo obtener el ID del servicio.");
                    }
                }
            }
            // Si todo fue exitoso, confirma la transacción guardando todos los cambios.
            conn.commit();
        } catch (SQLException ex) {
            // Si ocurrió CUALQUIER error, deshace TODOS los cambios realizados en esta transacción.
            if (conn != null) conn.rollback();
            throw ex; // Relanza la excepción para que la capa superior (la vista) la maneje.
        } finally {
            // Cierra la conexión, sin importar si hubo éxito o error.
            conexionBD.closeConnection(conn);
        }
    }

    /**
     * --- MÉTODO actualizar ---
     * Orquesta la actualización de un servicio, incluyendo su cliente y cerrajero asociados.
     * También usa transacciones para garantizar la integridad de los datos.
     */
    public void actualizar() throws SQLException {
        Connection conn = null;
        try {
            conn = conexionBD.getConnection();
            conn.setAutoCommit(false); // Inicia la transacción.
            
            // 1. Delega al objeto cliente la responsabilidad de actualizarse.
            cliente.actualizar(conn);

            // 2. Llama a guardar para el cerrajero. Si es nuevo se inserta, si ya existe no hace nada.
            //    Esto es clave para cuando se elige "Otro" cerrajero en una actualización.
            cerrajero.guardar(conn);
            
            // 3. Actualiza la tabla de servicio.
            String sql = "UPDATE servicio SET fecha_s=?, hora_s=?, tipo_s=?, estado_s=?, monto_pago=?, metodo_pago=?, id_cerrajero=? WHERE id_servicio=?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDate(1, new java.sql.Date(fecha.getTime()));
                pstmt.setTime(2, hora);
                pstmt.setString(3, tipo);
                pstmt.setString(4, estado.getDescripcion());
                pstmt.setBigDecimal(5, monto);
                pstmt.setString(6, metodoPago);
                pstmt.setLong(7, cerrajero.getIdCerrajero()); // Asigna el ID del cerrajero (nuevo o existente).
                pstmt.setInt(8, idServicio); // Usa el ID del servicio para el WHERE.
                pstmt.executeUpdate();
            }
            
            conn.commit(); // Confirma la transacción.
        } catch (SQLException ex) {
            if (conn != null) conn.rollback(); // Deshace los cambios en caso de error.
            throw ex;
        } finally {
            conexionBD.closeConnection(conn);
        }
    }

    /**
     * --- MÉTODO eliminar (Estático) ---
     * Elimina un servicio de la base de datos basado en su ID.
     * Es "estático" porque se puede llamar sin necesidad de crear un objeto Servicio.
     * Ej: Servicio.eliminar(101);
     */
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
    
    /**
     * --- MÉTODO listarServicios (Estático) ---
     * Consulta y devuelve una lista de todos los servicios para mostrar en la tabla.
     * Permite un filtro opcional por nombre de cliente.
     * @return Una lista de vectores, donde cada vector es una fila para la JTable.
     */
    public static List<Vector<Object>> listarServicios(String filtroCliente) throws SQLException {
        List<Vector<Object>> servicios = new ArrayList<>();
        // El JOIN une las tablas servicio, cliente y cerrajero para obtener los nombres en una sola consulta.
        String sql = "SELECT s.id_servicio, s.fecha_s, s.tipo_s, c.nombre_c, ce.nombre_ce, s.estado_s, s.monto_pago " +
                     "FROM servicio s JOIN cliente c ON s.id_cliente = c.id_cliente JOIN cerrajero ce ON s.id_cerrajero = ce.id_cerrajero";
        if (filtroCliente != null && !filtroCliente.isEmpty()) {
            sql += " WHERE c.nombre_c ILIKE ?"; // ILIKE es como LIKE pero no distingue mayúsculas/minúsculas.
        }
        sql += " ORDER BY s.id_servicio DESC"; // Muestra los más recientes primero.
        
        Connection conn = null;
        try {
            conn = conexionBD.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            if (filtroCliente != null && !filtroCliente.isEmpty()) {
                pstmt.setString(1, "%" + filtroCliente + "%"); // Los % son comodines.
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Vector<Object> fila = new Vector<>(); // Un Vector es compatible con el modelo de JTable.
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

    /**
     * --- MÉTODO cargarPorId (Estático) ---
     * Carga todos los datos de un único servicio desde la BD, incluyendo los datos
     * completos de su cliente y cerrajero asociados. Devuelve un objeto Servicio completo.
     * @return Un objeto Servicio si lo encuentra, o null si no.
     */
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
                // Crea el objeto Cliente con los datos de la consulta.
                Cliente cliente = new Cliente(
                    rs.getLong("id_cliente"), rs.getString("nombre_c"),
                    String.valueOf(rs.getLong("telefono_c")), rs.getString("direccion_c"),
                    rs.getString("ciudad_c")
                );
                // Crea el objeto Cerrajero.
                Cerrajero cerrajero = new Cerrajero(
                    rs.getLong("id_cerrajero"), rs.getString("nombre_ce"),
                    String.valueOf(rs.getLong("telefono_ce"))
                );
                // Crea el objeto Servicio completo, asociando el cliente y cerrajero.
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
        return null; // Retorna null si no se encontró un servicio con ese ID.
    }

    // --- GETTERS Y SETTERS --- 
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
