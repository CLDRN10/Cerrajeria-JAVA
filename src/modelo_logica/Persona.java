package modelo_logica;

/**
 * ==================================================================
 * CLASE Persona (Clase Abstracta)
 * ==================================================================
 * Esta es una clase "abstracta", lo que significa que actúa como una
 * plantilla o un modelo para otras clases. No se pueden crear objetos
 * directamente de `Persona`, pero sí se pueden crear de clases que
 * la "hereden" (como Cliente y Cerrajero).
 * 
 * PROPÓSITO:
 * Define las características y comportamientos comunes que comparte
 * cualquier persona en el sistema, en este caso, un nombre y un teléfono.
 * Esto evita repetir el mismo código en las clases `Cliente` y `Cerrajero`,
 * aplicando así el principio de Herencia de la POO.
 */
public abstract class Persona {

    // --- ATRIBUTOS ---
    // Son las variables que definen las características de una Persona.
    // Usamos "private" para aplicar el "encapsulamiento": los atributos
    // solo pueden ser accedidos o modificados a través de los métodos de esta clase (getters y setters).
    private String nombre;   // Almacena el nombre de la persona.
    private String telefono; // Almacena el número de teléfono de la persona.

    /**
     * --- CONSTRUCTOR ---
     * Es un método especial que se llama automáticamente cuando se crea un objeto
     * de una clase que hereda de Persona (como Cliente o Cerrajero).
     * Su función es inicializar los atributos con los valores que se le pasan como parámetros.
     * 
     * @param nombre El nombre de la persona a crear.
     * @param telefono El teléfono de la persona a crear.
     */
    public Persona(String nombre, String telefono) {
        // La palabra "this" se usa para referirse a los atributos de la propia clase.
        // "this.nombre" es el atributo de la clase, y "nombre" es el parámetro que llega al método.
        this.nombre = nombre;
        this.telefono = telefono;
    }

    /**
     * --- GETTERS Y SETTERS ---
     * Son métodos públicos que permiten acceder (get) y modificar (set)
     * los atributos privados de la clase, controlando así el acceso a los datos.
     */

    // Devuelve el valor del atributo "nombre".
    public String getNombre() {
        return nombre;
    }

    // Establece o cambia el valor del atributo "nombre".
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Devuelve el valor del atributo "telefono".
    public String getTelefono() {
        return telefono;
    }

    // Establece o cambia el valor del atributo "telefono".
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
