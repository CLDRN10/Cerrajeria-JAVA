import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;
import java.util.Vector;

public class VistaPrincipal {

    // --- Variables de Estado ---
    private Integer idServicioSeleccionado = null; // Usamos Integer para poder asignarle null

    // --- Componentes vinculados desde el .form ---
    private JPanel rootPanel;
    private JTabbedPane tabbedPane;
    private JPanel panelRegistrar;
    private JComboBox<String> comboTipoServicio;
    private JTextField txtNombreCliente;
    private JTextField txtTelefonoCliente;
    private JTextField txtDireccionCliente;
    private JComboBox<String> comboMunicipio;
    private JSpinner spinnerFecha;
    private JSpinner spinnerHora;
    private JSpinner spinnerValorServicio;
    private JComboBox<String> comboMetodoPago;
    private JComboBox<String> comboCerrajero;
    private JComboBox<String> comboEstado;
    private JButton btnGuardarServicio;
    private JPanel panelConsultar;
    private JPanel panelOtroCerrajero;
    private JTextField txtNombreOtroCerrajero;
    private JTextField txtTelefonoOtroCerrajero;
    private JTable tablaServicios;
    // --- Nuevos botones para CRUD ---
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiarFormulario;


    public VistaPrincipal() {
        inicializarComponentesLogicos();
        agregarListeners();
    }

