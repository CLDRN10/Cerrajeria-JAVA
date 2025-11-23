import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

public class Servicio {

    // Atributos con convención camelCase y nombres simplificados
    private Long idServicio;
    private Date fecha;
    private Time hora;
    private String tipo;
    private EstadoServicio estado;
    private BigDecimal monto;
    private String metodoPago;
    
    // --- Composición: Un servicio TIENE UN Cliente y UN Cerrajero ---
    private Cliente cliente;
    private Cerrajero cerrajero;

    // Constructor completo
    public Servicio(Long idServicio, Date fecha, Time hora, String tipo, EstadoServicio estado, BigDecimal monto, String metodoPago, Cliente cliente, Cerrajero cerrajero) {
        this.idServicio = idServicio;
        this.fecha = fecha;
        this.hora = hora;
        this.tipo = tipo;
        this.estado = estado;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.cliente = cliente;
        this.cerrajero = cerrajero;
    }

    // Constructor para servicios nuevos (sin ID inicial)
    public Servicio(Date fecha, Time hora, String tipo, EstadoServicio estado, BigDecimal monto, String metodoPago, Cliente cliente, Cerrajero cerrajero) {
        this.fecha = fecha;
        this.hora = hora;
        this.tipo = tipo;
        this.estado = estado;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.cliente = cliente;
        this.cerrajero = cerrajero;
    }

    // Getters y Setters
    public Long getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(Long idServicio) {
        this.idServicio = idServicio;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Time getHora() {
        return hora;
    }

    public void setHora(Time hora) {
        this.hora = hora;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public EstadoServicio getEstado() {
        return estado;
    }

    public void setEstado(EstadoServicio estado) {
        this.estado = estado;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Cerrajero getCerrajero() {
        return cerrajero;
    }

    public void setCerrajero(Cerrajero cerrajero) {
        this.cerrajero = cerrajero;
    }
}
