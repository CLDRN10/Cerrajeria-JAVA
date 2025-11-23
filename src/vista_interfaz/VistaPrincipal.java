package vista_interfaz;

// --- IMPORTACIONES ---
// Se importan las clases necesarias: las del modelo de lógica para manejar los datos,
// las de Swing para la interfaz gráfica, y otras utilidades de Java.
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
import java.util.Vector;

/**
 * ==================================================================
 * CLASE VistaPrincipal
 * ==================================================================
 * Es la clase principal de la interfaz de usuario. Controla todos los
 * elementos visuales de la ventana y gestiona la interacción con el usuario.
 * 
 * PROPÓSITO:
 * Actúa como la "VISTA" en una arquitectura similar a MVC. Sus responsabilidades son:
 * 1.  Mostrar los datos que le proporcionan las clases del modelo (ej: mostrar servicios en la tabla).
 * 2.  Capturar las acciones del usuario (clics en botones, texto introducido, etc.).
 * 3.  Recopilar los datos del formulario y pasárselos a las clases del modelo para que las procesen.
 * 
 * Esta clase NO contiene lógica de negocio ni consultas SQL. Simplemente "delega" el trabajo
 * a los objetos del paquete `modelo_logica`, logrando así la descentralización.
 */
public class VistaPrincipal {

    // --- ATRIBUTOS DE LA CLASE ---
    private Integer idServicioSeleccionado = null; // Guarda el ID del servicio que se selecciona en la tabla.
    private Empresa miEmpresa; // Una instancia de la clase Empresa para acceder a la lista de cerrajeros.

    // --- COMPONENTES SWING (GENERADOS POR EL DISEÑADOR) ---
    // Cada uno de estos atributos corresponde a un elemento de la interfaz gráfica.
    private JPanel rootPanel; // El panel principal que contiene todo lo demás.
    private JTabbedPane tabbedPane; // El contenedor con las pestañas "Registrar" y "Consultar".
    private JPanel panelRegistrar; // La pestaña para crear y modificar servicios.
    private JComboBox<String> comboTipoServicio;
    private JTextField txtNombreCliente;
    private JTextField txtTelefonoCliente;
    private JTextField txtDireccionCliente;
    private JComboBox<String> comboMunicipio;
    private JSpinner spinnerFecha;
    private JSpinner spinnerHora;
    private JSpinner spinnerValorServicio;
    private JComboBox<String> comboMetodoPago;
    private JComboBox<Object> comboCerrajero; // Es de tipo <Object> porque puede contener Strings y objetos Cerrajero.
    private JComboBox<EstadoServicio> comboEstado; // Contiene los valores del Enum EstadoServicio.
    private JButton btnGuardarServicio;
    private JPanel panelConsultar; // La pestaña para ver la tabla de servicios.
    private JPanel panelOtroCerrajero; // Un panel que se muestra u oculta si se elige "Otro" cerrajero.
    private JTextField txtNombreOtroCerrajero;
    private JTextField txtTelefonoOtroCerrajero;
    private JTable tablaServicios; // La tabla que muestra la lista de servicios.
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiarFormulario;
    private JPanel panelBusqueda;
    private JTextField txtBusquedaCliente;
    private JButton btnBuscar;
    private JButton btnMostrarTodos;

    /**
     * --- CONSTRUCTOR ---
     * Se ejecuta al crear la `VistaPrincipal`. Inicializa los componentes y
     * configura los "listeners" para responder a las acciones del usuario.
     */
    public VistaPrincipal() {
        this.miEmpresa = new Empresa("Cerrajería 24 horas");
        inicializarComponentesLogicos();
        agregarListeners();
    }

