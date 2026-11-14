package com.ues.edu.repositories;

import org.springframework.data.repository.CrudRepository;
import com.ues.edu.entities.User;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    public User findUserByUemail(String email);
// Login (devuelve el usuario sin filtrar por activo)

    User findByUemailAndUpassword(String email, String password);

    @Query("SELECT u FROM User u WHERE "
            + "LOWER(u.uname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
            + "LOWER(u.uemail) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> searchUsers(@Param("keyword") String keyword);

}
