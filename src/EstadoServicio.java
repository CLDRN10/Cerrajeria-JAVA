public enum EstadoServicio {
    PENDIENTE("Pendiente"),
    EN_PROCESO("En proceso"),
    FINALIZADO("Finalizado"),
    CANCELADO("Cancelado");

    private final String descripcion;

    EstadoServicio(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    // Este método permite encontrar un enum por su descripción, útil para la carga desde BD
    public static EstadoServicio fromDescripcion(String descripcion) {
        for (EstadoServicio estado : EstadoServicio.values()) {
            if (estado.descripcion.equalsIgnoreCase(descripcion)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("No se encontró un estado con la descripción: " + descripcion);
    }
}