    /**
     * --- MÉTODO inicializarComponentesLogicos ---
     * Configura los modelos y renderizadores de los componentes Swing que lo necesitan.
     * Por ejemplo, define el formato de fecha para los JSpinner o carga los datos
     * iniciales en los JComboBox.
     */
    private void inicializarComponentesLogicos() {
        // Configuración de los spinners para fecha, hora y número.
        spinnerFecha.setModel(new SpinnerDateModel());
        spinnerFecha.setEditor(new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy"));
        spinnerHora.setModel(new SpinnerDateModel());
        spinnerHora.setEditor(new JSpinner.DateEditor(spinnerHora, "hh:mm a"));
        spinnerValorServicio.setModel(new SpinnerNumberModel(50000.0, 0.0, 10000000.0, 1000.0));

        // Carga de los items en los ComboBox con opciones fijas.
        comboTipoServicio.setModel(new DefaultComboBoxModel<>(new String[]{"Seleccionar", "..."}));
        comboMunicipio.setModel(new DefaultComboBoxModel<>(new String[]{"Seleccionar", "Bucaramanga", "..."}));
        comboMetodoPago.setModel(new DefaultComboBoxModel<>(new String[]{"Nequi", "Efectivo"}));

        // Llama al método para cargar los cerrajeros desde la base de datos.
        cargarCerrajeros();

        // Configura cómo se debe mostrar cada item en el ComboBox de cerrajeros.
        // Si el item es un objeto Cerrajero, muestra su nombre. Si es un String (como "Otro"), lo muestra tal cual.
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

        // Carga el ComboBox de estados directamente desde el Enum EstadoServicio.
        comboEstado.setModel(new DefaultComboBoxModel<>(EstadoServicio.values()));
        // Le dice al ComboBox que para mostrar cada estado, debe usar su descripción (ej: "En Progreso").
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

        // Deja el formulario en su estado inicial.
        reiniciarFormulario();
    }

    /**
     * --- MÉTODO cargarCerrajeros ---
     * Pide a la clase `Empresa` que cargue los cerrajeros desde la BD y luego
     * los añade al JComboBox correspondiente.
     */
    private void cargarCerrajeros() {
        try {
            // 1. Delega la carga de datos a la clase del modelo.
            miEmpresa.cargarCerrajerosDesdeBD();
            
            // 2. Construye el modelo para el ComboBox.
            DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<>();
            model.addElement("Seleccionar"); // Añade la opción por defecto.
            for (Cerrajero c : miEmpresa.getCerrajeros()) {
                model.addElement(c); // Añade cada objeto Cerrajero.
            }
            model.addElement("Otro"); // Añade la opción "Otro".
            
            // 3. Asigna el modelo al componente de la interfaz.
            comboCerrajero.setModel(model);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(rootPanel, "Error al cargar cerrajeros: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * --- MÉTODO agregarListeners ---
     * Este método es el corazón de la interacción. Asigna "oyentes" (listeners) a los
     * componentes de la interfaz. Un listener es un objeto que "escucha" eventos
     * (como clics) y ejecuta un código en respuesta.
     */
    private void agregarListeners() {
        // Listener para el ComboBox de cerrajeros: si se selecciona "Otro", muestra el panel para ingresar uno nuevo.
        comboCerrajero.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                panelOtroCerrajero.setVisible("Otro".equals(e.getItem()));
                //... (código para reajustar el tamaño de la ventana)
            }
        });

        // Asigna a cada botón la acción que debe realizar al ser presionado.
        // Usa expresiones Lambda (ej: e -> guardarServicio()) para un código más conciso.
        btnGuardarServicio.addActionListener(e -> guardarServicio());
        btnActualizar.addActionListener(e -> actualizarServicio());
        btnEliminar.addActionListener(e -> eliminarServicio());
        btnLimpiarFormulario.addActionListener(e -> reiniciarFormulario());

        // Listener para las pestañas: cuando se selecciona la pestaña de "Consultar", carga los servicios.
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == panelConsultar) {
                cargarServicios(null); // Carga todos los servicios sin filtro.
            }
        });

        // Listener para la tabla: cuando se selecciona una fila, guarda el ID y carga los datos en el formulario.
        tablaServicios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaServicios.getSelectedRow() != -1) {
                idServicioSeleccionado = (Integer) tablaServicios.getValueAt(tablaServicios.getSelectedRow(), 0);
                cargarDatosServicioEnFormulario(idServicioSeleccionado);
            }
        });

        // Listeners para los botones de búsqueda.
        btnBuscar.addActionListener(e -> cargarServicios(txtBusquedaCliente.getText()));
        btnMostrarTodos.addActionListener(e -> {
            txtBusquedaCliente.setText("");
            cargarServicios(null);
        });
    }

    /**
     * --- MÉTODO reiniciarFormulario ---
     * Restablece todos los campos del formulario a su estado inicial, limpia selecciones
     * y ajusta la visibilidad de los botones (ej: muestra "Guardar" y oculta "Actualizar").
     */
    private void reiniciarFormulario() {
        // ... (código para limpiar todos los campos y reajustar botones)
    }

    /**
     * --- MÉTODO validarCampos ---
     * Comprueba que los campos obligatorios del formulario no estén vacíos.
     * @return `true` si la validación es exitosa, `false` en caso contrario.
     */
    private boolean validarCampos() {
        // ... (código para verificar cada campo importante)
        return true;
    }

    /**
     * --- MÉTODO guardarServicio ---
     * 1. Valida los campos.
     * 2. Recopila todos los datos del formulario.
     * 3. Crea los objetos del modelo (`Cliente`, `Cerrajero`, `Servicio`).
     * 4. Delega la operación de guardado al objeto `Servicio`.
     * 5. Muestra un mensaje al usuario y reinicia el formulario.
     */
    private void guardarServicio() {
        if (!validarCampos()) return;
        try {
            // Recopila datos y crea los objetos del modelo.
            Cliente cliente = new Cliente(txtNombreCliente.getText(), ...);
            Cerrajero cerrajero = ...;
            Servicio servicio = new Servicio(..., cliente, cerrajero);
            
            // *** DELEGACIÓN ***
            // La vista no sabe cómo guardar. Solo le dice al servicio: "guárdate".
            servicio.guardar();
            
            JOptionPane.showMessageDialog(rootPanel, "Servicio guardado.", "Éxito", ...);
            // ... (limpieza post-guardado)
        } catch (SQLException ex) {
            // Si el modelo lanza un error, la vista lo captura y se lo muestra al usuario.
            JOptionPane.showMessageDialog(rootPanel, "Error al guardar: " + ex.getMessage(), ...);
        }
    }

    /**
     * --- MÉTODO actualizarServicio ---
     * Similar a guardar, pero para actualizar un servicio existente.
     * 1. Recopila los datos.
     * 2. Carga el servicio original de la BD (usando `Servicio.cargarPorId`).
     * 3. Modifica sus atributos con los nuevos datos del formulario.
     * 4. Delega la operación de actualización al objeto `Servicio`.
     */
    private void actualizarServicio() {
        // ... (validaciones y confirmación)
        try {
             // *** DELEGACIÓN ***
            // La vista crea un objeto de servicio con los datos actualizados y le pide que se actualice.
            Servicio servicioActualizado = new Servicio(idServicioSeleccionado, ...);
            servicioActualizado.actualizar();
            
            JOptionPane.showMessageDialog(rootPanel, "Servicio actualizado.", ...);
            // ... (limpieza post-actualización)
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(rootPanel, "Error al actualizar: " + ex.getMessage(), ...);
        }
    }

    /**
     * --- MÉTODO eliminarServicio ---
     * Pide confirmación y luego delega la eliminación a un método estático de la clase `Servicio`.
     */
    private void eliminarServicio() {
        // ... (confirmación)
        try {
            // *** DELEGACIÓN ***
            // La vista no sabe cómo eliminar. Llama al método estático del modelo.
            Servicio.eliminar(idServicioSeleccionado);
            JOptionPane.showMessageDialog(rootPanel, "Servicio eliminado.", ...);
            // ... (limpieza)
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(rootPanel, "Error al eliminar: " + ex.getMessage(), ...);
        }
    }

    /**
     * --- MÉTODO cargarServicios ---
     * Delega la obtención de la lista de servicios a la clase `Servicio` y luego
     * usa esos datos para construir y mostrar las filas en la `JTable`.
     */
    private void cargarServicios(String filtroCliente) {
        try {
            DefaultTableModel modelo = (DefaultTableModel) tablaServicios.getModel();
            modelo.setRowCount(0); // Limpia la tabla antes de cargar nuevos datos.
            
            // *** DELEGACIÓN ***
            // Pide al modelo la lista de servicios.
            for (Vector<Object> fila : Servicio.listarServicios(filtroCliente)) {
                modelo.addRow(fila); // Añade cada fila al modelo de la tabla.
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(rootPanel, "Error al cargar servicios: " + ex.getMessage(), ...);
        }
    }

    /**
     * --- MÉTODO cargarDatosServicioEnFormulario ---
     * Cuando se selecciona un servicio en la tabla, este método:
     * 1. Pide a la clase `Servicio` que cargue todos los datos de ese servicio por su ID.
     * 2. Rellena todos los campos del formulario con los datos del objeto `Servicio` recibido.
     */
    private void cargarDatosServicioEnFormulario(int idServicio) {
        try {
            // *** DELEGACIÓN ***
            // Pide al modelo que le de el objeto Servicio completo.
            Servicio servicio = Servicio.cargarPorId(idServicio);
            if (servicio == null) return;

            // Una vez que tiene el objeto, la vista se encarga de rellenar sus propios componentes.
            txtNombreCliente.setText(servicio.getCliente().getNombre());
            // ... (rellenar todos los demás campos)
            
            // Lógica para ajustar los botones y cambiar a la pestaña de registro.
            // ...
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(rootPanel, "Error al cargar datos: " + ex.getMessage(), ...);
        }
    }

    // ... (otros métodos auxiliares como ajustarAnchoColumnas)

    // --- MÉTODO getMainPanel ---
    // Permite que la clase `Main` obtenga el panel principal para mostrarlo en la ventana.
    public JPanel getMainPanel() {
        return rootPanel;
    }
}
