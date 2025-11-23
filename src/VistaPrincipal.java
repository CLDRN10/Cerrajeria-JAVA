import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class VistaPrincipal {

    private Integer idServicioSeleccionado = null;
    private Empresa miEmpresa;

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
    private JComboBox<Cerrajero> comboCerrajero;
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
    private JPanel panelBusqueda;
    private JTextField txtBusquedaCliente;
    private JButton btnBuscar;
    private JButton btnMostrarTodos;

    public VistaPrincipal() {
        miEmpresa = new Empresa("Cerrajería La Llave Maestra", new ArrayList<>());
        inicializarComponentesLogicos();
        agregarListeners();
    }

    private void inicializarComponentesLogicos() {
        spinnerFecha.setModel(new SpinnerDateModel());
        spinnerFecha.setEditor(new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy"));
        spinnerHora.setModel(new SpinnerDateModel());
        spinnerHora.setEditor(new JSpinner.DateEditor(spinnerHora, "hh:mm a"));
        spinnerValorServicio.setModel(new SpinnerNumberModel(50000.0, 0.0, 10000000.0, 1000.0));
        comboTipoServicio.setModel(new DefaultComboBoxModel<>(new String[]{"Seleccionar", "Apertura de automóvil", "Apertura de caja fuerte", "Apertura de candado", "Apertura de motocicleta", "Apertura de puerta residencial", "Cambio de clave de automóvil", "Cambio de clave de motocicleta", "Cambio de clave residencial", "Duplicado de llave", "Elaboración de llaves", "Instalación de alarma", "Instalación de chapa", "Reparación general"}));
        comboMunicipio.setModel(new DefaultComboBoxModel<>(new String[]{"Seleccionar", "Bucaramanga", "Floridablanca", "Piedecuesta"}));
        comboMetodoPago.setModel(new DefaultComboBoxModel<>(new String[]{"Nequi", "Efectivo"}));
        
        cargarCerrajerosDesdeBD();

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
        reiniciarFormulario();
    }

    private void cargarCerrajerosDesdeBD() {
        List<Cerrajero> cerrajerosBD = miEmpresa.getCerrajeros();
        cerrajerosBD.clear();

        Connection conn = null;
        try {
            conn = new ConexionBD().getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id_cerrajero, nombre_ce, telefono_ce FROM cerrajero ORDER BY nombre_ce");
            while(rs.next()) {
                cerrajerosBD.add(new Cerrajero(
                    rs.getLong("id_cerrajero"),
                    rs.getString("nombre_ce"),
                    String.valueOf(rs.getLong("telefono_ce"))
                ));
            }

            // Poblar el ComboBox
            DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<>();
            model.addElement("Seleccionar");
            for (Cerrajero c : cerrajerosBD) {
                model.addElement(c);
            }
            model.addElement("Otro"); // Añadir la opción "Otro" al final
            comboCerrajero.setModel((ComboBoxModel) model);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(rootPanel, "Error al cargar cerrajeros desde la BD: " + ex.getMessage(), "Error de Conexión", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // ... (El resto de los métodos como agregarListeners, guardarServicio, etc., se mantienen casi iguales
    // con pequeños ajustes para manejar el objeto Cerrajero del ComboBox)

    private void agregarListeners() {
        comboCerrajero.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object item = e.getItem();
                panelOtroCerrajero.setVisible("Otro".equals(item));
                Window window = SwingUtilities.getWindowAncestor(rootPanel);
                if (window != null) window.pack();
            }
        });
        btnGuardarServicio.addActionListener(e -> guardarServicio());
        btnActualizar.addActionListener(e -> actualizarServicio());
        btnEliminar.addActionListener(e -> eliminarServicio());
        btnLimpiarFormulario.addActionListener(e -> reiniciarFormulario());
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == panelConsultar) {
                cargarServicios(null);
            }
        });
        tablaServicios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaServicios.getSelectedRow() != -1) {
                Integer idServicio = (Integer) tablaServicios.getValueAt(tablaServicios.getSelectedRow(), 0);
                cargarDatosServicioEnFormulario(idServicio);
            }
        });
        btnBuscar.addActionListener(e -> {
            String textoBusqueda = txtBusquedaCliente.getText();
            if (textoBusqueda != null && !textoBusqueda.trim().isEmpty()) {
                cargarServicios(textoBusqueda.trim());
            }
        });
        btnMostrarTodos.addActionListener(e -> {
            txtBusquedaCliente.setText("");
            cargarServicios(null);
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
        comboEstado.setSelectedItem(EstadoServicio.PENDIENTE);
        spinnerFecha.setValue(new Date());
        spinnerHora.setValue(new Date());
        spinnerValorServicio.setValue(50000.0);
        panelOtroCerrajero.setVisible(false);
        idServicioSeleccionado = null;
        tablaServicios.clearSelection();
        btnGuardarServicio.setVisible(true);
        btnActualizar.setVisible(false);
        btnEliminar.setVisible(false);
        btnLimpiarFormulario.setVisible(false);

        Window window = SwingUtilities.getWindowAncestor(rootPanel);
        if (window != null && window.isVisible()) {
            window.pack();
        }
    }
    private boolean validarCampos() {
        if (txtNombreCliente.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(rootPanel, "El nombre del cliente no puede estar vacío.", "Error de Validación", JOptionPane.ERROR_MESSAGE); return false; }
        if (txtTelefonoCliente.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(rootPanel, "El teléfono del cliente no puede estar vacío.", "Error de Validación", JOptionPane.ERROR_MESSAGE); return false; }
        if (comboTipoServicio.getSelectedIndex() == 0) { JOptionPane.showMessageDialog(rootPanel, "Debe seleccionar un tipo de servicio.", "Error de Validación", JOptionPane.ERROR_MESSAGE); return false; }
        if (comboMunicipio.getSelectedIndex() == 0) { JOptionPane.showMessageDialog(rootPanel, "Debe seleccionar un municipio.", "Error de Validación", JOptionPane.ERROR_MESSAGE); return false; }
        if (comboCerrajero.getSelectedIndex() == 0) { JOptionPane.showMessageDialog(rootPanel, "Debe seleccionar un cerrajero.", "Error de Validación", JOptionPane.ERROR_MESSAGE); return false; }
        if ("Otro".equals(comboCerrajero.getSelectedItem())) {
            if (txtNombreOtroCerrajero.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(rootPanel, "El nombre del 'Otro' cerrajero no puede estar vacío.", "Error de Validación", JOptionPane.ERROR_MESSAGE); return false; }
        }
        return true;
    }

    private void guardarServicio() {
        if (!validarCampos()) return;

        Cliente cliente = new Cliente(
                txtNombreCliente.getText(),
                txtTelefonoCliente.getText(),
                txtDireccionCliente.getText(),
                (String) comboMunicipio.getSelectedItem()
        );

        Cerrajero cerrajero;
        Object itemCerrajero = comboCerrajero.getSelectedItem();
        if (itemCerrajero instanceof Cerrajero) {
            cerrajero = (Cerrajero) itemCerrajero;
        } else if ("Otro".equals(itemCerrajero)) {
            cerrajero = new Cerrajero(txtNombreOtroCerrajero.getText(), txtTelefonoOtroCerrajero.getText());
        } else {
            JOptionPane.showMessageDialog(rootPanel, "Error: Cerrajero no válido seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Servicio servicio = new Servicio(
                (Date) spinnerFecha.getValue(),
                new Time(((Date) spinnerHora.getValue()).getTime()),
                (String) comboTipoServicio.getSelectedItem(),
                (EstadoServicio) comboEstado.getSelectedItem(),
                BigDecimal.valueOf((Double) spinnerValorServicio.getValue()),
                (String) comboMetodoPago.getSelectedItem(),
                cliente,
                cerrajero
        );

        Connection conn = null;
        try {
            conn = new ConexionBD().getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO cliente (nombre_c, telefono_c, direccion_c, ciudad_c) VALUES (?, ?, ?, ?) RETURNING id_cliente")) {
                pstmt.setString(1, servicio.getCliente().getNombre());
                pstmt.setLong(2, Long.parseLong(servicio.getCliente().getTelefono()));
                pstmt.setString(3, servicio.getCliente().getDireccion());
                pstmt.setString(4, servicio.getCliente().getCiudad());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    servicio.getCliente().setIdCliente(rs.getLong(1));
                } else { throw new SQLException("No se pudo crear el cliente."); }
            }

            long idCerrajero;
            if (servicio.getCerrajero().getIdCerrajero() != null) {
                idCerrajero = servicio.getCerrajero().getIdCerrajero();
            } else {
                try (PreparedStatement pstmtCrea = conn.prepareStatement("INSERT INTO cerrajero (nombre_ce, telefono_ce) VALUES (?, ?) RETURNING id_cerrajero")){
                    pstmtCrea.setString(1, servicio.getCerrajero().getNombre());
                    long telefono = servicio.getCerrajero().getTelefono().isEmpty() ? 0 : Long.parseLong(servicio.getCerrajero().getTelefono());
                    pstmtCrea.setLong(2, telefono);
                    ResultSet rsCrea = pstmtCrea.executeQuery();
                    if(rsCrea.next()){ idCerrajero = rsCrea.getLong(1); } else { throw new SQLException("No se pudo crear el cerrajero.");}
                }
            }
            servicio.getCerrajero().setIdCerrajero(idCerrajero);

            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO servicio (fecha_s, hora_s, tipo_s, estado_s, monto_pago, metodo_pago, id_cliente, id_cerrajero) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                pstmt.setDate(1, new java.sql.Date(servicio.getFecha().getTime()));
                pstmt.setTime(2, servicio.getHora());
                pstmt.setString(3, servicio.getTipo());
                pstmt.setString(4, servicio.getEstado().name());
                pstmt.setBigDecimal(5, servicio.getMonto());
                pstmt.setString(6, servicio.getMetodoPago());
                pstmt.setLong(7, servicio.getCliente().getIdCliente());
                pstmt.setLong(8, servicio.getCerrajero().getIdCerrajero());
                if (pstmt.executeUpdate() == 0) throw new SQLException("No se pudo guardar el servicio.");
            }

            conn.commit();
            JOptionPane.showMessageDialog(rootPanel, "Servicio guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            reiniciarFormulario();
            cargarCerrajerosDesdeBD(); // Recargar por si se añadió uno nuevo
        } catch (Exception ex) {
            try { if (conn != null) conn.rollback(); } catch (SQLException e) { e.printStackTrace(); }
            JOptionPane.showMessageDialog(rootPanel, "Error al guardar en la BD: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }
    private void actualizarServicio() {
        if (idServicioSeleccionado == null || !validarCampos()) return;
        int confirm = JOptionPane.showConfirmDialog(rootPanel, "¿Está seguro de que desea actualizar este servicio?", "Confirmar Actualización", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        
        Cliente clienteActualizado = new Cliente(
                txtNombreCliente.getText(),
                txtTelefonoCliente.getText(),
                txtDireccionCliente.getText(),
                (String) comboMunicipio.getSelectedItem()
        );

        Connection conn = null;
        try {
            conn = new ConexionBD().getConnection();
            conn.setAutoCommit(false);
            long idClienteDb;
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT id_cliente FROM servicio WHERE id_servicio = ?")) {
                pstmt.setInt(1, idServicioSeleccionado);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) { idClienteDb = rs.getLong("id_cliente"); } else { throw new SQLException("No se encontró el servicio."); }
            }
            try (PreparedStatement pstmt = conn.prepareStatement("UPDATE cliente SET nombre_c = ?, telefono_c = ?, direccion_c = ?, ciudad_c = ? WHERE id_cliente = ?")) {
                pstmt.setString(1, clienteActualizado.getNombre());
                pstmt.setLong(2, Long.parseLong(clienteActualizado.getTelefono()));
                pstmt.setString(3, clienteActualizado.getDireccion());
                pstmt.setString(4, clienteActualizado.getCiudad());
                pstmt.setLong(5, idClienteDb);
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement("UPDATE servicio SET fecha_s=?, hora_s=?, tipo_s=?, estado_s=?, monto_pago=?, metodo_pago=? WHERE id_servicio=?")) {
                pstmt.setDate(1, new java.sql.Date(((Date) spinnerFecha.getValue()).getTime()));
                pstmt.setTime(2, new Time(((Date) spinnerHora.getValue()).getTime()));
                pstmt.setString(3, (String) comboTipoServicio.getSelectedItem());
                pstmt.setString(4, ((EstadoServicio) comboEstado.getSelectedItem()).name());
                pstmt.setBigDecimal(5, BigDecimal.valueOf((Double) spinnerValorServicio.getValue()));
                pstmt.setString(6, (String) comboMetodoPago.getSelectedItem());
                pstmt.setInt(7, idServicioSeleccionado);
                pstmt.executeUpdate();
            }
            conn.commit();
            JOptionPane.showMessageDialog(rootPanel, "Servicio actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            reiniciarFormulario();
            cargarServicios(null);
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
            conn = new ConexionBD().getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM servicio WHERE id_servicio = ?")) {
                pstmt.setInt(1, idServicioSeleccionado);
                if (pstmt.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(rootPanel, "Servicio eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    reiniciarFormulario();
                    cargarServicios(null);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(rootPanel, "Error al eliminar el servicio: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }
    
    private void cargarServicios(String filtroCliente) {
        DefaultTableModel modelo = new DefaultTableModel(new String[]{"ID", "Fecha", "Tipo Servicio", "Cliente", "Cerrajero", "Estado", "Monto"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        String sql = "SELECT s.id_servicio, s.fecha_s, s.tipo_s, c.nombre_c, ce.nombre_ce, s.estado_s, s.monto_pago " +
                     "FROM servicio s JOIN cliente c ON s.id_cliente = c.id_cliente JOIN cerrajero ce ON s.id_cerrajero = ce.id_cerrajero";
        if (filtroCliente != null && !filtroCliente.isEmpty()) {
            sql += " WHERE c.nombre_c ILIKE ?";
        }
        sql += " ORDER BY s.id_servicio DESC";
        Connection conn = null;
        try {
            conn = new ConexionBD().getConnection();
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
                modelo.addRow(fila);
            }
            tablaServicios.setModel(modelo);
            ajustarAnchoColumnas();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(rootPanel, "Error al cargar servicios: " + ex.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void cargarDatosServicioEnFormulario(int idServicio) {
        String sql = "SELECT s.*, c.id_cliente, c.nombre_c, c.telefono_c, c.direccion_c, c.ciudad_c, ce.id_cerrajero, ce.nombre_ce, ce.telefono_ce " +
                     "FROM servicio s JOIN cliente c ON s.id_cliente = c.id_cliente JOIN cerrajero ce ON s.id_cerrajero = ce.id_cerrajero " +
                     "WHERE s.id_servicio = ?";
        Connection conn = null;
        try {
            conn = new ConexionBD().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idServicio);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Cliente cliente = new Cliente(
                        rs.getLong("id_cliente"),
                        rs.getString("nombre_c"),
                        String.valueOf(rs.getLong("telefono_c")),
                        rs.getString("direccion_c"),
                        rs.getString("ciudad_c")
                );
                Cerrajero cerrajero = new Cerrajero(
                        rs.getLong("id_cerrajero"),
                        rs.getString("nombre_ce"),
                        String.valueOf(rs.getLong("telefono_ce"))
                );
                EstadoServicio estado = EstadoServicio.valueOf(rs.getString("estado_s"));

                txtNombreCliente.setText(cliente.getNombre());
                txtTelefonoCliente.setText(cliente.getTelefono());
                txtDireccionCliente.setText(cliente.getDireccion());
                comboMunicipio.setSelectedItem(cliente.getCiudad());

                spinnerFecha.setValue(rs.getDate("fecha_s"));
                spinnerHora.setValue(rs.getTime("hora_s"));
                spinnerValorServicio.setValue(rs.getBigDecimal("monto_pago").doubleValue());
                comboTipoServicio.setSelectedItem(rs.getString("tipo_s"));
                comboMetodoPago.setSelectedItem(rs.getString("metodo_pago"));
                comboEstado.setSelectedItem(estado);
                
                // Lógica para seleccionar el cerrajero correcto en el ComboBox
                boolean cerrajeroEncontrado = false;
                for (int i = 0; i < comboCerrajero.getItemCount(); i++) {
                    Object item = comboCerrajero.getItemAt(i);
                    if (item instanceof Cerrajero && ((Cerrajero) item).getIdCerrajero().equals(cerrajero.getIdCerrajero())) {
                        comboCerrajero.setSelectedIndex(i);
                        cerrajeroEncontrado = true;
                        break;
                    }
                }
                if (!cerrajeroEncontrado) { comboCerrajero.setSelectedItem("Otro"); }

                this.idServicioSeleccionado = idServicio;
                btnGuardarServicio.setVisible(false);
                btnActualizar.setVisible(true);
                btnEliminar.setVisible(true);
                btnLimpiarFormulario.setVisible(true);
                tabbedPane.setSelectedComponent(panelRegistrar);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(rootPanel, "Error al cargar el detalle del servicio: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    private void ajustarAnchoColumnas() {
        tablaServicios.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumnModel columnModel = tablaServicios.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(40); 
        columnModel.getColumn(1).setPreferredWidth(90);  
        columnModel.getColumn(2).setPreferredWidth(200); 
        columnModel.getColumn(3).setPreferredWidth(180); 
        columnModel.getColumn(4).setPreferredWidth(150); 
        columnModel.getColumn(5).setPreferredWidth(100); 
        columnModel.getColumn(6).setPreferredWidth(100); 
    }

    public JPanel getMainPanel() {
        return rootPanel;
    }
}
