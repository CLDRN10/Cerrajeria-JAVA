import javax.swing.*;
import java.awt.event.ItemEvent;
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
    private JPanel panelOtroCerrajero;
    private JTextField txtNombreOtroCerrajero;
    private JTextField txtTelefonoOtroCerrajero;


    public VistaPrincipal() {
        // --- Configuración de componentes que requieren lógica en el arranque ---
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

        // Ocultar el panel para "Otro" cerrajero por defecto
        panelOtroCerrajero.setVisible(false);
    }

    private void agregarListeners() {
        comboCerrajero.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String seleccion = (String) e.getItem();
                panelOtroCerrajero.setVisible("Otro".equals(seleccion));
            }
        });
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
    public JPanel getPanelOtroCerrajero() { return panelOtroCerrajero; }
    public JTextField getTxtNombreOtroCerrajero() { return txtNombreOtroCerrajero; }
    public JTextField getTxtTelefonoOtroCerrajero() { return txtTelefonoOtroCerrajero; }
}
