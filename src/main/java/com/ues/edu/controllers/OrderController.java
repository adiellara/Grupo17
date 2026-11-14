package com.ues.edu.controllers;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ues.edu.entities.Orders;
import com.ues.edu.entities.Product;
import com.ues.edu.entities.User;
import com.ues.edu.repositories.OrderRepository;
import com.ues.edu.services.OrderServices;
import com.ues.edu.services.ProductServices;
import com.ues.edu.services.UserServices;

/**
 * Módulo Orden
 *
 * @author 
 */
@Controller
public class OrderController {

    @Autowired
    private OrderServices orderService;

    @Autowired
    private ProductServices productService;

    @Autowired
    private UserServices userServices;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Endpoint AJAX para buscar órdenes por nombre de usuario (Protegido solo para Admin)
     */
    @GetMapping("/searchOrdersByUser")
    @ResponseBody
    public ResponseEntity<List<Orders>> searchOrdersByUser(@RequestParam String username, HttpSession session) {
        if (session.getAttribute("adminLogueado") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        }
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        List<Orders> result = orderRepository.findByUser_UnameContainingIgnoreCase(username.trim());
        return ResponseEntity.ok(result);
    }

    /**
     * Crear una nueva orden desde el portal del cliente
     */
    @PostMapping("/order/create/{productId}")
    public String createOrder(@PathVariable("productId") int productId,
                              @RequestParam("quantity") int quantity,
                              HttpSession session,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        try {
            // 1. Validar sesión activa del cliente
            User usuarioSesion = (User) session.getAttribute("usuarioLogueado");
            if (usuarioSesion == null) {
                redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para realizar una compra.");
                return "redirect:/login";
            }

            // 2. Verificar en BD que el usuario siga activo
            User usuarioActual = userServices.getUser(usuarioSesion.getU_id());
            if (usuarioActual == null || !usuarioActual.isActivo()) {
                session.invalidate();
                redirectAttributes.addFlashAttribute("error", "Tu cuenta se encuentra inactiva. Contacta al administrador.");
                return "redirect:/login";
            }

            // 3. Validar cantidad positiva
            if (quantity <= 0) {
                redirectAttributes.addFlashAttribute("message", "La cantidad debe ser al menos 1 unidad.");
                return "redirect:/buyProduct";
            }

            // 4. Obtener y validar el producto
            Product producto = productService.getProduct(productId);
            if (producto == null) {
                redirectAttributes.addFlashAttribute("message", "El producto seleccionado no existe o fue eliminado.");
                return "redirect:/buyProduct";
            }

            // 5. Calcular total redondeado a 2 decimales y construir la orden
            double totalRedondeado = Math.round((producto.getPprice() * quantity) * 100.0) / 100.0;

            Orders order = new Orders();
            order.setUser(usuarioActual);
            order.setoName(producto.getPname());
            order.setoPrice(producto.getPprice());
            order.setoQuantity(quantity);
            order.setOrderDate(new Date());
            order.setTotalAmmout(totalRedondeado);
            order.setActivo(true);

            // 6. Guardar en MySQL
            orderService.saveOrder(order);

            // 7. Pasar datos a la vista de confirmación Order_success
            model.addAttribute("amount", order.getTotalAmmout());
            model.addAttribute("productName", order.getoName());
            model.addAttribute("quantity", order.getoQuantity());
            model.addAttribute("orderId", order.getoId());
            model.addAttribute("success", "Tu orden fue registrada exitosamente.");

            return "Order_success";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message",
                    "Ocurrió un error al registrar la orden: " + e.getMessage());
            return "redirect:/buyProduct";
        }
    }
}