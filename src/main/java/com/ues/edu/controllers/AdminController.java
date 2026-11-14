package com.ues.edu.controllers;

import java.util.Date;
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

import com.ues.edu.count.Logic;
import com.ues.edu.entities.Admin;
import com.ues.edu.entities.Orders;
import com.ues.edu.entities.Product;
import com.ues.edu.entities.User;
import com.ues.edu.loginCredentials.AdminLogin;
import com.ues.edu.repositories.OrderRepository;
import com.ues.edu.services.AdminServices;
import com.ues.edu.services.OrderServices;
import com.ues.edu.services.ProductServices;
import com.ues.edu.services.UserServices;

/**
 * Modulo Administrador
 *
 * @author Carlos Alexander De Leon
 */
@Controller
public class AdminController {

    @Autowired
    private UserServices services;

    @Autowired
    private AdminServices adminServices;

    @Autowired
    private ProductServices productServices;

    @Autowired
    private OrderServices orderServices;

    @Autowired
    private OrderRepository orderRepository;

    private boolean isAdminAuthenticated(HttpSession session) {
        return session != null && session.getAttribute("adminLogueado") != null;
    }

    private void applyNoCacheHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    @PostMapping("/adminLogin")
    public String getAllData(@ModelAttribute("adminLogin") AdminLogin login,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {
        String email = login.getEmail();
        String password = login.getPassword();

        if (adminServices.validateAdminCredentials(email, password)) {
            Admin adminCompleto = adminServices.getAdminByEmail(email);
            session.setAttribute("adminLogueado", adminCompleto != null ? adminCompleto : email);
            return "redirect:/admin/services";
        } else {
            redirectAttributes.addFlashAttribute("error", "❌ Credenciales incorrectas. Intenta nuevamente.");
            return "redirect:/admin";
        }
    }

    public List<Orders> getOrdersForUsername(String username) {
        return orderRepository.findByUser_UnameContainingIgnoreCase(username);
    }

    // === BÚSQUEDA DE PRODUCTO Y ORDEN PARA CLIENTE LOGUEADO ===
    @PostMapping("/product/search")
    public String seachHandler(@RequestParam("productName") String name,
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        User usuarioSesion = (User) session.getAttribute("usuarioLogueado");
        if (usuarioSesion == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para buscar productos.");
            return "redirect:/login";
        }

        List<Orders> orders = this.orderServices.getOrdersForUser(usuarioSesion);
        model.addAttribute("name", usuarioSesion.getUname());
        model.addAttribute("orders", orders);

        Product product = this.productServices.getProductByName(name != null ? name.trim() : "");
        if (product == null) {
            model.addAttribute("message", "Lo sentimos, el producto solicitado no está disponible.");
            model.addAttribute("product", null);
            return "BuyProduct";
        }

        model.addAttribute("product", product);
        return "BuyProduct";
    }

    @PostMapping("/product/order")
    public String orderHandler(@ModelAttribute Orders order,
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        User usuarioSesion = (User) session.getAttribute("usuarioLogueado");
        if (usuarioSesion == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para realizar una orden.");
            return "redirect:/login";
        }

        double totalAmount = Logic.countTotal(order.getoPrice(), order.getoQuantity());
        order.setTotalAmmout(totalAmount);
        order.setUser(usuarioSesion);
        order.setOrderDate(new Date());
        order.setActivo(true);
        this.orderServices.saveOrder(order);

        model.addAttribute("amount", totalAmount);
        model.addAttribute("productName", order.getoName());
        return "Order_success";
    }

    @GetMapping({"/product/back", "/buyProduct"})
    public String back(HttpSession session, Model model, HttpServletResponse response) {
        applyNoCacheHeaders(response);
        User usuarioSesion = (User) session.getAttribute("usuarioLogueado");
        if (usuarioSesion == null) {
            return "redirect:/login";
        }

        List<Orders> orders = this.orderServices.getOrdersForUser(usuarioSesion);
        model.addAttribute("name", usuarioSesion.getUname());
        model.addAttribute("orders", orders);
        return "BuyProduct";
    }

    // === PANEL ADMINISTRATIVO PROTEGIDO ===
    @GetMapping("/admin/services")
    public String returnBack(Model model, HttpSession session, HttpServletResponse response) {
        applyNoCacheHeaders(response);
        if (!isAdminAuthenticated(session)) {
            return "redirect:/admin";
        }

        model.addAttribute("users", this.services.getAllUser());
        model.addAttribute("admins", this.adminServices.getAll());
        model.addAttribute("products", this.productServices.getAllProducts());
        model.addAttribute("orders", this.orderServices.getOrders());
        return "Admin_Page";
    }

    @GetMapping("/addAdmin")
    public String addAdminPage(HttpSession session, HttpServletResponse response) {
        applyNoCacheHeaders(response);
        if (!isAdminAuthenticated(session)) return "redirect:/admin";
        return "Add_Admin";
    }

    @PostMapping("/addingAdmin")
    public String addAdmin(@ModelAttribute Admin admin, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdminAuthenticated(session)) return "redirect:/admin";
        try {
            if (adminServices.getAdminByEmail(admin.getAdminEmail()) != null) {
                redirectAttributes.addFlashAttribute("error", "Ya existe un administrador con ese correo.");
                return "redirect:/admin/services";
            }
            this.adminServices.addAdmin(admin);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar administrador.");
        }
        return "redirect:/admin/services";
    }

