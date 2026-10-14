package com.ues.edu.entities;

import java.time.LocalDateTime;

/**
 *
 * @author Carlos Alex
 */
public class TicketCompra {

    private String codigo;
    private String titular;
    private double monto;
    private LocalDateTime fecha;

    public TicketCompra(String codigo, String titular, double monto, LocalDateTime fecha) {
        this.codigo = codigo;
        this.titular = titular;
        this.monto = monto;
        this.fecha = fecha;
    }

    // Getters
    public String getCodigo() {
        return codigo;
    }

    public String getTitular() {
        return titular;
    }

    public double getMonto() {
        return monto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

}
