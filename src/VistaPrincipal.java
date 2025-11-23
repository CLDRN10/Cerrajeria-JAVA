import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.*;
import java.util.Vector;

public class VistaPrincipal {

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
    // --- Componente de tabla añadido ---
    private JTable tablaServicios;

    public VistaPrincipal() {
        inicializarComponentesLogicos();
        agregarListeners();
    }

    private void inicializarComponentesLogicos() {
        // --- Configuración de Spinners ---
        spinnerFecha.setModel(new SpinnerDateModel());
        JSpinner.DateEditor deFecha = new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy");
        spinnerFecha.setEditor(deFecha);

        spinnerHora.setModel(new SpinnerDateModel());
        JSpinner.DateEditor deHora = new JSpinner.DateEditor(spinnerHora, "hh:mm a");
        spinnerHora.setEditor(deHora);

        spinnerValorServicio.setModel(new SpinnerNumberModel(50000.0, 0.0, 10000000.0, 1000.0));

        // --- Carga de datos en ComboBoxes ---
        comboTipoServicio.setModel(new DefaultComboBoxModel<>(new String[]{"Seleccionar", "Apertura de automóvil", "Apertura de caja fuerte", "Apertura de candado", "Apertura de motocicleta", "Apertura de puerta residencial", "Cambio de clave de automóvil", "Cambio de clave de motocicleta", "Cambio de clave residencial", "Duplicado de llave", "Elaboración de llaves", "Instalación de alarma", "Instalación de chapa", "Reparación general"}));
        comboMunicipio.setModel(new DefaultComboBoxModel<>(new String[]{"Seleccionar", "Bucaramanga", "Floridablanca", "Piedecuesta"}));
        comboMetodoPago.setModel(new DefaultComboBoxModel<>(new String[]{"Nequi", "Efectivo"}));
        comboCerrajero.setModel(new DefaultComboBoxModel<>(new String[]{"Seleccionar", "Jose Hernandez", "Otro"})); // A futuro, esto debería cargarse desde la BD

        DefaultComboBoxModel<String> estadoModel = new DefaultComboBoxModel<>();
        estadoModel.addElement("PENDIENTE");
        estadoModel.addElement("EN PROCESO");
        estadoModel.addElement("FINALIZADO");
        estadoModel.addElement("CANCELADO");
        comboEstado.setModel(estadoModel);

        reiniciarFormulario(); // Establece el estado inicial limpio
    }