    @GetMapping("/updateAdmin/{adminId}")
    public String update(@PathVariable("adminId") int id, Model model, HttpSession session, HttpServletResponse response) {
        applyNoCacheHeaders(response);
        if (!isAdminAuthenticated(session)) return "redirect:/admin";
        Admin admin = this.adminServices.getAdmin(id);
        model.addAttribute("admin", admin);
        return "Update_Admin";
    }

    @GetMapping("/updatingAdmin/{id}")
    public String updateAdmin(@ModelAttribute Admin admin, @PathVariable("id") int id, HttpSession session) {
        if (!isAdminAuthenticated(session)) return "redirect:/admin";
        this.adminServices.update(admin, id);
        return "redirect:/admin/services";
    }

    @GetMapping("/deleteAdmin/{id}")
    public String deleteAdmin(@PathVariable("id") int id, HttpSession session) {
        if (!isAdminAuthenticated(session)) return "redirect:/admin";
        this.adminServices.delete(id);
        return "redirect:/admin/services";
    }

    @GetMapping("/addProduct")
    public String addProduct(HttpSession session, HttpServletResponse response) {
        applyNoCacheHeaders(response);
        if (!isAdminAuthenticated(session)) return "redirect:/admin";
        return "Add_Product";
    }

    @GetMapping("/updateProduct/{productId}")
    public String updateProduct(@PathVariable("productId") int id, Model model, HttpSession session, HttpServletResponse response) {
        applyNoCacheHeaders(response);
        if (!isAdminAuthenticated(session)) return "redirect:/admin";
        Product product = this.productServices.getProduct(id);
        model.addAttribute("product", product);
        return "Update_Product";
    }

    @GetMapping("/addUser")
    public String addUser(HttpSession session, HttpServletResponse response) {
        applyNoCacheHeaders(response);
        if (!isAdminAuthenticated(session)) return "redirect:/admin";
        return "Add_User";
    }

    @GetMapping("/updateUser/{userId}")
    public String updateUserPage(@PathVariable("userId") int id, Model model, HttpSession session, HttpServletResponse response) {
        applyNoCacheHeaders(response);
        if (!isAdminAuthenticated(session)) return "redirect:/admin";
        User user = this.services.getUser(id);
        model.addAttribute("user", user);
        return "Update_User";
    }

    @GetMapping("/admin/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Has cerrado sesión correctamente.");
        return "redirect:/admin";
    }

    @GetMapping("/admin/searchUsers")
    public String searchUsers(@RequestParam("keyword") String keyword, Model model, HttpSession session, HttpServletResponse response) {
        applyNoCacheHeaders(response);
        if (!isAdminAuthenticated(session)) return "redirect:/admin";

        List<User> results = services.searchUsers(keyword);
        if (results.isEmpty()) {
            model.addAttribute("message", "No se encontraron usuarios");
        }

        model.addAttribute("users", results);
        model.addAttribute("keyword", keyword);
        model.addAttribute("admins", adminServices.getAll());
        model.addAttribute("products", productServices.getAllProducts());
        model.addAttribute("orders", orderServices.getOrders());

        return "Admin_Page";
    }

    @GetMapping("/toggleOrderStatus/{id}")
    public String toggleOrderStatus(@PathVariable("id") int id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdminAuthenticated(session)) return "redirect:/admin";
        orderServices.toggleOrderStatus(id);
        redirectAttributes.addFlashAttribute("success", "Estado de la orden actualizado correctamente.");
        return "redirect:/admin/services";
    }

    @GetMapping("/deleteOrder/{id}")
    public String deleteOrder(@PathVariable("id") int id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdminAuthenticated(session)) return "redirect:/admin";
        orderServices.deleteOrder(id);
        redirectAttributes.addFlashAttribute("success", "Orden eliminada correctamente.");
        return "redirect:/admin/services";
    }
}
