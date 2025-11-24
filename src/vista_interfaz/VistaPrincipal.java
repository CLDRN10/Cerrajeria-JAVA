package vista_interfaz;

import modelo_logica.Cerrajero;
import modelo_logica.Cliente;
import modelo_logica.Empresa;
import modelo_logica.EstadoServicio;
import modelo_logica.Servicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;
import java.util.Objects;
import java.util.Vector;

public class VistaPrincipal {

    // --- ATRIBUTOS DE ESTADO ---
    private Integer idServicioSeleccionado = null;
    private Cliente clienteSeleccionado = null; 
    private final Empresa miEmpresa;

    // --- COMPONENTES SWING ---
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
    private JComboBox<Object> comboCerrajero;
    private JComboBox<EstadoServicio> comboEstado;
    private JButton btnGuardarServicio;
    private JPanel panelConsultar;
    private JPanel panelOtroCerrajero;
    private JTextField txtNombreOtroCerrajero;
    private JTextField txtTelefonoOtroCerrajero;
    private JTable tablaServicios;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiarFormulario;
    private JTextField txtBusquedaCliente;
    private JButton btnBuscar;
    private JButton btnMostrarTodos;

    public VistaPrincipal() {
        this.miEmpresa = new Empresa("Cerrajería 24 horas");
        inicializarComponentesVisuales();
        agregarListeners();
        cargarServicios(null); 
    }

