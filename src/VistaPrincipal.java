import javax.swing.*;
import java.util.Date;

public class VistaPrincipal {

    // --- Componentes vinculados desde el .form ---
    private JPanel mainPanel;
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

    public VistaPrincipal() {
        // --- Configuración de componentes que requieren lógica en el arranque ---
        inicializarComponentesLogicos();
    }

    private void inicializarComponentesLogicos() {
        // --- Configuración de Spinners ---

        // Configura el Spinner de Fecha
        spinnerFecha.setModel(new SpinnerDateModel());
        JSpinner.DateEditor deFecha = new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy");
        spinnerFecha.setEditor(deFecha);
        spinnerFecha.setValue(new Date()); // Valor inicial: fecha actual

        // Configura el Spinner de Hora
        spinnerHora.setModel(new SpinnerDateModel());
        JSpinner.DateEditor deHora = new JSpinner.DateEditor(spinnerHora, "hh:mm a");
        spinnerHora.setEditor(deHora);
        spinnerHora.setValue(new Date()); // Valor inicial: hora actual

        // Configura el Spinner de Valor del Servicio
        spinnerValorServicio.setModel(new SpinnerNumberModel(50000.0, 0.0, 10000000.0, 1000.0));

        // --- Carga de datos en ComboBoxes ---
        comboTipoServicio.setModel(new DefaultComboBoxModel<>(new String[]{"Seleccionar", "Apertura de puerta", "Cambio de cerradura", "Instalación de cerrojo", "Reparación"}));
        comboMunicipio.setModel(new DefaultComboBoxModel<>(new String[]{"Seleccionar", "Bogotá", "Medellín", "Cali", "Barranquilla", "Cartagena"}));
        comboMetodoPago.setModel(new DefaultComboBoxModel<>(new String[]{"Efectivo", "Tarjeta de Crédito", "Tarjeta de Débito", "Transferencia"}));
        comboCerrajero.setModel(new DefaultComboBoxModel<>(new String[]{"Seleccionar", "Carlos Perez", "Ana García", "Luis Rodriguez"}));

        // Llenar el ComboBox de Estados desde el Enum
        DefaultComboBoxModel<String> estadoModel = new DefaultComboBoxModel<>();
        estadoModel.addElement("Seleccionar estado");
        for (EstadoServicio estado : EstadoServicio.values()) {
            estadoModel.addElement(estado.getDescripcion());
        }
        comboEstado.setModel(estadoModel);
    }

    // --- Getters para que el Controlador pueda acceder a los componentes ---

    public JPanel getMainPanel() { return mainPanel; }
    public JComboBox<String> getComboTipoServicio() { return comboTipoServicio; }
    public JTextField getTxtNombreCliente() { return txtNombreCliente; }
    public JTextField getTxtTelefonoCliente() { return txtTelefonoCliente; }
    public JTextField getTxtDireccionCliente() { return txtDireccionCliente; }
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
}
