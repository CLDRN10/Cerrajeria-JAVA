public class Cliente implements Persona {
    // -------------------
    // Atributos
    // -------------------
    private Long id_cliente;
    private String nombre_c;
    private String telefono_c;
    private String direccion_c;
    private String ciudad_c;

    // -------------------
    // Constructor
    // -------------------
    public Cliente(Long id_cliente, String nombre_c, String telefono_c, String direccion_c, String ciudad_c) {
        this.id_cliente = id_cliente;
        this.nombre_c = nombre_c;
        this.telefono_c = telefono_c;
        this.direccion_c = direccion_c;
        this.ciudad_c = ciudad_c;
    }

    // -------------------
    // Getters y Setters
    // -------------------
    public Long getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(Long id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getNombre_c() {
        return nombre_c;
    }

    public void setNombre_c(String nombre_c) {
        this.nombre_c = nombre_c;
    }

    public String getTelefono_c() {
        return telefono_c;
    }

    public void setTelefono_c(String telefono_c) {
        this.telefono_c = telefono_c;
    }

    public String getDireccion_c() {
        return direccion_c;
    }

    public void setDireccion_c(String direccion_c) {
        this.direccion_c = direccion_c;
    }

    public String getCiudad_c() {
        return ciudad_c;
    }

    public void setCiudad_c(String ciudad_c) {
        this.ciudad_c = ciudad_c;
    }

    // -------------------
    // Métodos de la interfaz Persona
    // -------------------
    @Override
    public String getNombre() {
        return nombre_c;
    }

    @Override
    public String getTelefono() {
        return telefono_c;
    }
}
