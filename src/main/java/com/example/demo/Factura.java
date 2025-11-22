package com.example.demo;

public class Factura {
    private Servicio servicio;

    public Factura(Servicio servicio) {
        this.servicio = servicio;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public void imprimirFactura() {
        System.out.println("Factura del servicio: " + servicio.getId_servicio());
        System.out.println("Cliente: " + servicio.getCliente().getNombre());
        System.out.println("Cerrajero: " + servicio.getCerrajero().getNombre());
        System.out.println("Monto: " + servicio.getMonto_pago());
    }
}
