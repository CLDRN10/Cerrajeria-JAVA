package com.example.demo;

public class Cerrajero implements Persona {
    // -------------------
    // Atributos
    // -------------------
    private Long id_cerrajero;
    private String nombre_ce;
    private String telefono_ce;

    // -------------------
    // Constructor
    // -------------------
    public Cerrajero(Long id_cerrajero, String nombre_ce, String telefono_ce) {
        this.id_cerrajero = id_cerrajero;
        this.nombre_ce = nombre_ce;
        this.telefono_ce = telefono_ce;
    }

    // -------------------
    // Getters y Setters
    // -------------------
    public Long getId_cerrajero() {
        return id_cerrajero;
    }

    public void setId_cerrajero(Long id_cerrajero) {
        this.id_cerrajero = id_cerrajero;
    }

    public String getNombre_ce() {
        return nombre_ce;
    }

    public void setNombre_ce(String nombre_ce) {
        this.nombre_ce = nombre_ce;
    }

    public String getTelefono_ce() {
        return telefono_ce;
    }

    public void setTelefono_ce(String telefono_ce) {
        this.telefono_ce = telefono_ce;
    }

    // -------------------
    // Métodos de la interfaz Persona
    // -------------------
    @Override
    public String getNombre() {
        return nombre_ce;
    }

    @Override
    public String getTelefono() {
        return telefono_ce;
    }
}
