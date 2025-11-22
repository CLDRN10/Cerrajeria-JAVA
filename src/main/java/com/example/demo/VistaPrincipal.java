package com.example.demo;

import javax.swing.*;

public class VistaPrincipal {

    // --- Componentes vinculados desde el .form ---
    private JPanel mainPanel;
    private JTabbedPane tabbedPane;
    private JPanel panelRegistrar;
    private JComboBox<String> comboTipoServicio;
    private JTextField txtNombreCliente;
    private JTextField txtDireccionCliente;
    private JComboBox<String> comboMunicipio;
    private JSpinner spinnerFechaHora;
    private JSpinner spinnerValorServicio;
    private JComboBox<String> comboMetodoPago;
    private JComboBox<String> comboCerrajero;
    private JComboBox<String> comboEstado;
    private JButton btnRegistrarServicio;
    private JPanel panelConsultar;

    public VistaPrincipal() {
        // --- Configuración de componentes que requieren lógica en el arranque ---
        inicializarComponentesLogicos();
    }

    private void inicializarComponentesLogicos() {
        // --- Configuración de Spinners ---
        // Configura el Spinner de Fecha/Hora
        spinnerFechaHora.setModel(new SpinnerDateModel());
        JSpinner.DateEditor de = new JSpinner.DateEditor(spinnerFechaHora, "yyyy/MM/dd HH:mm:ss");
        spinnerFechaHora.setEditor(de);

        // Configura el Spinner de Valor del Servicio
        spinnerValorServicio.setModel(new SpinnerNumberModel(50000.0, 0.0, 10000000.0, 1000.0));

        // --- Carga de datos en ComboBoxes ---
        // (Estos datos vendrán de la base de datos en el futuro)
        comboTipoServicio.setModel(new DefaultComboBoxModel<>(new String[]{"Apertura de puerta", "Cambio de cerradura", "Instalación de cerrojo", "Reparación"}));
        comboMunicipio.setModel(new DefaultComboBoxModel<>(new String[]{"Bogotá", "Medellín", "Cali", "Barranquilla", "Cartagena"}));
        comboMetodoPago.setModel(new DefaultComboBoxModel<>(new String[]{"Efectivo", "Tarjeta de Crédito", "Tarjeta de Débito", "Transferencia"}));
        comboCerrajero.setModel(new DefaultComboBoxModel<>(new String[]{"Carlos Perez", "Ana García", "Luis Rodriguez"}));

        // Llenar el ComboBox de Estados desde el Enum
        DefaultComboBoxModel<String> estadoModel = new DefaultComboBoxModel<>();
        for (EstadoServicio estado : EstadoServicio.values()) {
            estadoModel.addElement(estado.getDescripcion());
        }
        comboEstado.setModel(estadoModel);
    }

    // --- Getters para que el Controlador pueda acceder a los componentes ---

    public JPanel getMainPanel() { return mainPanel; }
    public JComboBox<String> getComboTipoServicio() { return comboTipoServicio; }
    public JTextField getTxtNombreCliente() { return txtNombreCliente; }
    public JTextField getTxtDireccionCliente() { return txtDireccionCliente; }
    public JComboBox<String> getComboMunicipio() { return comboMunicipio; }
    public JSpinner getSpinnerFechaHora() { return spinnerFechaHora; }
    public JSpinner getSpinnerValorServicio() { return spinnerValorServicio; }
    public JComboBox<String> getComboMetodoPago() { return comboMetodoPago; }
    public JComboBox<String> getComboCerrajero() { return comboCerrajero; }
    public JComboBox<String> getComboEstado() { return comboEstado; }
    public JButton getBtnRegistrarServicio() { return btnRegistrarServicio; }
}
