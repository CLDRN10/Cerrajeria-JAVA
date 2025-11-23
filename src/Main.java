import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        // SwingUtilities.invokeLater asegura que el código de la UI se ejecute en el hilo correcto (Event Dispatch Thread).
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Gestión de Cerrajería");
            VistaPrincipal vista = new VistaPrincipal();
            frame.setContentPane(vista.getMainPanel());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // --- LA SECUENCIA CANÓNICA DE SWING ---
            
            // 1. pack(): Calcula el tamaño óptimo de la ventana basándose en el contenido.
            // Esto ahora funcionará porque ya no hay un "pack()" conflictivo dentro de VistaPrincipal.
            frame.pack();

            // 2. setLocationRelativeTo(null): Centra la ventana (ahora con el tamaño correcto) en la pantalla.
            frame.setLocationRelativeTo(null);

            // 3. setVisible(true): Muestra la ventana al usuario, ya perfectamente dimensionada y centrada.
            frame.setVisible(true);
        });
    }
}
