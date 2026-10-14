package com.ues.edu.services;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import com.ues.edu.entities.User;
import com.ues.edu.repositories.UserRepository;
import org.springframework.stereotype.Service;

/**
 * Provee las funcionales del CRUD Usuario
 *
 * @author Cristian Rodolfo Aguirre González
 *
 */
@Service
public class UserServices {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUser() {
        List<User> users = (List<User>) this.userRepository.findAll();
        return users;
    }

    public User getUser(int id) {
        Optional<User> optional = this.userRepository.findById(id);
        User user = optional.get();
        return user;
    }

    public User getUserByEmail(String email) {
        User user = this.userRepository.findUserByUemail(email);
        return user;
    }

    public void updateUser(User user, int id) {
        user.setU_id(id);
        this.userRepository.save(user);
    }

    public void deleteUser(int id) {
        this.userRepository.deleteById(id);
    }

    public void addUser(User user) {
        this.userRepository.save(user);
    }

    public boolean validateLoginCredentials(String email, String password) {
        List<User> users = (List<User>) this.userRepository.findAll();
        for (User u : users) {
            if (u != null && u.getUpassword().equals(password) && u.getUemail().equals(email)) {
                return true;
            }
        }
        return false;
    }

}
