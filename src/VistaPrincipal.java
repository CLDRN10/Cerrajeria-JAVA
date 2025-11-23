import javax.swing.*;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.*;

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

    public VistaPrincipal() {
        inicializarComponentesLogicos();
        agregarListeners();
    }

    private void inicializarComponentesLogicos() {
        // --- Configuración de Spinners ---
        spinnerFecha.setModel(new SpinnerDateModel());
        JSpinner.DateEditor deFecha = new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy");
        spinnerFecha.setEditor(deFecha);
        spinnerFecha.setValue(new Date());

        spinnerHora.setModel(new SpinnerDateModel());
        JSpinner.DateEditor deHora = new JSpinner.DateEditor(spinnerHora, "hh:mm a");
        spinnerHora.setEditor(deHora);
        spinnerHora.setValue(new Date());

        spinnerValorServicio.setModel(new SpinnerNumberModel(50000.0, 0.0, 10000000.0, 1000.0));

        // --- Carga de datos en ComboBoxes ---
        comboTipoServicio.setModel(new DefaultComboBoxModel<>(new String[]{"Seleccionar", "Apertura de automóvil", "Apertura de caja fuerte", "Apertura de candado", "Apertura de motocicleta", "Apertura de puerta residencial", "Cambio de clave de automóvil", "Cambio de clave de motocicleta", "Cambio de clave residencial", "Duplicado de llave", "Elaboración de llaves", "Instalación de alarma", "Instalación de chapa", "Reparación general"}));
        comboMunicipio.setModel(new DefaultComboBoxModel<>(new String[]{"Seleccionar", "Bucaramanga", "Floridablanca", "Piedecuesta"}));
        comboMetodoPago.setModel(new DefaultComboBoxModel<>(new String[]{"Nequi", "Efectivo"}));
        comboCerrajero.setModel(new DefaultComboBoxModel<>(new String[]{"Seleccionar", "Jose Hernandez", "Otro"})); // A futuro, esto debería cargarse desde la BD

        DefaultComboBoxModel<String> estadoModel = new DefaultComboBoxModel<>();
        estadoModel.addElement("Pendiente");
        estadoModel.addElement("En proceso");
        estadoModel.addElement("Finalizado");
        estadoModel.addElement("Cancelado");
        comboEstado.setModel(estadoModel);

        panelOtroCerrajero.setVisible(false);
    }

    private void agregarListeners() {
        comboCerrajero.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String seleccion = (String) e.getItem();
                boolean esOtro = "Otro".equals(seleccion);
                panelOtroCerrajero.setVisible(esOtro);
                SwingUtilities.getWindowAncestor(rootPanel).pack();
            }
        });

        btnGuardarServicio.addActionListener(e -> guardarServicio());
    }

    private void guardarServicio() {
        Connection conn = null;
        long idCliente = -1;
        long idCerrajero = -1;

        try {
            // Validar y convertir números de teléfono ANTES de la transacción
            long telefonoClienteNum;
            try {
                telefonoClienteNum = Long.parseLong(txtTelefonoCliente.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(rootPanel, "El teléfono del cliente debe ser un número válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ConexionBD conexionBD = new ConexionBD();
            conn = conexionBD.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // 1. Insertar Cliente y obtener su ID
            String sqlCliente = "INSERT INTO cliente (nombre_c, telefono_c, direccion_c, ciudad_c) VALUES (?, ?, ?, ?) RETURNING id_cliente";
            try (PreparedStatement pstmtCliente = conn.prepareStatement(sqlCliente)) {
                pstmtCliente.setString(1, txtNombreCliente.getText());
                pstmtCliente.setLong(2, telefonoClienteNum); // Usar el número convertido
                pstmtCliente.setString(3, txtDireccionCliente.getText());
                pstmtCliente.setString(4, (String) comboMunicipio.getSelectedItem());

                ResultSet rs = pstmtCliente.executeQuery();
                if (rs.next()) {
                    idCliente = rs.getLong(1);
                } else {
                    throw new SQLException("No se pudo crear el cliente.");
                }
            }

            // 2. Gestionar Cerrajero y obtener su ID
            String cerrajeroSeleccionado = (String) comboCerrajero.getSelectedItem();
            if ("Otro".equals(cerrajeroSeleccionado)) {
                long telefonoOtroCerrajeroNum;
                 try {
                    telefonoOtroCerrajeroNum = Long.parseLong(txtTelefonoOtroCerrajero.getText());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(rootPanel, "El teléfono del cerrajero 'Otro' debe ser un número válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String sqlCerrajero = "INSERT INTO cerrajero (nombre_ce, telefono_ce) VALUES (?, ?) RETURNING id_cerrajero";
                try (PreparedStatement pstmtCerrajero = conn.prepareStatement(sqlCerrajero)) {
                    pstmtCerrajero.setString(1, txtNombreOtroCerrajero.getText());
                    pstmtCerrajero.setLong(2, telefonoOtroCerrajeroNum); // Usar el número convertido
                    ResultSet rs = pstmtCerrajero.executeQuery();
                    if (rs.next()) {
                        idCerrajero = rs.getLong(1);
                    } else {
                        throw new SQLException("No se pudo crear el cerrajero 'Otro'.");
                    }
                }
            } else {
                String sqlBuscaCerrajero = "SELECT id_cerrajero FROM cerrajero WHERE nombre_ce = ? LIMIT 1";
                 try (PreparedStatement pstmtBusca = conn.prepareStatement(sqlBuscaCerrajero)){
                    pstmtBusca.setString(1, cerrajeroSeleccionado);
                    ResultSet rs = pstmtBusca.executeQuery();
                    if (rs.next()){
                        idCerrajero = rs.getLong(1);
                    } else {
                        // Si no existe, lo creamos con un teléfono placeholder
                        String sqlCreaCerrajero = "INSERT INTO cerrajero (nombre_ce, telefono_ce) VALUES (?, 0) RETURNING id_cerrajero";
                        try(PreparedStatement pstmtCrea = conn.prepareStatement(sqlCreaCerrajero)) {
                            pstmtCrea.setString(1, cerrajeroSeleccionado);
                            ResultSet rsCrea = pstmtCrea.executeQuery();
                            if(rsCrea.next()){
                                idCerrajero = rsCrea.getLong(1);
                            } else {
                                throw new SQLException("No se pudo encontrar ni crear al cerrajero seleccionado.");
                            }
                        }
                    }
                 }
            }

            // 3. Insertar el Servicio
            String sqlServicio = "INSERT INTO servicio (fecha_s, hora_s, tipo_s, estado_s, monto_pago, metodo_pago, id_cliente, id_cerrajero) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmtServicio = conn.prepareStatement(sqlServicio)) {
                java.util.Date fechaUtil = (java.util.Date) spinnerFecha.getValue();
                pstmtServicio.setDate(1, new java.sql.Date(fechaUtil.getTime()));

                java.util.Date horaUtil = (java.util.Date) spinnerHora.getValue();
                pstmtServicio.setTime(2, new Time(horaUtil.getTime()));

                pstmtServicio.setString(3, (String) comboTipoServicio.getSelectedItem());
                pstmtServicio.setString(4, (String) comboEstado.getSelectedItem());

                Double valorSpinner = (Double) spinnerValorServicio.getValue();
                pstmtServicio.setBigDecimal(5, BigDecimal.valueOf(valorSpinner));

                pstmtServicio.setString(6, (String) comboMetodoPago.getSelectedItem());
                pstmtServicio.setLong(7, idCliente);
                pstmtServicio.setLong(8, idCerrajero);

                int filasAfectadas = pstmtServicio.executeUpdate();
                if (filasAfectadas == 0) {
                    throw new SQLException("No se pudo guardar el servicio.");
                }
            }

            conn.commit(); // Confirmar transacción
            JOptionPane.showMessageDialog(rootPanel, "Servicio guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            try {
                if (conn != null) conn.rollback(); // Revertir en caso de error
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ex.printStackTrace();
            JOptionPane.showMessageDialog(rootPanel, "Error al guardar en la base de datos: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            // Captura cualquier otra excepción inesperada, como la de formato de número
            e.printStackTrace();
            JOptionPane.showMessageDialog(rootPanel, "Ha ocurrido un error inesperado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public JPanel getMainPanel() { return rootPanel; }
    // ... (resto de getters sin cambios)
    public JComboBox<String> getComboTipoServicio() { return comboTipoServicio; }
    public JTextField getTxtNombreCliente() { return txtNombreCliente; }
    public JTextField getTxtTelefonoCliente() { return txtTelefonoCliente; }
    public JComboBox<String> getComboMunicipio() { return comboMunicipio; }
    public JSpinner getSpinnerFecha() { return spinnerFecha; }
    public JSpinner getSpinnerHora() { return spinnerHora; }
    public JSpinner getSpinnerValorServicio() { return spinnerValorServicio; }
    public JComboBox<String> getComboMetodoPago() { return comboMetodoPago; }
    public JComboBox<String> getComboCerrajero() { return comboCerrajero; }
    public JComboBox<String> getComboEstado() { return comboEstado; }
    public JButton getBtnGuardarServicio() { return btnGuardarServicio; }
    public JTabbedPane getTabbedPane() { return tabbedPane; }
    public JPanel getPanelRegistrar() { return panelRegistrar; }
    public JPanel getPanelConsultar() { return panelConsultar; }
    public JPanel getPanelOtroCerrajero() { return panelOtroCerrajero; }
    public JTextField getTxtNombreOtroCerrajero() { return txtNombreOtroCerrajero; }
    public JTextField getTxtTelefonoOtroCerrajero() { return txtTelefonoOtroCerrajero; }
}
