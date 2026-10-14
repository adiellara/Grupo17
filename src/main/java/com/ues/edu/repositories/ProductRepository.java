package com.ues.edu.repositories;


import org.springframework.data.repository.CrudRepository;
import com.ues.edu.entities.Product;
import org.springframework.stereotype.Repository;
@Repository
public interface ProductRepository extends CrudRepository<Product,Integer>
{
	public Product findByPname(String name);

}