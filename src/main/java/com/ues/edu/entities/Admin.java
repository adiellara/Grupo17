package com.ues.edu.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Entidad que representa al personal operativo y administrativo de SIPAGO.
 * Roles soportados: ADMIN, GERENTE, CAJERO, COCINERO
 *
 * @author Carlos Alexander De Leon
 */
@Entity
@Table(name = "admin")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int adminId;

    @NotNull
    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 100)
    private String adminName;

    @NotNull
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Formato de correo inválido")
    @Column(nullable = false, unique = true, length = 120)
    private String adminEmail;

    @JsonIgnore
    @Column(nullable = false, length = 255)
    private String adminPassword;

    @Column(length = 20)
    private String adminNumber;

    @Column(nullable = false, length = 30)
    private String rol = "ADMIN"; // ADMIN, GERENTE, CAJERO, COCINERO

    @PrePersist
    protected void onCreate() {
        if (this.rol == null || this.rol.trim().isEmpty()) {
            this.rol = "ADMIN";
        }
    }

    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }
    public String getAdminName() { return adminName; }
    public void setAdminName(String adminName) { this.adminName = adminName; }
    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }
    public String getAdminPassword() { return adminPassword; }
    public void setAdminPassword(String adminPassword) { this.adminPassword = adminPassword; }
    public String getAdminNumber() { return adminNumber; }
    public void setAdminNumber(String adminNumber) { this.adminNumber = adminNumber; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    @Override
    public String toString() {
        return "Admin [adminId=" + adminId + ", adminName=" + adminName + ", adminEmail=" + adminEmail
                + ", adminNumber=" + adminNumber + ", rol=" + rol + "]";
    }
}