import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

public class Servicio {

    // Atributos - Propiedades - Características
    private Long id_servicio;
    private Date fecha_s;
    private Time hora_s;
    private String tipo_s;
    private EstadoServicio estado_s;
    private BigDecimal monto_pago;
    private String metodo_pago;
    private Cliente cliente;
    private Cerrajero cerrajero;

    //Metodos - Funciones
    public Servicio(Long id_servicio, Date fecha_s, Time hora_s, String tipo_s, EstadoServicio estado_s, BigDecimal monto_pago, String metodo_pago, Cliente cliente, Cerrajero cerrajero) {
        this.id_servicio = id_servicio;
        this.fecha_s = fecha_s;
        this.hora_s = hora_s;
        this.tipo_s = tipo_s;
        this.estado_s = estado_s;
        this.monto_pago = monto_pago;
        this.metodo_pago = metodo_pago;
        this.cliente = cliente;
        this.cerrajero = cerrajero;
    }

    public Long getId_servicio() {
        return id_servicio;
    }

    public void setId_servicio(Long id_servicio) {
        this.id_servicio = id_servicio;
    }

    public Date getFecha_s() {
        return fecha_s;
    }

    public void setFecha_s(Date fecha_s) {
        this.fecha_s = fecha_s;
    }

    public Time getHora_s() {
        return hora_s;
    }

    public void setHora_s(Time hora_s) {
        this.hora_s = hora_s;
    }

    public String getTipo_s() {
        return tipo_s;
    }

    public void setTipo_s(String tipo_s) {
        this.tipo_s = tipo_s;
    }

    public EstadoServicio getEstado_s() {
        return estado_s;
    }

    public void setEstado_s(EstadoServicio estado_s) {
        this.estado_s = estado_s;
    }

    public BigDecimal getMonto_pago() {
        return monto_pago;
    }

    public void setMonto_pago(BigDecimal monto_pago) {
        this.monto_pago = monto_pago;
    }

    public String getMetodo_pago() {
        return metodo_pago;
    }

    public void setMetodo_pago(String metodo_pago) {
        this.metodo_pago = metodo_pago;
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
