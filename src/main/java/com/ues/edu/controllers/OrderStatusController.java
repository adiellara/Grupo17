package com.ues.edu.controllers;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ues.edu.entities.Orders;
import com.ues.edu.services.OrderServices;

@Controller
public class OrderStatusController {

    private static final List<String> ESTADOS_VALIDOS = Arrays.asList("INGRESADA", "PREPARANDO", "ENTREGADA");

    @Autowired
    private OrderServices orderServices;

    // Vista de rastreo de una orden (Cliente o Personal)
    @GetMapping("/order/status/{id}")
    public String viewOrderStatus(@PathVariable("id") int id, HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null && session.getAttribute("adminLogueado") == null) {
            return "redirect:/login";
        }
        Orders order = orderServices.getOrderById(id);
        if (order == null) {
            return "redirect:/user/home";
        }
        model.addAttribute("order", order);
        model.addAttribute("esStaff", session.getAttribute("adminLogueado") != null);
        return "orderStatus";
    }

    // Cambio de estado en Cocina: INGRESADA -> PREPARANDO -> ENTREGADA
    @PostMapping("/order/{id}/updateStatus")
    public String updateOrderStatus(@PathVariable("id") int id,
                                    @RequestParam("nuevoEstado") String nuevoEstado,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLogueado") == null) {
            return "redirect:/admin";
        }

        Orders order = orderServices.getOrderById(id);
        if (order != null && ESTADOS_VALIDOS.contains(nuevoEstado)) {
            order.setEstadoOrden(nuevoEstado);
            orderServices.saveOrder(order);
            redirectAttributes.addFlashAttribute("success",
                    "Orden #" + id + " actualizada a estado: " + nuevoEstado);
        }
        return "redirect:/admin/services";
    }
}