    private void inicializarComponentesLogicos() {
        // Configuración de Spinners
        spinnerFecha.setModel(new SpinnerDateModel());
        spinnerFecha.setEditor(new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy"));
        spinnerHora.setModel(new SpinnerDateModel());
        spinnerHora.setEditor(new JSpinner.DateEditor(spinnerHora, "hh:mm a"));
        spinnerValorServicio.setModel(new SpinnerNumberModel(50000.0, 0.0, 10000000.0, 1000.0));

        // Carga de datos en ComboBoxes
        comboTipoServicio.setModel(new DefaultComboBoxModel<>(new String[]{"Seleccionar", "Apertura de automóvil", "Apertura de caja fuerte", "Apertura de candado", "Apertura de motocicleta", "Apertura de puerta residencial", "Cambio de clave de automóvil", "Cambio de clave de motocicleta", "Cambio de clave residencial", "Duplicado de llave", "Elaboración de llaves", "Instalación de alarma", "Instalación de chapa", "Reparación general"}));
        comboMunicipio.setModel(new DefaultComboBoxModel<>(new String[]{"Seleccionar", "Bucaramanga", "Floridablanca", "Piedecuesta"}));
        comboMetodoPago.setModel(new DefaultComboBoxModel<>(new String[]{"Nequi", "Efectivo"}));
        comboCerrajero.setModel(new DefaultComboBoxModel<>(new String[]{"Seleccionar", "Jose Hernandez", "Otro"}));
        comboEstado.setModel(new DefaultComboBoxModel<>(new String[]{"PENDIENTE", "EN PROCESO", "FINALIZADO", "CANCELADO"}));

        reiniciarFormulario();
    }

    private void agregarListeners() {
        // Listener para mostrar/ocultar panel "Otro Cerrajero"
        comboCerrajero.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                panelOtroCerrajero.setVisible("Otro".equals(e.getItem()));
                Window window = SwingUtilities.getWindowAncestor(rootPanel);
                if (window != null) window.pack();
            }
        });

        // Listeners de los botones CRUD
        btnGuardarServicio.addActionListener(e -> guardarServicio());
        btnActualizar.addActionListener(e -> actualizarServicio());
        btnEliminar.addActionListener(e -> eliminarServicio());
        btnLimpiarFormulario.addActionListener(e -> reiniciarFormulario());

        // Listener para cargar datos al cambiar a la pestaña de consulta
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == panelConsultar) {
                cargarServicios();
            }
        });

        // --- NUEVO: Listener para seleccionar un servicio de la tabla ---
        tablaServicios.getSelectionModel().addListSelectionListener(e -> {
            // Evitar que el evento se dispare dos veces (al soltar el mouse)
            if (!e.getValueIsAdjusting() && tablaServicios.getSelectedRow() != -1) {
                int filaSeleccionada = tablaServicios.getSelectedRow();
                // Obtener el ID del servicio de la primera columna de la tabla
                Integer idServicio = (Integer) tablaServicios.getValueAt(filaSeleccionada, 0);
                cargarDatosServicioEnFormulario(idServicio);
            }
        });
    }

    private void reiniciarFormulario() {
        // Limpiar campos y reiniciar combos
        txtNombreCliente.setText("");
        txtTelefonoCliente.setText("");
        txtDireccionCliente.setText("");
        txtNombreOtroCerrajero.setText("");
        txtTelefonoOtroCerrajero.setText("");
        comboTipoServicio.setSelectedIndex(0);
        comboMunicipio.setSelectedIndex(0);
        comboCerrajero.setSelectedIndex(0);
        comboMetodoPago.setSelectedIndex(0);
        comboEstado.setSelectedIndex(0);
        spinnerFecha.setValue(new Date());
        spinnerHora.setValue(new Date());
        spinnerValorServicio.setValue(50000.0);
        panelOtroCerrajero.setVisible(false);

        // --- GESTIÓN DE ESTADO ---
        idServicioSeleccionado = null; // Limpiar el ID seleccionado
        tablaServicios.clearSelection(); // Limpiar la selección de la tabla

        // --- GESTIÓN DE BOTONES ---
        btnGuardarServicio.setVisible(true);
        btnActualizar.setVisible(false);
        btnEliminar.setVisible(false);
        btnLimpiarFormulario.setVisible(false); // Este solo aparece al editar

        Window window = SwingUtilities.getWindowAncestor(rootPanel);
        if (window != null) window.pack();
    }

    private boolean validarCampos() {
        // (La lógica de validación no cambia, se mantiene igual)
        if (txtNombreCliente.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(rootPanel, "El nombre del cliente no puede estar vacío.", "Error de Validación", JOptionPane.ERROR_MESSAGE); return false; }
        if (txtTelefonoCliente.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(rootPanel, "El teléfono del cliente no puede estar vacío.", "Error de Validación", JOptionPane.ERROR_MESSAGE); return false; }
        if (comboTipoServicio.getSelectedIndex() == 0) { JOptionPane.showMessageDialog(rootPanel, "Debe seleccionar un tipo de servicio.", "Error de Validación", JOptionPane.ERROR_MESSAGE); return false; }
        if (comboMunicipio.getSelectedIndex() == 0) { JOptionPane.showMessageDialog(rootPanel, "Debe seleccionar un municipio.", "Error de Validación", JOptionPane.ERROR_MESSAGE); return false; }
        if (comboCerrajero.getSelectedIndex() == 0) { JOptionPane.showMessageDialog(rootPanel, "Debe seleccionar un cerrajero.", "Error de Validación", JOptionPane.ERROR_MESSAGE); return false; }
        if ("Otro".equals(comboCerrajero.getSelectedItem())) {
            if (txtNombreOtroCerrajero.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(rootPanel, "El nombre del 'Otro' cerrajero no puede estar vacío.", "Error de Validación", JOptionPane.ERROR_MESSAGE); return false; }
            if (txtTelefonoOtroCerrajero.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(rootPanel, "El teléfono del 'Otro' cerrajero no puede estar vacío.", "Error de Validación", JOptionPane.ERROR_MESSAGE); return false; }
        }
        return true;
    }

    // --- MÉTODOS CRUD ---

    private void guardarServicio() {
        if (!validarCampos()) return;
        // (Lógica de guardar sin cambios)
        Connection conn = null;
        try {
            ConexionBD conexionBD = new ConexionBD();
            conn = conexionBD.getConnection();
            conn.setAutoCommit(false);
            long telefonoClienteNum = Long.parseLong(txtTelefonoCliente.getText());

            // Insertar Cliente
            String sqlCliente = "INSERT INTO cliente (nombre_c, telefono_c, direccion_c, ciudad_c) VALUES (?, ?, ?, ?) RETURNING id_cliente";
            long idCliente; 
            try (PreparedStatement pstmt = conn.prepareStatement(sqlCliente)) {
                pstmt.setString(1, txtNombreCliente.getText());
                pstmt.setLong(2, telefonoClienteNum);
                pstmt.setString(3, txtDireccionCliente.getText());
                pstmt.setString(4, (String) comboMunicipio.getSelectedItem());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) { idCliente = rs.getLong(1); } 
                else { throw new SQLException("No se pudo crear el cliente."); }
            }

            // Gestionar Cerrajero
            long idCerrajero;
            String cerrajeroSeleccionado = (String) comboCerrajero.getSelectedItem();
            if ("Otro".equals(cerrajeroSeleccionado)) {
                 String sqlCerrajero = "INSERT INTO cerrajero (nombre_ce, telefono_ce) VALUES (?, ?) RETURNING id_cerrajero";
                 try (PreparedStatement pstmt = conn.prepareStatement(sqlCerrajero)){
                     pstmt.setString(1, txtNombreOtroCerrajero.getText());
                     pstmt.setLong(2, Long.parseLong(txtTelefonoOtroCerrajero.getText()));
                     ResultSet rs = pstmt.executeQuery();
                     if(rs.next()){ idCerrajero = rs.getLong(1); } else { throw new SQLException("No se pudo crear el cerrajero 'Otro'");}
                 }
            } else {
                 // Para simplificar, asumimos que el cerrajero ya existe o creamos uno con teléfono 0
                 String sqlBusca = "SELECT id_cerrajero FROM cerrajero WHERE nombre_ce = ? LIMIT 1";
                 try (PreparedStatement pstmt = conn.prepareStatement(sqlBusca)){
                     pstmt.setString(1, cerrajeroSeleccionado);
                     ResultSet rs = pstmt.executeQuery();
                     if(rs.next()){ idCerrajero = rs.getLong(1); } else {
                          String sqlCrea = "INSERT INTO cerrajero (nombre_ce, telefono_ce) VALUES (?, 0) RETURNING id_cerrajero";
                          try (PreparedStatement pstmtCrea = conn.prepareStatement(sqlCrea)){
                              pstmtCrea.setString(1, cerrajeroSeleccionado);
                              ResultSet rsCrea = pstmtCrea.executeQuery();
                              if(rsCrea.next()){ idCerrajero = rsCrea.getLong(1); } else { throw new SQLException("No se pudo crear el cerrajero predeterminado.");}
                          }
                     }
                 }
            }

            // Insertar Servicio
            String sqlServicio = "INSERT INTO servicio (fecha_s, hora_s, tipo_s, estado_s, monto_pago, metodo_pago, id_cliente, id_cerrajero) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlServicio)) {
                pstmt.setDate(1, new java.sql.Date(((Date) spinnerFecha.getValue()).getTime()));
                pstmt.setTime(2, new Time(((Date) spinnerHora.getValue()).getTime()));
                pstmt.setString(3, (String) comboTipoServicio.getSelectedItem());
                pstmt.setString(4, (String) comboEstado.getSelectedItem());
                pstmt.setBigDecimal(5, BigDecimal.valueOf((Double) spinnerValorServicio.getValue()));
                pstmt.setString(6, (String) comboMetodoPago.getSelectedItem());
                pstmt.setLong(7, idCliente);
                pstmt.setLong(8, idCerrajero);
                if (pstmt.executeUpdate() == 0) throw new SQLException("No se pudo guardar el servicio.");
            }
            conn.commit();
            JOptionPane.showMessageDialog(rootPanel, "Servicio guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            reiniciarFormulario();
        } catch (Exception ex) {
            try { if (conn != null) conn.rollback(); } catch (SQLException e) { e.printStackTrace(); }
            JOptionPane.showMessageDialog(rootPanel, "Error al guardar en la BD: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }
    
    private void actualizarServicio() {
        if (idServicioSeleccionado == null) return;
        if (!validarCampos()) return;

        // Diálogo de confirmación
        int confirm = JOptionPane.showConfirmDialog(rootPanel, "¿Está seguro de que desea actualizar este servicio?", "Confirmar Actualización", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        Connection conn = null;
        try {
            ConexionBD conexionBD = new ConexionBD();
            conn = conexionBD.getConnection();
            conn.setAutoCommit(false);

            // 1. Obtener IDs de cliente y cerrajero actuales del servicio
            long idCliente, idCerrajero;
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT id_cliente, id_cerrajero FROM servicio WHERE id_servicio = ?")) {
                pstmt.setInt(1, idServicioSeleccionado);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    idCliente = rs.getLong("id_cliente");
                    idCerrajero = rs.getLong("id_cerrajero");
                } else {
                    throw new SQLException("No se encontró el servicio a actualizar.");
                }
            }

            // 2. Actualizar Cliente
            try (PreparedStatement pstmt = conn.prepareStatement("UPDATE cliente SET nombre_c = ?, telefono_c = ?, direccion_c = ?, ciudad_c = ? WHERE id_cliente = ?")) {
                pstmt.setString(1, txtNombreCliente.getText());
                pstmt.setLong(2, Long.parseLong(txtTelefonoCliente.getText()));
                pstmt.setString(3, txtDireccionCliente.getText());
                pstmt.setString(4, (String) comboMunicipio.getSelectedItem());
                pstmt.setLong(5, idCliente);
                pstmt.executeUpdate();
            }

            // 3. Actualizar Cerrajero (Simplificado: Solo se actualiza el cerrajero existente. No se crea uno nuevo ni se maneja el cambio a "Otro")
             try (PreparedStatement pstmt = conn.prepareStatement("UPDATE cerrajero SET nombre_ce = ? WHERE id_cerrajero = ?")) {
                 pstmt.setString(1, (String)comboCerrajero.getSelectedItem());
                 pstmt.setLong(2, idCerrajero);
                 pstmt.executeUpdate();
             }

            // 4. Actualizar Servicio
            String sqlServicio = "UPDATE servicio SET fecha_s=?, hora_s=?, tipo_s=?, estado_s=?, monto_pago=?, metodo_pago=? WHERE id_servicio=?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlServicio)) {
                pstmt.setDate(1, new java.sql.Date(((Date) spinnerFecha.getValue()).getTime()));
                pstmt.setTime(2, new Time(((Date) spinnerHora.getValue()).getTime()));
                pstmt.setString(3, (String) comboTipoServicio.getSelectedItem());
                pstmt.setString(4, (String) comboEstado.getSelectedItem());
                pstmt.setBigDecimal(5, BigDecimal.valueOf((Double) spinnerValorServicio.getValue()));
                pstmt.setString(6, (String) comboMetodoPago.getSelectedItem());
                pstmt.setInt(7, idServicioSeleccionado);
                pstmt.executeUpdate();
            }

            conn.commit();
            JOptionPane.showMessageDialog(rootPanel, "Servicio actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            reiniciarFormulario();
            cargarServicios(); // Recargar la tabla para ver los cambios

        } catch (Exception ex) {
            try { if (conn != null) conn.rollback(); } catch (SQLException e) { e.printStackTrace(); }
            JOptionPane.showMessageDialog(rootPanel, "Error al actualizar el servicio: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    private void eliminarServicio() {
        if (idServicioSeleccionado == null) return;

        int confirm = JOptionPane.showConfirmDialog(rootPanel, "¿Está seguro de que desea eliminar este servicio?\nEsta acción no se puede deshacer.", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        Connection conn = null;
        try {
            ConexionBD conexionBD = new ConexionBD();
            conn = conexionBD.getConnection();
             // La BD está configurada con ON DELETE CASCADE, así que solo necesitamos borrar el servicio.
             // El cliente y el cerrajero PERMANECERÁN en la BD, lo cual es correcto.
            String sql = "DELETE FROM servicio WHERE id_servicio = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, idServicioSeleccionado);
                int filasAfectadas = pstmt.executeUpdate();
                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(rootPanel, "Servicio eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    reiniciarFormulario();
                    cargarServicios(); // Recargar la tabla
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(rootPanel, "Error al eliminar el servicio: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    private void cargarServicios() {
        // (Lógica de cargar servicios sin cambios)
        DefaultTableModel modelo = new DefaultTableModel(new String[]{"ID", "Fecha", "Tipo Servicio", "Cliente", "Cerrajero", "Estado", "Monto"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        String sql = "SELECT s.id_servicio, s.fecha_s, s.tipo_s, c.nombre_c, ce.nombre_ce, s.estado_s, s.monto_pago " +
                     "FROM servicio s JOIN cliente c ON s.id_cliente = c.id_cliente JOIN cerrajero ce ON s.id_cerrajero = ce.id_cerrajero " +
                     "ORDER BY s.id_servicio DESC";
        Connection conn = null;
        try {
            ConexionBD conexionBD = new ConexionBD();
            conn = conexionBD.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Vector<Object> fila = new Vector<>();
                fila.add(rs.getInt("id_servicio"));
                fila.add(rs.getDate("fecha_s"));
                fila.add(rs.getString("tipo_s"));
                fila.add(rs.getString("nombre_c"));
                fila.add(rs.getString("nombre_ce"));
                fila.add(rs.getString("estado_s"));
                fila.add(rs.getBigDecimal("monto_pago"));
                modelo.addRow(fila);
            }
            tablaServicios.setModel(modelo);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(rootPanel, "Error al cargar servicios: " + ex.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void cargarDatosServicioEnFormulario(int idServicio) {
        String sql = "SELECT s.*, c.nombre_c, c.telefono_c, c.direccion_c, c.ciudad_c, ce.nombre_ce " +
                     "FROM servicio s JOIN cliente c ON s.id_cliente = c.id_cliente JOIN cerrajero ce ON s.id_cerrajero = ce.id_cerrajero " +
                     "WHERE s.id_servicio = ?";
        Connection conn = null;
        try {
            ConexionBD conexionBD = new ConexionBD();
            conn = conexionBD.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idServicio);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // --- Cargar datos en el formulario ---
                txtNombreCliente.setText(rs.getString("nombre_c"));
                txtTelefonoCliente.setText(String.valueOf(rs.getLong("telefono_c")));
                txtDireccionCliente.setText(rs.getString("direccion_c"));
                spinnerFecha.setValue(rs.getDate("fecha_s"));
                spinnerHora.setValue(rs.getTime("hora_s"));
                spinnerValorServicio.setValue(rs.getBigDecimal("monto_pago").doubleValue());
                
                // Seleccionar item correcto en ComboBoxes
                comboTipoServicio.setSelectedItem(rs.getString("tipo_s"));
                comboMunicipio.setSelectedItem(rs.getString("ciudad_c"));
                comboMetodoPago.setSelectedItem(rs.getString("metodo_pago"));
                comboEstado.setSelectedItem(rs.getString("estado_s"));
                comboCerrajero.setSelectedItem(rs.getString("nombre_ce"));
                
                // --- GESTIÓN DE ESTADO ---
                this.idServicioSeleccionado = idServicio; // Guardar el ID del servicio que estamos editando
                
                // --- GESTIÓN DE BOTONES ---
                btnGuardarServicio.setVisible(false); // Ocultar Guardar
                btnActualizar.setVisible(true);     // Mostrar Actualizar
                btnEliminar.setVisible(true);       // Mostrar Eliminar
                btnLimpiarFormulario.setVisible(true);

                // Cambiar a la pestaña de registro para ver/editar los datos
                tabbedPane.setSelectedComponent(panelRegistrar);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(rootPanel, "Error al cargar el detalle del servicio: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    // --- Getters para los componentes (necesarios para el arranque de la UI) ---
    public JPanel getMainPanel() { return rootPanel; }
}
