package com.ues.edu.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import com.ues.edu.entities.Product;
import com.ues.edu.repositories.ProductRepository;
import org.springframework.stereotype.Service;

/**
 * Provee las funcionalidades del Mantenimiento de Productos
 *
 * @author Karla Estefany Menendez Roman
 *
 */
@Service
public class ProductServices {

    @Autowired
    private ProductRepository productRepository;

    public void addProduct(Product p) {
        this.productRepository.save(p);
    }

    public List<Product> getAllProducts() {
        List<Product> products = (List<Product>) this.productRepository.findAll();
        return products;
    }

    public Product getProduct(int id) {
        Optional<Product> optional = this.productRepository.findById(id);
        Product product = optional.get();
        return product;
    }

    public void updateproduct(Product p, int id) {
        p.setPid(id);
        Optional<Product> optional = this.productRepository.findById(id);
        Product prod = optional.get();

        if (prod.getPid() == id) {
            this.productRepository.save(p);
        }
    }

    public void deleteProduct(int id) {
        this.productRepository.deleteById(id);
    }

    public Product getProductByName(String name) {

        Product product = this.productRepository.findByPname(name);
        if (product != null) {
            return product;
        }
        return null;

    }
}
