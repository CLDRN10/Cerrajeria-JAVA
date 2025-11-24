package modelo_logica;

/**
 * ==================================================================
 * ENUM EstadoServicio
 * ==================================================================
 * Un "Enum" (enumeración) es un tipo de clase especial en Java que
 * representa un conjunto fijo de constantes o valores. Es la forma
 * perfecta de modelar algo que solo puede tener un número limitado
 * de estados, como los días de la semana, los meses o, en nuestro caso,
 * los estados de un servicio.
 * 
 * PROPÓSITO:
 * Define de forma segura y clara los posibles estados por los que puede
 * pasar un servicio. Usar un Enum en lugar de simples Strings ("Pendiente", "En Progreso")
 * evita errores de tipeo y hace el código mucho más legible y robusto.
 */
public enum EstadoServicio {
    
    // --- VALORES POSIBLES ---
    // Cada uno de estos es, en efecto, un objeto único de tipo EstadoServicio.
    // A cada estado le asociamos una descripción amigable para el usuario.
    PENDIENTE("Pendiente"),
    EN_PROGRESO("En Proceso"),
    FINALIZADO("Finalizado"),
    CANCELADO("Cancelado");

    // --- ATRIBUTO ---
    private final String descripcion; // Almacena la descripción legible del estado.

    /**
     * --- CONSTRUCTOR PRIVADO ---
     * El constructor de un Enum es siempre privado. Se llama automáticamente
     * por cada constante definida arriba (PENDIENTE, EN_PROGRESO, etc.).
     *
     * @param descripcion La cadena de texto asociada a la constante.
     */
    EstadoServicio(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * --- MÉTODO getDescripcion ---
     * Devuelve la descripción legible del estado. Por ejemplo, si tenemos
     * el enum `EstadoServicio.EN_PROGRESO`, este método devolverá el String "En Progreso".
     * Es útil para mostrar el estado en la interfaz de usuario (JTable, JComboBox).
     *
     * @return La descripción en formato String.
     */
    public String getDescripcion() {
        return descripcion;
    }
    
    /**
     * --- MÉTODO fromDescripcion (Estático) ---
     * Este es un método de utilidad muy práctico. Permite obtener el objeto Enum
     * a partir de su descripción en formato String. Es lo contrario a getDescripcion().
     * 
     * Por ejemplo, si leemos "Finalizado" de la base de datos, llamamos a
     * `EstadoServicio.fromDescripcion("Finalizado")` y nos devolverá el objeto
     * `EstadoServicio.FINALIZADO`.
     * 
     * @param descripcion La descripción en String que queremos convertir a Enum.
     * @return El objeto EstadoServicio correspondiente, o null si no se encuentra.
     */
    public static EstadoServicio fromDescripcion(String descripcion) {
        // Recorre todos los valores posibles del Enum (PENDIENTE, EN_PROGRESO, etc.).
        for (EstadoServicio estado : values()) {
            // Compara la descripción de cada estado con la que se pasó como parámetro.
            if (estado.descripcion.equals(descripcion)) {
                // Si encuentra una coincidencia, devuelve el objeto Enum.
                return estado;
            }
        }
        // Si el bucle termina y no encuentra ninguna coincidencia, devuelve null o lanza una excepción.
        // En este caso, devolver null es seguro porque el código que lo usa lo manejará.
        return null; 
    }
}
