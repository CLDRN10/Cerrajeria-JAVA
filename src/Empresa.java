import java.util.List;

public class Empresa {
    // -------------------
    // Atributos
    // -------------------
    private String nombre;
    private List<Cerrajero> cerrajeros;

    // -------------------
    // Constructor
    // -------------------
    public Empresa(String nombre, List<Cerrajero> cerrajeros) {
        this.nombre = nombre;
        this.cerrajeros = cerrajeros;
    }

    // -------------------
    // Getters y Setters
    // -------------------
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Cerrajero> getCerrajeros() {
        return cerrajeros;
    }

    public void setCerrajeros(List<Cerrajero> cerrajeros) {
        this.cerrajeros = cerrajeros;
    }
}