    private void agregarListeners() {
        comboCerrajero.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String seleccion = (String) e.getItem();
                boolean esOtro = "Otro".equals(seleccion);
                panelOtroCerrajero.setVisible(esOtro);
                Window window = SwingUtilities.getWindowAncestor(rootPanel);
                if (window != null) {
                    window.pack();
                }
            }
        });

        btnGuardarServicio.addActionListener(e -> guardarServicio());

        // --- NUEVO LISTENER: Cargar datos al seleccionar la pestaña de consulta ---
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == panelConsultar) {
                cargarServicios();
            }
        });
    }

    private void reiniciarFormulario() {
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
        Window window = SwingUtilities.getWindowAncestor(rootPanel);
        if (window != null) {
            window.pack();
        }
    }

    private boolean validarCampos() {
        if (txtNombreCliente.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(rootPanel, "El nombre del cliente no puede estar vacío.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (txtTelefonoCliente.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(rootPanel, "El teléfono del cliente no puede estar vacío.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (comboTipoServicio.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(rootPanel, "Debe seleccionar un tipo de servicio.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (comboMunicipio.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(rootPanel, "Debe seleccionar un municipio.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (comboCerrajero.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(rootPanel, "Debe seleccionar un cerrajero.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if ("Otro".equals(comboCerrajero.getSelectedItem())) {
            if (txtNombreOtroCerrajero.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(rootPanel, "El nombre del 'Otro' cerrajero no puede estar vacío.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (txtTelefonoOtroCerrajero.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(rootPanel, "El teléfono del 'Otro' cerrajero no puede estar vacío.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private void guardarServicio() {
        if (!validarCampos()) {
            return;
        }

        Connection conn = null;
        long idCliente = -1;
        long idCerrajero = -1;

        try {
            long telefonoClienteNum = Long.parseLong(txtTelefonoCliente.getText());
            ConexionBD conexionBD = new ConexionBD();
            conn = conexionBD.getConnection();
            conn.setAutoCommit(false);

            String sqlCliente = "INSERT INTO cliente (nombre_c, telefono_c, direccion_c, ciudad_c) VALUES (?, ?, ?, ?) RETURNING id_cliente";
            try (PreparedStatement pstmtCliente = conn.prepareStatement(sqlCliente)) {
                pstmtCliente.setString(1, txtNombreCliente.getText());
                pstmtCliente.setLong(2, telefonoClienteNum);
                pstmtCliente.setString(3, txtDireccionCliente.getText());
                pstmtCliente.setString(4, (String) comboMunicipio.getSelectedItem());
                ResultSet rs = pstmtCliente.executeQuery();
                if (rs.next()) { idCliente = rs.getLong(1); } 
                else { throw new SQLException("No se pudo crear el cliente."); }
            }

            String cerrajeroSeleccionado = (String) comboCerrajero.getSelectedItem();
            if ("Otro".equals(cerrajeroSeleccionado)) {
                long telefonoOtroCerrajeroNum = Long.parseLong(txtTelefonoOtroCerrajero.getText());
                String sqlCerrajero = "INSERT INTO cerrajero (nombre_ce, telefono_ce) VALUES (?, ?) RETURNING id_cerrajero";
                try (PreparedStatement pstmtCerrajero = conn.prepareStatement(sqlCerrajero)) {
                    pstmtCerrajero.setString(1, txtNombreOtroCerrajero.getText());
                    pstmtCerrajero.setLong(2, telefonoOtroCerrajeroNum);
                    ResultSet rs = pstmtCerrajero.executeQuery();
                    if (rs.next()) { idCerrajero = rs.getLong(1); } 
                    else { throw new SQLException("No se pudo crear el cerrajero 'Otro'."); }
                }
            } else {
                String sqlBuscaCerrajero = "SELECT id_cerrajero FROM cerrajero WHERE nombre_ce = ? LIMIT 1";
                try (PreparedStatement pstmtBusca = conn.prepareStatement(sqlBuscaCerrajero)){
                    pstmtBusca.setString(1, cerrajeroSeleccionado);
                    ResultSet rs = pstmtBusca.executeQuery();
                    if (rs.next()){ idCerrajero = rs.getLong(1); }
                    else {
                        String sqlCreaCerrajero = "INSERT INTO cerrajero (nombre_ce, telefono_ce) VALUES (?, 0) RETURNING id_cerrajero";
                        try(PreparedStatement pstmtCrea = conn.prepareStatement(sqlCreaCerrajero)) {
                            pstmtCrea.setString(1, cerrajeroSeleccionado);
                            ResultSet rsCrea = pstmtCrea.executeQuery();
                            if(rsCrea.next()){ idCerrajero = rsCrea.getLong(1); }
                            else { throw new SQLException("No se pudo encontrar ni crear al cerrajero seleccionado."); }
                        }
                    }
                }
            }

            String sqlServicio = "INSERT INTO servicio (fecha_s, hora_s, tipo_s, estado_s, monto_pago, metodo_pago, id_cliente, id_cerrajero) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmtServicio = conn.prepareStatement(sqlServicio)) {
                pstmtServicio.setDate(1, new java.sql.Date(((Date) spinnerFecha.getValue()).getTime()));
                pstmtServicio.setTime(2, new Time(((Date) spinnerHora.getValue()).getTime()));
                pstmtServicio.setString(3, (String) comboTipoServicio.getSelectedItem());
                pstmtServicio.setString(4, (String) comboEstado.getSelectedItem());
                pstmtServicio.setBigDecimal(5, BigDecimal.valueOf((Double) spinnerValorServicio.getValue()));
                pstmtServicio.setString(6, (String) comboMetodoPago.getSelectedItem());
                pstmtServicio.setLong(7, idCliente);
                pstmtServicio.setLong(8, idCerrajero);
                if (pstmtServicio.executeUpdate() == 0) {
                    throw new SQLException("No se pudo guardar el servicio.");
                }
            }

            conn.commit();
            JOptionPane.showMessageDialog(rootPanel, "Servicio guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            reiniciarFormulario();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(rootPanel, "El teléfono debe ser un número válido y sin puntos ni comas.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
             if (conn != null) { try { conn.rollback(); } catch (SQLException e) { e.printStackTrace(); } }
        } catch (SQLException ex) {
            try { if (conn != null) conn.rollback(); } catch (SQLException e) { e.printStackTrace(); }
            ex.printStackTrace();
            JOptionPane.showMessageDialog(rootPanel, "Error al guardar en la base de datos: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    // --- NUEVO MÉTODO: Cargar y mostrar los datos en la tabla ---
    private void cargarServicios() {
        String[] columnas = {"ID", "Fecha", "Tipo Servicio", "Cliente", "Cerrajero", "Estado", "Monto"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hace que las celdas no sean editables
            }
        };

        String sql = "SELECT s.id_servicio, s.fecha_s, s.tipo_s, c.nombre_c, ce.nombre_ce, s.estado_s, s.monto_pago " +
                     "FROM servicio s " +
                     "JOIN cliente c ON s.id_cliente = c.id_cliente " +
                     "JOIN cerrajero ce ON s.id_cerrajero = ce.id_cerrajero " +
                     "ORDER BY s.fecha_s DESC, s.hora_s DESC";

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

            if (tablaServicios != null) {
                tablaServicios.setModel(modelo);
            } else {
                 JOptionPane.showMessageDialog(rootPanel, "Error: La tabla 'tablaServicios' no se ha inicializado.\nPor favor, asegúrese de haberla añadido en el diseñador del formulario.", "Error de Interfaz", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(rootPanel, "Error al cargar los servicios: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public JPanel getMainPanel() { return rootPanel; }
    // ... (resto de getters) ...
    public JTabbedPane getTabbedPane() { return tabbedPane; }
    public JPanel getPanelRegistrar() { return panelRegistrar; }
    public JPanel getPanelConsultar() { return panelConsultar; }
    public JPanel getPanelOtroCerrajero() { return panelOtroCerrajero; }
    public JTextField getTxtNombreOtroCerrajero() { return txtNombreOtroCerrajero; }
    public JTextField getTxtTelefonoOtroCerrajero() { return txtTelefonoOtroCerrajero; }
    // --- NUEVO GETTER para la tabla ---
    public JTable getTablaServicios() { return tablaServicios; }
}
