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

/**
 * Entidad de Órdenes / Pedidos del sistema SIPAGO.
 *
 * @author Edith Estefany Vanegas Albánez
 */
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

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_u_id", nullable = false)
    @JsonIgnoreProperties({"orders", "upassword"})
    private User user;

    @PrePersist
    protected void onCreate() {
        if (this.orderDate == null) {
            this.orderDate = new Date();
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Orders [oId=" + oId + ", oName=" + oName + ", oPrice=" + oPrice + ", oQuantity=" + oQuantity
                + ", orderDate=" + orderDate + ", totalAmmout=" + totalAmmout + ", activo=" + activo
                + ", userId=" + (user != null ? user.getU_id() : null) + "]";
    }
}
