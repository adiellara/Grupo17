package com.ues.edu.entities;

import java.util.List;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int u_id;

    @Column(nullable = false, length = 100)
    private String uname;

    @Column(length = 100)
    private String apellido;

    @Column(nullable = false, unique = true, length = 120)
    private String uemail;

    @Column(length = 20)
    private String genero;

    @JsonIgnore
    @Column(nullable = false, length = 255)
    private String upassword;

    @Column(length = 20)
    private String unumber;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(nullable = false, length = 30)
    private String rol = "CLIENTE";

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Orders> orders;

    @PrePersist
    protected void onCreate() {
        if (this.rol == null || this.rol.trim().isEmpty()) {
            this.rol = "CLIENTE";
        }
    }

    public User() {}

    public User(String uemail, String upassword) {
        this.uemail = uemail;
        this.upassword = upassword;
    }

    public int getU_id() { return u_id; }
    public void setU_id(int u_id) { this.u_id = u_id; }
    public String getUname() { return uname; }
    public void setUname(String uname) { this.uname = uname; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getUemail() { return uemail; }
    public void setUemail(String uemail) { this.uemail = uemail; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public String getUpassword() { return upassword; }
    public void setUpassword(String upassword) { this.upassword = upassword; }
    public String getUnumber() { return unumber; }
    public void setUnumber(String unumber) { this.unumber = unumber; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public List<Orders> getOrders() { return orders; }
    public void setOrders(List<Orders> orders) { this.orders = orders; }

    @Override
    public String toString() {
        return "User [u_id=" + u_id + ", uname=" + uname + ", apellido=" + apellido + ", uemail=" + uemail
                + ", unumber=" + unumber + ", activo=" + activo + ", rol=" + rol + "]";
    }
}
