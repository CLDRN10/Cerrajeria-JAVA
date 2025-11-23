import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;

public class VistaPrincipal {

    // --- Componentes vinculados desde el .form ---
    private JPanel rootPanel; // Corregido: de mainPanel de vuelta a rootPanel
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
        comboCerrajero.setModel(new DefaultComboBoxModel<>(new String[]{"Seleccionar", "Jose Hernandez", "Otro"}));

        DefaultComboBoxModel<String> estadoModel = new DefaultComboBoxModel<>();
        estadoModel.addElement("Seleccionar estado");
        for (EstadoServicio estado : EstadoServicio.values()) {
            estadoModel.addElement(estado.getDescripcion());
        }
        comboEstado.setModel(estadoModel);

        panelOtroCerrajero.setVisible(false);
    }

    private void agregarListeners() {
        comboCerrajero.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String seleccion = (String) e.getItem();
                boolean esOtro = "Otro".equals(seleccion);
                panelOtroCerrajero.setVisible(esOtro);
                SwingUtilities.getWindowAncestor(rootPanel).pack(); // Corregido
            }
        });

        btnGuardarServicio.addActionListener(e -> guardarServicio());
    }

    private void guardarServicio() {
        String sql = "INSERT INTO servicios (tipo_servicio, nombre_cliente, telefono_cliente, direccion_cliente, municipio, fecha_servicio, hora_servicio, valor_servicio, metodo_pago, nombre_cerrajero, telefono_cerrajero, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            ConexionBD conexionBD = new ConexionBD();
            conn = conexionBD.getConnection();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, (String) comboTipoServicio.getSelectedItem());
            pstmt.setString(2, txtNombreCliente.getText());
            pstmt.setString(3, txtTelefonoCliente.getText());
            pstmt.setString(4, txtDireccionCliente.getText());
            pstmt.setString(5, (String) comboMunicipio.getSelectedItem());

            java.util.Date fechaUtil = (java.util.Date) spinnerFecha.getValue();
            pstmt.setDate(6, new java.sql.Date(fechaUtil.getTime()));

            java.util.Date horaUtil = (java.util.Date) spinnerHora.getValue();
            pstmt.setTime(7, new Time(horaUtil.getTime()));

            pstmt.setDouble(8, (Double) spinnerValorServicio.getValue());
            pstmt.setString(9, (String) comboMetodoPago.getSelectedItem());

            if ("Otro".equals(comboCerrajero.getSelectedItem())) {
                pstmt.setString(10, txtNombreOtroCerrajero.getText());
                pstmt.setString(11, txtTelefonoOtroCerrajero.getText());
            } else {
                pstmt.setString(10, (String) comboCerrajero.getSelectedItem());
                pstmt.setNull(11, java.sql.Types.VARCHAR);
            }

            pstmt.setString(12, (String) comboEstado.getSelectedItem());

            int filasAfectadas = pstmt.executeUpdate();
            conn.commit();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(rootPanel, "Servicio guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE); // Corregido
            } else {
                JOptionPane.showMessageDialog(rootPanel, "No se pudo guardar el servicio.", "Error", JOptionPane.ERROR_MESSAGE); // Corregido
            }

        } catch (SQLException ex) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ex.printStackTrace();
            JOptionPane.showMessageDialog(rootPanel, "Error al guardar en la base de datos: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE); // Corregido
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // --- Getters para que el Controlador pueda acceder a los componentes ---

    public JPanel getMainPanel() { return rootPanel; } // Corregido
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
