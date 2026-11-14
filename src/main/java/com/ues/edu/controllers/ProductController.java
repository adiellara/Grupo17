package com.ues.edu.controllers;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ues.edu.entities.Product;
import com.ues.edu.services.ProductServices;

/**
 * Modulo Producto
 *
 * @author Susana Guadalupe Martínez Medrano
 */
@Controller
public class ProductController {

    @Autowired
    private ProductServices productServices;

    private boolean isAdminAuthenticated(HttpSession session) {
        return session != null && session.getAttribute("adminLogueado") != null;
    }

    private void applyNoCacheHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    @PostMapping("/addingProduct")
    public String addProduct(@ModelAttribute Product product,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (!isAdminAuthenticated(session)) return "redirect:/admin";

        if (product.getPprice() < 0) {
            redirectAttributes.addFlashAttribute("error", "El precio del producto no puede ser negativo.");
            return "redirect:/admin/services";
        }
        this.productServices.addProduct(product);
        redirectAttributes.addFlashAttribute("success", "Producto registrado correctamente.");
        return "redirect:/admin/services";
    }

    @GetMapping("/updatingProduct/{productId}")
    public String updateProduct(@ModelAttribute Product product,
                                @PathVariable("productId") int id,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (!isAdminAuthenticated(session)) return "redirect:/admin";

        if (product.getPprice() < 0) {
            redirectAttributes.addFlashAttribute("error", "El precio del producto no puede ser negativo.");
            return "redirect:/admin/services";
        }
        this.productServices.updateproduct(product, id);
        redirectAttributes.addFlashAttribute("success", "Producto actualizado correctamente.");
        return "redirect:/admin/services";
    }

    @GetMapping("/deleteProduct/{productId}")
    public String delete(@PathVariable("productId") int id,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        if (!isAdminAuthenticated(session)) return "redirect:/admin";

        this.productServices.deleteProduct(id);
        redirectAttributes.addFlashAttribute("success", "Producto eliminado correctamente.");
        return "redirect:/admin/services";
    }

    @GetMapping("/admin/products")
    public String getAllProducts(Model model, HttpSession session, HttpServletResponse response) {
        applyNoCacheHeaders(response);
        if (!isAdminAuthenticated(session)) return "redirect:/admin";

        List<Product> products = this.productServices.getAllProducts();
        model.addAttribute("products", products);
        return "Tabla_Products";
    }

    @GetMapping("/admin/products/search")
    public String searchProducts(@RequestParam("productName") String productName,
                                 Model model,
                                 HttpSession session,
                                 HttpServletResponse response) {
        applyNoCacheHeaders(response);
        if (!isAdminAuthenticated(session)) return "redirect:/admin";

        List<Product> searchResults = this.productServices.searchProductsByName(productName);
        model.addAttribute("products", searchResults);
        return "Tabla_Products";
    }
}
