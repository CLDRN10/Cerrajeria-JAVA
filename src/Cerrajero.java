public class Cerrajero implements Persona {

    // Atributos - Propiedades - Características
    private Long id_cerrajero;
    private String nombre_ce;
    private String telefono_ce;

    // Métodos - Funciones
    public Cerrajero(Long id_cerrajero, String nombre_ce, String telefono_ce) {
        this.id_cerrajero = id_cerrajero;
        this.nombre_ce = nombre_ce;
        this.telefono_ce = telefono_ce;
    }

    public Long getId_cerrajero() {
        return id_cerrajero;
    }

    public void setId_cerrajero(Long id_cerrajero) {
        this.id_cerrajero = id_cerrajero;
    }

    public String getNombre_ce() {
        return nombre_ce;
    }

    public void setNombre_ce(String nombre_ce) {
        this.nombre_ce = nombre_ce;
    }

    public String getTelefono_ce() {
        return telefono_ce;
    }

    public void setTelefono_ce(String telefono_ce) {
        this.telefono_ce = telefono_ce;
    }

    @Override
    public String getNombre() {
        return nombre_ce;
    }

    @Override
    public String getTelefono() {
        return telefono_ce;
    }
}
