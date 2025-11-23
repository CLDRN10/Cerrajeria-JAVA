import vista_interfaz.VistaPrincipal;
import javax.swing.JFrame;

/**
 * ==================================================================
 * CLASE Main
 * ==================================================================
 * Es la clase que contiene el punto de entrada de toda la aplicación Java.
 * 
 * PROPÓSITO:
 * Su única responsabilidad es arrancar la aplicación, creando y mostrando
 * la ventana principal (`VistaPrincipal`).
 */
public class Main {

    /**
     * --- MÉTODO main (Estático) ---
     * Este es el primer método que se ejecuta cuando se inicia el programa.
     * La Máquina Virtual de Java (JVM) busca y ejecuta este método.
     * 
     * @param args Argumentos de línea de comandos (no se usan en esta aplicación).
     */
    public static void main(String[] args) {
        // Crea una nueva ventana (JFrame), que es el contenedor principal de la aplicación.
        JFrame frame = new JFrame("Gestión de Servicios de Cerrajería");
        
        // Crea una instancia de nuestra clase VistaPrincipal, que contiene todos los componentes
        // de la interfaz (botones, campos de texto, tablas, etc.).
        VistaPrincipal vista = new VistaPrincipal();
        
        // Le dice a la ventana que su contenido será el panel principal de nuestra VistaPrincipal.
        frame.setContentPane(vista.getMainPanel());
        
        // Define la operación por defecto cuando el usuario cierra la ventana.
        // EXIT_ON_CLOSE asegura que el programa termine completamente.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Ajusta el tamaño de la ventana automáticamente para que todos los componentes
        // quepan según sus tamaños preferidos.
        frame.pack();
        
        // Centra la ventana en medio de la pantalla.
        frame.setLocationRelativeTo(null);
        
        // Finalmente, hace que la ventana sea visible para el usuario.
        frame.setVisible(true);
    }
}
