public enum EstadoServicio {
    PENDIENTE("Pendiente de asignación"),
    EN_PROCESO("En proceso"),
    FINALIZADO("Finalizado correctamente"),
    CANCELADO("Cancelado por el cliente");

    // -------------------
    // Atributo
    // -------------------
    private final String descripcion;

    // -------------------
    // Constructor
    // -------------------
    EstadoServicio(String descripcion) {
        this.descripcion = descripcion;
    }

    // -------------------
    // Método
    // -------------------
    public String getDescripcion() {
        return descripcion;
    }
}
