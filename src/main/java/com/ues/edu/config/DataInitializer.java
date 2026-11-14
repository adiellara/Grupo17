package com.ues.edu.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ues.edu.entities.Admin;
import com.ues.edu.repositories.AdminRepository;

/**
 * Inicializador automático de datos esenciales al arrancar SIPAGO.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public void run(String... args) throws Exception {
        // Verifica si no existe ningún administrador en la base de datos
        if (adminRepository.count() == 0) {
            Admin defaultAdmin = new Admin();
            defaultAdmin.setAdminName("Administrador Principal");
            defaultAdmin.setAdminEmail("admin@sipago.com");
            defaultAdmin.setAdminPassword("Admin1234");
            defaultAdmin.setAdminNumber("22251500");

            adminRepository.save(defaultAdmin);
            System.out.println("Administrador por defecto creado: admin@sipago.com / Admin1234");
        }
    }
}
