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
            String rol = (adminCompleto != null && adminCompleto.getRol() != null && !adminCompleto.getRol().trim().isEmpty())
                    ? adminCompleto.getRol().trim().toUpperCase()
                    : "ADMIN";

            session.setAttribute("adminLogueado", adminCompleto != null ? adminCompleto : email);
            session.setAttribute("rolOperativo", rol);
            return "redirect:/admin/services";
        } else {
            redirectAttributes.addFlashAttribute("error", "❌ Credenciales de Acceso Operativo incorrectas.");
            return "redirect:/admin";
        }
    }

    public List<Orders> getOrdersForUsername(String username) {
        return orderRepository.findByUser_UnameContainingIgnoreCase(username);
    }

    // === BÚSQUEDA DE PRODUCTO Y ORDEN PARA CLIENTE LOGUEADO ===
    @PostMapping("/product/search")
public String seachHandler(@RequestParam(value = "productName", required = false) String name,
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

        String termino = (name != null) ? name.trim() : "";
        List<Product> resultados = this.productServices.searchProductsByName(termino);

        if (resultados == null || resultados.isEmpty()) {
            model.addAttribute("message", "Lo sentimos, el producto solicitado no está disponible.");
            model.addAttribute("product", null);
            model.addAttribute("products", null);
            return "BuyProduct";
        }

        model.addAttribute("products", resultados);
        model.addAttribute("product", resultados.get(0));
        return "BuyProduct";
    }

    @PostMapping("/product/order")
    public String orderHandler(@ModelAttribute Orders order,
            HttpSession session,
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
        order.setEstadoOrden("INGRESADA");
        order.setEstadoPago("PENDIENTE");
        this.orderServices.saveOrder(order);

        return "redirect:/pago/" + order.getoId();
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

// === AUXILIAR DE JERARQUÍA RBAC ===
    private String getRolSesion(HttpSession session) {
        Object rol = session.getAttribute("rolOperativo");
        return (rol != null && !rol.toString().trim().isEmpty())
                ? rol.toString().trim().toUpperCase()
                : "ADMIN";
    }

    // === PANEL ADMINISTRATIVO PROTEGIDO ===
    @GetMapping("/admin/services")
    public String returnBack(Model model, HttpSession session, HttpServletResponse response) {
        applyNoCacheHeaders(response);
        if (!isAdminAuthenticated(session)) {
            return "redirect:/admin";
        }

        Object obj = session.getAttribute("adminLogueado");
        Object rolSesion = session.getAttribute("rolOperativo");
        if (rolSesion == null || rolSesion.toString().trim().isEmpty()) {
            String rolFallback = (obj instanceof Admin && ((Admin) obj).getRol() != null && !((Admin) obj).getRol().trim().isEmpty())
                    ? ((Admin) obj).getRol().trim().toUpperCase()
                    : "ADMIN";
            session.setAttribute("rolOperativo", rolFallback);
        }

        model.addAttribute("users", this.services.getAllUser());
        model.addAttribute("admins", this.adminServices.getAll());
        model.addAttribute("products", this.productServices.getAllProducts());
        model.addAttribute("orders", this.orderServices.getOrders());
        return "Admin_Page";
    }

    @GetMapping("/addAdmin")
    public String addAdminPage(HttpSession session, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        applyNoCacheHeaders(response);
        if (!isAdminAuthenticated(session)) {
            return "redirect:/admin";
        }
        String rolSesion = getRolSesion(session);
        if (!("ADMIN".equals(rolSesion) || "GERENTE".equals(rolSesion))) {
            redirectAttributes.addFlashAttribute("error", "⛔ No tienes permisos para registrar personal.");
            return "redirect:/admin/services";
        }
        return "Add_Admin";
    }

    @PostMapping("/addingAdmin")
    public String addAdmin(@ModelAttribute Admin admin, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/admin";
        }
        String rolSesion = getRolSesion(session);
        String rolNuevo = admin.getRol() != null ? admin.getRol().trim().toUpperCase() : "ADMIN";

        if (!("ADMIN".equals(rolSesion) || "GERENTE".equals(rolSesion))) {
            redirectAttributes.addFlashAttribute("error", "⛔ Acceso denegado.");
            return "redirect:/admin/services";
        }

        // Jerarquía: GERENTE solo puede crear CAJERO o COCINERO
        if ("GERENTE".equals(rolSesion) && !("CAJERO".equals(rolNuevo) || "COCINERO".equals(rolNuevo))) {
            redirectAttributes.addFlashAttribute("error", "⛔ Un Gerente solo puede registrar Cajeros o Cocineros.");
            return "redirect:/admin/services";
        }

        try {
            if (adminServices.getAdminByEmail(admin.getAdminEmail()) != null) {
                redirectAttributes.addFlashAttribute("error", "Ya existe un usuario operativo con ese correo.");
                return "redirect:/admin/services";
            }
            admin.setRol(rolNuevo);
            this.adminServices.addAdmin(admin);
            redirectAttributes.addFlashAttribute("success", "✅ Usuario operativo (" + rolNuevo + ") registrado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar usuario operativo.");
        }
        return "redirect:/admin/services";
    }

    @GetMapping("/updateAdmin/{adminId}")
    public String update(@PathVariable("adminId") int id, Model model, HttpSession session,
                         HttpServletResponse response, RedirectAttributes redirectAttributes) {
        applyNoCacheHeaders(response);
        if (!isAdminAuthenticated(session)) {
            return "redirect:/admin";
        }
        String rolSesion = getRolSesion(session);
        Admin admin = this.adminServices.getAdmin(id);
        if (admin == null) {
            return "redirect:/admin/services";
        }

        String rolObjetivo = (admin.getRol() != null && !admin.getRol().trim().isEmpty())
                ? admin.getRol().trim().toUpperCase() : "ADMIN";

        // Jerarquía: GERENTE solo puede editar CAJERO o COCINERO
        if (!"ADMIN".equals(rolSesion) && !("GERENTE".equals(rolSesion) && ("CAJERO".equals(rolObjetivo) || "COCINERO".equals(rolObjetivo)))) {
            redirectAttributes.addFlashAttribute("error", "⛔ No tienes permisos para editar esta cuenta.");
            return "redirect:/admin/services";
        }

        model.addAttribute("admin", admin);
        return "Update_Admin";
    }

    @PostMapping("/updatingAdmin/{id}")
    public String updateAdmin(@ModelAttribute Admin admin, @PathVariable("id") int id,
                              HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/admin";
        }
        String rolSesion = getRolSesion(session);
        Admin existente = this.adminServices.getAdmin(id);
        if (existente == null) {
            return "redirect:/admin/services";
        }

        String rolActualObjetivo = (existente.getRol() != null && !existente.getRol().trim().isEmpty())
                ? existente.getRol().trim().toUpperCase() : "ADMIN";
        String rolNuevo = (admin.getRol() != null && !admin.getRol().trim().isEmpty())
                ? admin.getRol().trim().toUpperCase() : rolActualObjetivo;

        // Validar jerarquía tanto sobre la cuenta destino como sobre el rol asignado
        if ("GERENTE".equals(rolSesion)) {
            boolean objetivoPermitido = "CAJERO".equals(rolActualObjetivo) || "COCINERO".equals(rolActualObjetivo);
            boolean nuevoRolPermitido = "CAJERO".equals(rolNuevo) || "COCINERO".equals(rolNuevo);
            if (!objetivoPermitido || !nuevoRolPermitido) {
                redirectAttributes.addFlashAttribute("error", "Un Gerente solo puede administrar Cajeros o Cocineros.");
                return "redirect:/admin/services";
            }
        } else if (!"ADMIN".equals(rolSesion)) {
            redirectAttributes.addFlashAttribute("error", "⛔ Acceso denegado.");
            return "redirect:/admin/services";
        }

        admin.setRol(rolNuevo);
        this.adminServices.update(admin, id);

        // Si el usuario editó su propia cuenta, refrescar datos en sesión
        Object logueado = session.getAttribute("adminLogueado");
        if (logueado instanceof Admin && ((Admin) logueado).getAdminId() == id) {
            Admin actualizado = this.adminServices.getAdmin(id);
            session.setAttribute("adminLogueado", actualizado);
            session.setAttribute("rolOperativo", actualizado.getRol());
        }

        redirectAttributes.addFlashAttribute("success", "✅ Datos de operador actualizados correctamente.");
        return "redirect:/admin/services";
    }

    @GetMapping("/deleteAdmin/{id}")
    public String deleteAdmin(@PathVariable("id") int id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/admin";
        }
        String rolSesion = getRolSesion(session);
        Admin objetivo = this.adminServices.getAdmin(id);
        if (objetivo == null) {
            return "redirect:/admin/services";
        }

        // Evitar que el operador elimine su propia sesión activa
        Object logueado = session.getAttribute("adminLogueado");
        if (logueado instanceof Admin && ((Admin) logueado).getAdminId() == id) {
            redirectAttributes.addFlashAttribute("error", "⚠️ No puedes eliminar tu propia cuenta mientras tienes sesión activa.");
            return "redirect:/admin/services";
        }

        String rolObjetivo = (objetivo.getRol() != null && !objetivo.getRol().trim().isEmpty())
                ? objetivo.getRol().trim().toUpperCase() : "ADMIN";

        if ("ADMIN".equals(rolSesion) || ("GERENTE".equals(rolSesion) && ("CAJERO".equals(rolObjetivo) || "COCINERO".equals(rolObjetivo)))) {
            this.adminServices.delete(id);
            redirectAttributes.addFlashAttribute("success", "🗑️ Operador eliminado del sistema.");
        } else {
            redirectAttributes.addFlashAttribute("error", "⛔ No tienes permisos para eliminar esta cuenta.");
        }
        return "redirect:/admin/services";
    }

    @GetMapping("/addProduct")
    public String addProduct(HttpSession session, HttpServletResponse response) {
        applyNoCacheHeaders(response);
        if (!isAdminAuthenticated(session)) {
            return "redirect:/admin";
        }
        return "Add_Product";
    }

    @GetMapping("/updateProduct/{productId}")
    public String updateProduct(@PathVariable("productId") int id, Model model, HttpSession session, HttpServletResponse response) {
        applyNoCacheHeaders(response);
        if (!isAdminAuthenticated(session)) {
            return "redirect:/admin";
        }
        Product product = this.productServices.getProduct(id);
        model.addAttribute("product", product);
        return "Update_Product";
    }

    @GetMapping("/addUser")
    public String addUser(HttpSession session, HttpServletResponse response) {
        applyNoCacheHeaders(response);
        if (!isAdminAuthenticated(session)) {
            return "redirect:/admin";
        }
        return "Add_User";
    }

    @GetMapping("/updateUser/{userId}")
    public String updateUserPage(@PathVariable("userId") int id, Model model, HttpSession session, HttpServletResponse response) {
        applyNoCacheHeaders(response);
        if (!isAdminAuthenticated(session)) {
            return "redirect:/admin";
        }
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
        if (!isAdminAuthenticated(session)) {
            return "redirect:/admin";
        }

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
        if (!isAdminAuthenticated(session)) {
            return "redirect:/admin";
        }
        orderServices.toggleOrderStatus(id);
        redirectAttributes.addFlashAttribute("success", "Estado de la orden actualizado correctamente.");
        return "redirect:/admin/services";
    }

    @GetMapping("/deleteOrder/{id}")
    public String deleteOrder(@PathVariable("id") int id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/admin";
        }
        orderServices.deleteOrder(id);
        redirectAttributes.addFlashAttribute("success", "Orden eliminada correctamente.");
        return "redirect:/admin/services";
    }
    
    @GetMapping("/addOrder")
    public String addOrderPage(Model model, HttpSession session, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        applyNoCacheHeaders(response);
        if (!isAdminAuthenticated(session)) {
            return "redirect:/admin";
        }
        String rolSesion = getRolSesion(session);
        if (!("ADMIN".equals(rolSesion) || "CAJERO".equals(rolSesion))) {
            redirectAttributes.addFlashAttribute("error", "⛔ Solo Cajero o Administrador pueden ingresar órdenes en caja.");
            return "redirect:/admin/services";
        }
        model.addAttribute("users", this.services.getAllUser());
        model.addAttribute("products", this.productServices.getAllProducts());
        return "Add_Order";
    }

    @PostMapping("/admin/crearOrdenCaja")
