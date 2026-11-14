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

import com.ues.edu.entities.Orders;
import com.ues.edu.entities.User;
import com.ues.edu.services.OrderServices;
import com.ues.edu.services.UserServices;

/**
 * Controlador unificado para manejar tanto el CRUD de usuarios (administrador)
 * como el registro y login de clientes.
 *
 * @author Cristian Rodolfo Aguirre Gonzalez
 */
@Controller
public class UserController {

    @Autowired
    private UserServices services;

    @Autowired
    private OrderServices orderServices;

    private boolean isAdminAuthenticated(HttpSession session) {
        return session != null && session.getAttribute("adminLogueado") != null;
    }

    // -----------------------------
    // SECCIÓN ADMIN (CRUD USUARIOS PROTEGIDO)
    // -----------------------------

    @PostMapping("/addingUser")
    public String addUser(@ModelAttribute User user, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdminAuthenticated(session)) return "redirect:/admin";
        try {
            if (services.getUserByEmail(user.getUemail()) != null) {
                redirectAttributes.addFlashAttribute("error", "El correo ingresado ya está registrado.");
                return "redirect:/admin/services";
            }
            this.services.addUser(user);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear usuario.");
        }
        return "redirect:/admin/services";
    }

    @GetMapping("/updatingUser/{id}")
    public String updateUser(@ModelAttribute User user, @PathVariable("id") int id, HttpSession session) {
        if (!isAdminAuthenticated(session)) return "redirect:/admin";
        this.services.updateUser(user, id);
        return "redirect:/admin/services";
    }

    @GetMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable("id") int id, HttpSession session) {
        if (!isAdminAuthenticated(session)) return "redirect:/admin";
        this.services.deleteUser(id);
        return "redirect:/admin/services";
    }

    @GetMapping("/toggleUserStatus/{id}")
    public String toggleUserStatus(@PathVariable("id") int id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdminAuthenticated(session)) return "redirect:/admin";
        services.toggleUserStatus(id);
        redirectAttributes.addFlashAttribute("success", "Estado del usuario actualizado correctamente.");
        return "redirect:/admin/services";
    }

    // -----------------------------
    // SECCIÓN CLIENTE (REGISTRO Y LOGIN)
    // -----------------------------

    @GetMapping("/clientes/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("user", new User());
        return "clientes";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute("user") User user,
                                   RedirectAttributes redirectAttributes) {
        try {
            User existente = services.getUserByEmail(user.getUemail());
            if (existente != null) {
                redirectAttributes.addFlashAttribute("error",
                        "El correo ingresado ya está registrado. Por favor usa otro.");
                return "redirect:/clientes/registro";
            }

            services.addClient(user);
            redirectAttributes.addFlashAttribute("success",
                    "Cuenta creada exitosamente. ¡Ya puedes iniciar sesión!");
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Ocurrió un error inesperado al registrar el usuario. Intenta nuevamente.");
            return "redirect:/clientes/registro";
        }
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "Login";
    }

    @PostMapping("/login")
    public String validarLogin(@RequestParam("email") String email,
                               @RequestParam("password") String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        // Verificar primero si el usuario existe y está inactivo para dar retroalimentación clara
        User posibleInactivo = services.getUserByEmail(email);
        if (posibleInactivo != null && !posibleInactivo.isActivo()
                && posibleInactivo.getUpassword() != null
                && posibleInactivo.getUpassword().equals(password)) {
            redirectAttributes.addFlashAttribute("error",
                    "⚠️ Tu cuenta está inactiva. Por favor contacta con el administrador.");
            return "redirect:/login";
        }

        User user = services.login(email, password);
        if (user != null) {
            session.setAttribute("usuarioLogueado", user);
            return "redirect:/user/home";
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "❌ Correo o contraseña incorrectos. Intenta nuevamente.");
            return "redirect:/login";
        }
    }

    @GetMapping("/user/home")
    public String homeUsuario(HttpSession session, Model model, HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        User usuarioSesion = (User) session.getAttribute("usuarioLogueado");
        if (usuarioSesion == null) {
            return "redirect:/login";
        }

        // Recargar usuario actualizado desde BD
        User usuarioActual = services.getUser(usuarioSesion.getU_id());
        if (usuarioActual == null || !usuarioActual.isActivo()) {
            session.invalidate();
            return "redirect:/login";
        }

        List<Orders> orders = orderServices.getOrdersForUser(usuarioActual);
        model.addAttribute("usuario", usuarioActual);
        model.addAttribute("name", usuarioActual.getUname());
        model.addAttribute("orders", orders);
        return "BuyProduct";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Has cerrado sesión correctamente.");
        return "redirect:/login";
    }
}
