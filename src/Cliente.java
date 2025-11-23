public class Cliente implements Persona {

    // Atributos
    private Long idCliente;
    private String nombre;
    private String telefono;
    private String direccion;
    private String ciudad;

    // Constructor
    public Cliente(Long idCliente, String nombre, String telefono, String direccion, String ciudad) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.ciudad = ciudad;
    }

    public Cliente(String nombre, String telefono, String direccion, String ciudad) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.ciudad = ciudad;
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
    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getCiudad() {
        return ciudad;
    }
}
