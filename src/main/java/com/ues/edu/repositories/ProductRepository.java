package com.ues.edu.repositories;


import org.springframework.data.repository.CrudRepository;
import com.ues.edu.entities.Product;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product,Integer>
{
	public Product findByPname(String name);
        // ************ MÉTODO NUEVO PARA LA BÚSQUEDA ************
        // Este método buscará todos los productos cuyo nombre (pname) contenga la cadena,
        // ignorando mayúsculas y minúsculas, y devolverá una lista (List<Product>).

        List<Product> findByPnameContainingIgnoreCase(String pname);

}