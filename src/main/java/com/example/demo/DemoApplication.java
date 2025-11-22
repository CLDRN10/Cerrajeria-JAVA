package com.example.demo;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class DemoApplication {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Gestión de Cerrajería");
            VistaPrincipal vista = new VistaPrincipal();
            frame.setContentPane(vista.getMainPanel());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack(); // Ajusta el tamaño de la ventana a los componentes
            frame.setLocationRelativeTo(null); // Centra la ventana
            frame.setVisible(true);

            // En el futuro, aquí conectaremos la vista con el controlador
            // ConexionBD conexion = new ConexionBD();
            // Controlador controlador = new Controlador(vista, conexion);
        });
    }
}
