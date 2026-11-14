package com.ues.edu.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.ues.edu.entities.Admin;
import com.ues.edu.repositories.AdminRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public void run(String... args) throws Exception {
        if (adminRepository.count() == 0) {
            crearStaff("Administrador del Sistema", "admin@sipago.com", "Admin1234", "22251500", "ADMIN");
            crearStaff("Gerente General", "gerente@sipago.com", "Gerente123", "22251501", "GERENTE");
            crearStaff("Cajero Principal", "cajero@sipago.com", "Cajero123", "22251502", "CAJERO");
            crearStaff("Chef de Cocina", "cocinero@sipago.com", "Cocinero123", "22251503", "COCINERO");
            System.out.println("✅ Cuentas iniciales de personal creadas (ADMIN, GERENTE, CAJERO, COCINERO).");
        }
    }

    private void crearStaff(String nombre, String email, String pass, String tel, String rol) {
        Admin staff = new Admin();
        staff.setAdminName(nombre);
        staff.setAdminEmail(email);
        staff.setAdminPassword(pass);
        staff.setAdminNumber(tel);
        staff.setRol(rol);
        adminRepository.save(staff);
    }
}