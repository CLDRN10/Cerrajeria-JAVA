public class Cliente implements Persona {

    // Atributos con convención camelCase
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

    // Getters y Setters de la clase Cliente
    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
}
