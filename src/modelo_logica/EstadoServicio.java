package modelo_logica;

public enum EstadoServicio {
    
    PENDIENTE("Pendiente"),
    EN_PROGRESO("En Proceso"),
    FINALIZADO("Finalizado"),
    CANCELADO("Cancelado");

    //Atributos - Caracteristicas - Propiedades
    private final String descripcion;


    //Metodos - Funciones
    EstadoServicio(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
    
    public static EstadoServicio fromDescripcion(String descripcion) {
        for (EstadoServicio estado : values()) {
            if (estado.descripcion.equals(descripcion)) {
                return estado;
            }
        }
        return null; 
    }
}
