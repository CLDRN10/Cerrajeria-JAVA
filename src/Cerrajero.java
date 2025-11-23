public class Cerrajero implements Persona {

    // Atributos
    private Long idCerrajero;
    private String nombre;
    private String telefono;

    // Constructores
    public Cerrajero(Long idCerrajero, String nombre, String telefono) {
        this.idCerrajero = idCerrajero;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public Cerrajero(String nombre, String telefono) {
        this.nombre = nombre;
        this.telefono = telefono;
    }

    // Métodos de la interfaz Persona
    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public String getTelefono() {
        return telefono;
    }

    // Getters y Setters (Solo los necesarios)
    public Long getIdCerrajero() {
        return idCerrajero;
    }

    public void setIdCerrajero(Long idCerrajero) {
        this.idCerrajero = idCerrajero;
    }
}
