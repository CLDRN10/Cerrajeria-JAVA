import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

public class Servicio {

    // Atributos
    private Date fecha;
    private Time hora;
    private String tipo;
    private EstadoServicio estado;
    private BigDecimal monto;
    private String metodoPago;
    private Cliente cliente;
    private Cerrajero cerrajero;

    // Constructor
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

    // Getters
    public Date getFecha() { return fecha; }
    public Time getHora() { return hora; }
    public String getTipo() { return tipo; }
    public EstadoServicio getEstado() { return estado; }
    public BigDecimal getMonto() { return monto; }
    public String getMetodoPago() { return metodoPago; }
    public Cliente getCliente() { return cliente; }
    public Cerrajero getCerrajero() { return cerrajero; }
}
