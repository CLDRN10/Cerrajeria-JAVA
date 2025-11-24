import vista_interfaz.VistaPrincipal;
import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Gestión de Servicios de Cerrajería");
        VistaPrincipal vista = new VistaPrincipal();       
        frame.setContentPane(vista.getMainPanel());      
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);       
        frame.pack();       
        frame.setLocationRelativeTo(null);       
        frame.setVisible(true);
    }
}
