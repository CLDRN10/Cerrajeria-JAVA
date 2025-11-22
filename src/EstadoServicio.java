public enum EstadoServicio {
    PENDIENTE("Pendiente de asignación"),
    EN_PROCESO("En proceso"),
    FINALIZADO("Finalizado correctamente"),
    CANCELADO("Cancelado por el cliente");

    // Atributos - Propiedades - Características
    private final String descripcion;

    //Metodos - Funciones
    EstadoServicio(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
