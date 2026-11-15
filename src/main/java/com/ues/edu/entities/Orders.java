package com.ues.edu.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int oId;

    @Column(nullable = false, length = 150)
    private String oName;

    @Column(nullable = false)
    private double oPrice;

    @Column(nullable = false)
    private int oQuantity;

    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;

    @Column(nullable = false)
    private double totalAmmout;

    @Column(nullable = false)
    private boolean activo = true;

    // === NOMBRE OPCIONAL EN TICKET PARA VENTAS DE MOSTRADOR ===
    @Column(length = 120)
    private String nombreCliente;

    // === CAMPOS DE ESTADO DE COCINA Y PAGO ===
    @Column(nullable = false, length = 30)
    private String estadoOrden = "INGRESADA"; // INGRESADA, PREPARANDO, ENTREGADA

    @Column(nullable = false, length = 30)
    private String estadoPago = "PENDIENTE"; // PENDIENTE, PAGADO

    @Column(length = 40)
    private String metodoPago; // EFECTIVO, TARJETA VISA, TARJETA MASTERCARD, etc.

    @Column(length = 60)
    private String codigoTicket; // Código de autorización o folio de caja

    private Double montoRecibido;

    private Double cambio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_u_id", nullable = true)
    @JsonIgnoreProperties({"orders", "upassword"})
    private User user;

    @PrePersist
    protected void onCreate() {
        if (this.orderDate == null) {
            this.orderDate = new Date();
        }
        if (this.estadoOrden == null || this.estadoOrden.trim().isEmpty()) {
            this.estadoOrden = "INGRESADA";
        }
        if (this.estadoPago == null || this.estadoPago.trim().isEmpty()) {
            this.estadoPago = "PENDIENTE";
        }
        if (this.totalAmmout <= 0 && this.oPrice > 0 && this.oQuantity > 0) {
            this.totalAmmout = Math.round((this.oPrice * this.oQuantity) * 100.0) / 100.0;
        }
    }

    public int getoId() {
        return oId;
    }

    public void setoId(int oId) {
        this.oId = oId;
    }

    public String getoName() {
        return oName;
    }

    public void setoName(String oName) {
        this.oName = oName;
    }

    public double getoPrice() {
        return oPrice;
    }

    public void setoPrice(double oPrice) {
        this.oPrice = oPrice;
    }

    public int getoQuantity() {
        return oQuantity;
    }

    public void setoQuantity(int oQuantity) {
        this.oQuantity = oQuantity;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmmout() {
        return totalAmmout;
    }

    public void setTotalAmmout(double totalAmmout) {
        this.totalAmmout = totalAmmout;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getEstadoOrden() {
        return estadoOrden;
    }

    public void setEstadoOrden(String estadoOrden) {
        this.estadoOrden = estadoOrden;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getCodigoTicket() {
        return codigoTicket;
    }

    public void setCodigoTicket(String codigoTicket) {
        this.codigoTicket = codigoTicket;
    }

    public Double getMontoRecibido() {
        return montoRecibido;
    }

    public void setMontoRecibido(Double montoRecibido) {
        this.montoRecibido = montoRecibido;
    }

    public Double getCambio() {
        return cambio;
    }

    public void setCambio(Double cambio) {
        this.cambio = cambio;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Orders [oId=" + oId + ", oName=" + oName + ", oPrice=" + oPrice + ", oQuantity=" + oQuantity
                + ", nombreCliente=" + nombreCliente + ", orderDate=" + orderDate + ", totalAmmout=" + totalAmmout
                + ", activo=" + activo + ", estadoOrden=" + estadoOrden + ", estadoPago=" + estadoPago
                + ", metodoPago=" + metodoPago + ", codigoTicket=" + codigoTicket
                + ", userId=" + (user != null ? user.getU_id() : null) + "]";
    }
}
