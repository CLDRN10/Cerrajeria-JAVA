import java.util.List;

public class Empresa {

    // Atributos
    private String nombre;
    private List<Cerrajero> cerrajeros;

    // Constructor
    public Empresa(String nombre, List<Cerrajero> cerrajeros) {
        this.nombre = nombre;
        this.cerrajeros = cerrajeros;
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public List<Cerrajero> getCerrajeros() {
        return cerrajeros;
    }
}
