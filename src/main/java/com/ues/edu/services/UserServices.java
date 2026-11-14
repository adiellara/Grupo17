package com.ues.edu.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ues.edu.entities.User;
import com.ues.edu.repositories.UserRepository;

/**
 * Provee las funcionalidades del CRUD Usuario y control de acceso.
 *
 * @author 
 */
@Service
public class UserServices {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUser() {
        return (List<User>) this.userRepository.findAll();
    }

    public User getUser(int id) {
        return this.userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        if (email == null) {
            return null;
        }
        return this.userRepository.findUserByUemail(email.trim());
    }

    public void updateUser(User user, int id) {
        Optional<User> existingOpt = this.userRepository.findById(id);
        if (existingOpt.isPresent()) {
            User existing = existingOpt.get();
            existing.setUname(user.getUname());
            if (user.getApellido() != null) {
                existing.setApellido(user.getApellido());
            }
            existing.setUemail(user.getUemail());
            existing.setUnumber(user.getUnumber());
            if (user.getGenero() != null) {
                existing.setGenero(user.getGenero());
            }

            // Solo actualiza la contraseña si viene una nueva no vacía
            if (user.getUpassword() != null && !user.getUpassword().trim().isEmpty()) {
                existing.setUpassword(user.getUpassword());
            }

            this.userRepository.save(existing);
        }
    }

    public void deleteUser(int id) {
        if (this.userRepository.existsById(id)) {
            this.userRepository.deleteById(id);
        }
    }

    public void addUser(User user) {
        if (user.getUemail() != null) {
            user.setUemail(user.getUemail().trim().toLowerCase());
        }
        user.setActivo(true);
        this.userRepository.save(user);
    }

    public void addClient(User user) {
        if (user.getUemail() != null) {
            user.setUemail(user.getUemail().trim().toLowerCase());
        }
        User existente = userRepository.findUserByUemail(user.getUemail());
        if (existente != null) {
            throw new IllegalArgumentException("Correo ya registrado");
        }
        user.setActivo(true);
        userRepository.save(user);
    }

    /**
     * Valida el login del cliente con correo, contraseña y estado activo.
     */
    public User login(String email, String password) {
        if (email == null || password == null || email.trim().isEmpty() || password.isEmpty()) {
            return null;
        }
        User user = userRepository.findByUemailAndUpassword(email.trim(), password);
        if (user != null && !user.isActivo()) {
            return null; // Bloquea el acceso a usuarios inactivos
        }
        return user;
    }

    // Método para activar / inactivar usuarios desde el Panel Admin
    public void toggleUserStatus(int id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setActivo(!user.isActivo());
            userRepository.save(user);
        }
    }

    public List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return (List<User>) userRepository.findAll();
        }
        return userRepository.searchUsers(keyword.trim());
    }
}
