package com.ues.edu.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ues.edu.entities.Product;
import com.ues.edu.loginCredentials.AdminLogin;
import com.ues.edu.services.ProductServices;

/**
 * Modulo Bienvenida
 *
 * @author Carlos Alexander De Leon
 */
@Controller
public class HomeController {

    @Autowired
    private ProductServices productServices;

    @GetMapping(value = {"/home", "/"})
    public String home(Model model) {
        System.out.println("--> Accediendo a la pagina principal (/home)");
        List<Product> featuredProducts = this.productServices.getAllProducts();
        model.addAttribute("featuredProducts", featuredProducts);
        return "Home";
    }

    @GetMapping("/products")
    public String products(Model model) {
        System.out.println("--> Consultando catalogo publico (/products)");
        List<Product> allProducts = this.productServices.getAllProducts();
        model.addAttribute("products", allProducts);
        return "Products";
    }

    @GetMapping("/location")
    public String location() {
        return "Locate_us";
    }

    @GetMapping("/about")
    public String about() {
        return "About";
    }

    @GetMapping("/admin")
    public String loginAdd(Model model) {
        System.out.println("--> Accediendo a login administrativo (/admin)");
        model.addAttribute("adminLogin", new AdminLogin());
        return "Log_Admin";
    }
}