    private void inicializarComponentesVisuales() {
        spinnerFecha.setModel(new SpinnerDateModel());
        spinnerFecha.setEditor(new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy"));
        spinnerHora.setModel(new SpinnerDateModel());
        spinnerHora.setEditor(new JSpinner.DateEditor(spinnerHora, "hh:mm a"));
        spinnerValorServicio.setModel(new SpinnerNumberModel(50000.0, 0.0, 10000000.0, 1000.0));

        comboTipoServicio.setModel(new DefaultComboBoxModel<>(new String[]{"Seleccionar", "Apertura de puerta", "Instalación de cerradura", "Cambio de guardas", "Mantenimiento"}));
        comboMunicipio.setModel(new DefaultComboBoxModel<>(new String[]{"Seleccionar", "Bucaramanga", "Floridablanca", "Girón", "Piedecuesta"}));
        comboMetodoPago.setModel(new DefaultComboBoxModel<>(new String[]{"Efectivo", "Nequi", "Bancolombia", "Daviplata"}));

        cargarCerrajeros();
        configurarRenderers();

        tablaServicios.setDefaultEditor(Object.class, null); 
        reiniciarFormulario();
    }

    private void configurarRenderers() {
        comboCerrajero.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Cerrajero) {
                    setText(((Cerrajero) value).getNombre());
                } else if (value instanceof String) {
                    setText((String) value);
                }
                return this;
            }
        });

        comboEstado.setModel(new DefaultComboBoxModel<>(EstadoServicio.values()));
        comboEstado.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof EstadoServicio) {
                    setText(((EstadoServicio) value).getDescripcion());
                }
                return this;
            }
        });
    }

    private void cargarCerrajeros() {
        try {
            miEmpresa.cargarCerrajerosDesdeBD();
            DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<>();
            model.addElement("Seleccionar");
            for (Cerrajero c : miEmpresa.getCerrajeros()) {
                model.addElement(c);
            }
            model.addElement("Otro");
            comboCerrajero.setModel(model);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(rootPanel, "Error al cargar cerrajeros: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarListeners() {
        comboCerrajero.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                boolean esOtro = "Otro".equals(e.getItem());
                panelOtroCerrajero.setVisible(esOtro);
                SwingUtilities.getWindowAncestor(rootPanel).pack();
            }
        });

        btnGuardarServicio.addActionListener(e -> guardarServicio());
        btnActualizar.addActionListener(e -> actualizarServicio());
        btnEliminar.addActionListener(e -> eliminarServicio());
        btnLimpiarFormulario.addActionListener(e -> reiniciarFormulario());

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == panelConsultar) {
                cargarServicios(null);
                reiniciarFormulario();
            }
        });

        tablaServicios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaServicios.getSelectedRow() != -1) {
                int filaSeleccionada = tablaServicios.getSelectedRow();
                idServicioSeleccionado = (Integer) tablaServicios.getValueAt(filaSeleccionada, 0);
                cargarDatosServicioEnFormulario(idServicioSeleccionado);
            }
        });

        btnBuscar.addActionListener(e -> cargarServicios(txtBusquedaCliente.getText()));
        btnMostrarTodos.addActionListener(e -> {
            txtBusquedaCliente.setText("");
            cargarServicios(null);
        });
    }

    private void reiniciarFormulario() {
        idServicioSeleccionado = null;
        clienteSeleccionado = null;

        comboTipoServicio.setSelectedIndex(0);
        txtNombreCliente.setText("");
        txtTelefonoCliente.setText("");
        txtDireccionCliente.setText("");
        comboMunicipio.setSelectedIndex(0);
        spinnerFecha.setValue(new Date());
        spinnerHora.setValue(new Date());
        spinnerValorServicio.setValue(50000.0);
        comboMetodoPago.setSelectedIndex(0);
        comboCerrajero.setSelectedIndex(0);
        comboEstado.setSelectedItem(EstadoServicio.PENDIENTE);

        panelOtroCerrajero.setVisible(false);
        txtNombreOtroCerrajero.setText("");
        txtTelefonoOtroCerrajero.setText("");

        btnGuardarServicio.setVisible(true);
        btnActualizar.setVisible(false);
        btnEliminar.setVisible(false);
        btnLimpiarFormulario.setText("Limpiar Formulario");

        tablaServicios.clearSelection();
        if (rootPanel.isDisplayable()) {
             SwingUtilities.getWindowAncestor(rootPanel).pack();
        }
    }

    private boolean validarCampos() {
        if (comboTipoServicio.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(rootPanel, "Debe seleccionar un tipo de servicio.", "Campo Requerido", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtNombreCliente.getText().trim().isEmpty() || txtTelefonoCliente.getText().trim().isEmpty() || txtDireccionCliente.getText().trim().isEmpty()) {
             JOptionPane.showMessageDialog(rootPanel, "Nombre, teléfono y dirección del cliente son obligatorios.", "Campos Requeridos", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (comboMunicipio.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(rootPanel, "Debe seleccionar un municipio.", "Campo Requerido", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (comboCerrajero.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(rootPanel, "Debe seleccionar un cerrajero.", "Campo Requerido", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if ("Otro".equals(comboCerrajero.getSelectedItem()) && (txtNombreOtroCerrajero.getText().trim().isEmpty() || txtTelefonoOtroCerrajero.getText().trim().isEmpty())) {
             JOptionPane.showMessageDialog(rootPanel, "El nombre y teléfono del nuevo cerrajero son obligatorios.", "Campos Requeridos", JOptionPane.WARNING_MESSAGE);
             return false;
        }
        return true;
    }

    private void guardarServicio() {
        if (!validarCampos()) return;

        try {
            Cliente cliente = new Cliente(
                txtNombreCliente.getText().trim(),
                txtTelefonoCliente.getText().trim(),
                txtDireccionCliente.getText().trim(),
                (String) comboMunicipio.getSelectedItem()
            );

            Cerrajero cerrajero;
            if (comboCerrajero.getSelectedItem() instanceof Cerrajero) {
                cerrajero = (Cerrajero) comboCerrajero.getSelectedItem();
            } else {
                cerrajero = new Cerrajero(txtNombreOtroCerrajero.getText().trim(), txtTelefonoOtroCerrajero.getText().trim());
            }

            Servicio servicio = new Servicio(
                    new java.sql.Date(((Date) spinnerFecha.getValue()).getTime()),
                    new Time(((Date) spinnerHora.getValue()).getTime()),
                    (String) comboTipoServicio.getSelectedItem(),
                    (EstadoServicio) comboEstado.getSelectedItem(),
                    BigDecimal.valueOf((Double) spinnerValorServicio.getValue()),
                    (String) comboMetodoPago.getSelectedItem(),
                    cliente,
                    cerrajero
            );

            servicio.guardar();

            JOptionPane.showMessageDialog(rootPanel, "Servicio guardado con éxito.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            reiniciarFormulario();
            cargarServicios(null);
            tabbedPane.setSelectedIndex(1);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(rootPanel, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void actualizarServicio() {
        if (idServicioSeleccionado == null || clienteSeleccionado == null) return;
        if (!validarCampos()) return;

        int confirmacion = JOptionPane.showConfirmDialog(rootPanel, "¿Está seguro de que desea actualizar este servicio?", "Confirmar Actualización", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        try {
            clienteSeleccionado.setNombre(txtNombreCliente.getText().trim());
            clienteSeleccionado.setTelefono(txtTelefonoCliente.getText().trim());
            clienteSeleccionado.setDireccion(txtDireccionCliente.getText().trim());
            clienteSeleccionado.setCiudad((String) comboMunicipio.getSelectedItem());

            Cerrajero cerrajero;
            if (comboCerrajero.getSelectedItem() instanceof Cerrajero) {
                cerrajero = (Cerrajero) comboCerrajero.getSelectedItem();
            } else { 
                cerrajero = new Cerrajero(txtNombreOtroCerrajero.getText().trim(), txtTelefonoOtroCerrajero.getText().trim());
            }

            Servicio servicioActualizado = new Servicio(
                    idServicioSeleccionado,
                    new java.sql.Date(((Date) spinnerFecha.getValue()).getTime()),
                    new Time(((Date) spinnerHora.getValue()).getTime()),
                    (String) comboTipoServicio.getSelectedItem(),
                    (EstadoServicio) comboEstado.getSelectedItem(),
                    BigDecimal.valueOf((Double) spinnerValorServicio.getValue()),
                    (String) comboMetodoPago.getSelectedItem(),
                    clienteSeleccionado,
                    cerrajero
            );

            servicioActualizado.actualizar();

            JOptionPane.showMessageDialog(rootPanel, "Servicio actualizado con éxito.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            reiniciarFormulario();
            cargarServicios(null);
            tabbedPane.setSelectedIndex(1);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(rootPanel, "Error al actualizar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void eliminarServicio() {
        if (idServicioSeleccionado == null) return;

        int confirmacion = JOptionPane.showConfirmDialog(rootPanel, "¿Está seguro de que desea eliminar este servicio permanentemente?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        try {
            Servicio.eliminar(idServicioSeleccionado);
            JOptionPane.showMessageDialog(rootPanel, "Servicio eliminado con éxito.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            reiniciarFormulario();
            cargarServicios(null);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(rootPanel, "Error al eliminar: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarServicios(String filtroCliente) {
        try {
            DefaultTableModel modelo = new DefaultTableModel() {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
            modelo.setColumnIdentifiers(new Object[]{"ID", "Fecha", "Tipo", "Cliente", "Cerrajero", "Estado", "Monto"});
            
            for (Vector<Object> fila : Servicio.listarServicios(filtroCliente)) {
                modelo.addRow(fila);
            }
            
            tablaServicios.setModel(modelo);
            ajustarAnchoColumnas();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(rootPanel, "Error al cargar servicios: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDatosServicioEnFormulario(int idServicio) {
        try {
            Servicio servicio = Servicio.cargarPorId(idServicio);
            if (servicio == null) return;

            this.clienteSeleccionado = servicio.getCliente();

            comboTipoServicio.setSelectedItem(servicio.getTipo());
            txtNombreCliente.setText(clienteSeleccionado.getNombre());
            txtTelefonoCliente.setText(clienteSeleccionado.getTelefono());
            txtDireccionCliente.setText(clienteSeleccionado.getDireccion());
            comboMunicipio.setSelectedItem(clienteSeleccionado.getCiudad());
            spinnerFecha.setValue(servicio.getFecha());
            spinnerHora.setValue(servicio.getHora());
            spinnerValorServicio.setValue(servicio.getMonto().doubleValue());
            comboMetodoPago.setSelectedItem(servicio.getMetodoPago());
            comboEstado.setSelectedItem(servicio.getEstado());

            boolean cerrajeroEncontrado = false;
            for (int i = 0; i < comboCerrajero.getItemCount(); i++) {
                if (comboCerrajero.getItemAt(i) instanceof Cerrajero) {
                    Cerrajero c = (Cerrajero) comboCerrajero.getItemAt(i);
                    if (Objects.equals(c.getIdCerrajero(), servicio.getCerrajero().getIdCerrajero())) {
                        comboCerrajero.setSelectedIndex(i);
                        cerrajeroEncontrado = true;
                        break;
                    }
                }
            }
            if (!cerrajeroEncontrado) {
                comboCerrajero.setSelectedItem("Otro");
                txtNombreOtroCerrajero.setText(servicio.getCerrajero().getNombre());
                txtTelefonoOtroCerrajero.setText(servicio.getCerrajero().getTelefono());
                panelOtroCerrajero.setVisible(true);
            }

            btnGuardarServicio.setVisible(false);
            btnActualizar.setVisible(true);
            btnEliminar.setVisible(true);
            btnLimpiarFormulario.setText("Cancelar Edición");
            tabbedPane.setSelectedIndex(0);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(rootPanel, "Error al cargar datos del servicio: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajustarAnchoColumnas() {
        TableColumnModel modeloColumna = tablaServicios.getColumnModel();
        modeloColumna.getColumn(0).setMaxWidth(50);
        modeloColumna.getColumn(1).setPreferredWidth(90);
        modeloColumna.getColumn(2).setPreferredWidth(150);
        modeloColumna.getColumn(3).setPreferredWidth(150);
        modeloColumna.getColumn(4).setPreferredWidth(150);
        modeloColumna.getColumn(5).setPreferredWidth(110);
        modeloColumna.getColumn(6).setPreferredWidth(100);
    }

    public JPanel getMainPanel() {
        return rootPanel;
    }
}
