package com.ues.edu.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ues.edu.entities.Admin;
import com.ues.edu.repositories.AdminRepository;

@Service
public class AdminServices {

    @Autowired
    private AdminRepository adminRepository;

    public List<Admin> getAll() {
        return (List<Admin>) this.adminRepository.findAll();
    }

    public Admin getAdmin(int id) {
        return this.adminRepository.findById(id).orElse(null);
    }

    public void update(Admin admin, int id) {
        Optional<Admin> existingOpt = this.adminRepository.findById(id);
        if (existingOpt.isPresent()) {
            Admin existing = existingOpt.get();
            existing.setAdminName(admin.getAdminName());
            existing.setAdminEmail(admin.getAdminEmail());
            existing.setAdminNumber(admin.getAdminNumber());

            // Solo actualiza la contraseña si se envió una nueva no vacía
            if (admin.getAdminPassword() != null && !admin.getAdminPassword().trim().isEmpty()) {
                existing.setAdminPassword(admin.getAdminPassword());
            }
            this.adminRepository.save(existing);
        }
    }

    public void delete(int id) {
        if (this.adminRepository.existsById(id)) {
            this.adminRepository.deleteById(id);
        }
    }

    public void addAdmin(Admin admin) {
        if (admin.getAdminEmail() != null) {
            admin.setAdminEmail(admin.getAdminEmail().trim().toLowerCase());
        }
        this.adminRepository.save(admin);
    }

    public boolean validateAdminCredentials(String email, String password) {
        if (email == null || password == null || email.trim().isEmpty() || password.isEmpty()) {
            return false;
        }
        Admin admin = adminRepository.findByAdminEmail(email.trim());
        return admin != null && admin.getAdminPassword() != null && admin.getAdminPassword().equals(password);
    }

    public Admin getAdminByEmail(String email) {
        if (email == null) {
            return null;
        }
        return adminRepository.findByAdminEmail(email.trim());
    }
}