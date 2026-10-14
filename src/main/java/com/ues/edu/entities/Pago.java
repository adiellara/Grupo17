package com.ues.edu.entities;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 *
 * @author Carlos Alex
 */
public class Pago {

    @NotBlank(message = "El nombre del titular es obligatorio")
    private String nombreTitular;

    @Pattern(regexp = "\\d{16}", message = "El número de tarjeta debe tener 16 dígitos")
    private String numeroTarjeta;

    @Pattern(regexp = "\\d{2}/\\d{2}", message = "Formato de fecha inválido (MM/AA)")
    private String fechaExpiracion;

    @Pattern(regexp = "\\d{3}", message = "El CVV debe tener 3 dígitos")
    private String cvv;

    private Double monto;

    // Getters y Setters
    public String getNombreTitular() {
        return nombreTitular;
    }

    public void setNombreTitular(String nombreTitular) {
        this.nombreTitular = nombreTitular;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(String fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

}
