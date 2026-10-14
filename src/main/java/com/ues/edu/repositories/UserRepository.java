package com.ues.edu.repositories;

import org.springframework.data.repository.CrudRepository;
import com.ues.edu.entities.User;
import org.springframework.stereotype.Repository;
@Repository
public interface UserRepository extends CrudRepository<User,Integer>
{
public User findUserByUemail(String email);
}