public String crearOrdenDesdeCaja(@RequestParam(value = "userId", defaultValue = "0") int userId,
            @RequestParam(value = "nombreCliente", required = false) String nombreCliente,
            @RequestParam("productId") int productId,
            @RequestParam("cantidad") int cantidad,
            @RequestParam(value = "estadoOrden", defaultValue = "INGRESADA") String estadoOrden,
            @RequestParam(value = "accion", defaultValue = "COBRAR") String accion,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        System.out.println("==========================================");
        System.out.println("[SIPAGO POS] POST /admin/crearOrdenCaja recibido:");
        System.out.println(" -> userId: " + userId);
        System.out.println(" -> nombreCliente: " + nombreCliente);
        System.out.println(" -> productId: " + productId);
        System.out.println(" -> cantidad: " + cantidad);
        System.out.println(" -> estadoOrden: " + estadoOrden);
        System.out.println(" -> accion: " + accion);

        if (!isAdminAuthenticated(session)) {
            System.out.println("[SIPAGO POS] Sesión no autenticada -> redirigiendo a /admin");
            return "redirect:/admin";
        }

        try {
            Product prod = productServices.getProduct(productId);
            System.out.println("[SIPAGO POS] Producto encontrado: " + (prod != null ? prod.getPname() + " ($" + prod.getPprice() + ")" : "NULL"));

            if (prod == null || cantidad <= 0) {
                System.out.println("[SIPAGO POS] Producto inválido o cantidad <= 0");
                redirectAttributes.addFlashAttribute("error", "⚠️ Selecciona un producto y cantidad válidos.");
                return "redirect:/addOrder";
            }

            User cliente = null;
            if (userId > 0) {
                cliente = services.getUser(userId);
                System.out.println("[SIPAGO POS] Cliente registrado seleccionado: " + (cliente != null ? cliente.getUname() : "NULL"));
            } else {
                // Busca o crea un cliente técnico 'Mostrador' para cumplir con user_u_id en MySQL
                cliente = services.getUserByEmail("mostrador@sipago.local");
                if (cliente == null) {
                    System.out.println("[SIPAGO POS] Creando registro técnico 'Cliente Mostrador' en BD...");
                    User mostrador = new User();
                    mostrador.setUname("Cliente Mostrador");
                    mostrador.setUemail("mostrador@sipago.local");
                    mostrador.setUpassword("MOSTRADOR_INTERNO");
                    mostrador.setUnumber("70000000");
                    mostrador.setActivo(true);
                    services.addUser(mostrador);
                    cliente = services.getUserByEmail("mostrador@sipago.local");
                }
                System.out.println("[SIPAGO POS] Usando cliente técnico ID: " + (cliente != null ? cliente.getU_id() : "NULL"));
            }

            String etiquetaCliente = (nombreCliente != null && !nombreCliente.trim().isEmpty())
                    ? nombreCliente.trim()
                    : (cliente != null ? cliente.getUname() : "Mostrador");

            Orders orden = new Orders();
            orden.setUser(cliente);
            orden.setNombreCliente(etiquetaCliente);
            orden.setoName(prod.getPname());
            orden.setoPrice(prod.getPprice());
            orden.setoQuantity(cantidad);
            orden.setTotalAmmout(Math.round((prod.getPprice() * cantidad) * 100.0) / 100.0);
            orden.setOrderDate(new Date());
            orden.setActivo(true);
            orden.setEstadoOrden(estadoOrden);
            orden.setEstadoPago("PENDIENTE");

            orderServices.saveOrder(orden);
            System.out.println("[SIPAGO POS] ✅ Orden guardada en MySQL con oId = " + orden.getoId());

            if ("COCINA".equalsIgnoreCase(accion)) {
                System.out.println("[SIPAGO POS] Redirigiendo a /admin/services (Modo Cocina)");
                redirectAttributes.addFlashAttribute("success", "✅ Orden #" + orden.getoId() + " (" + etiquetaCliente + ") enviada a cocina.");
                return "redirect:/admin/services";
            }

            System.out.println("[SIPAGO POS] Redirigiendo a /pago/" + orden.getoId());
            return "redirect:/pago/" + orden.getoId();

        } catch (Exception e) {
            System.out.println("[SIPAGO POS] ❌ EXCEPCIÓN al crear orden: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "❌ Error al registrar la orden: " + e.getMessage());
            return "redirect:/addOrder";
        }
    }

}
