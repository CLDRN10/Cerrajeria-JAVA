import java.util.List;

public class Empresa {
   
    // Atributos - Propiedades - Características
    private String nombre;
    private List<Cerrajero> cerrajeros;

    //Métodos - Funciones
    public Empresa(String nombre, List<Cerrajero> cerrajeros) {
        this.nombre = nombre;
        this.cerrajeros = cerrajeros;
    }

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
