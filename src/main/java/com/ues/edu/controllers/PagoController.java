package com.ues.edu.controllers;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ues.edu.entities.Orders;
import com.ues.edu.entities.TicketCompra;
import com.ues.edu.entities.User;
import com.ues.edu.services.OrderServices;
import com.ues.edu.services.PaymentService;
import com.ues.edu.services.PaymentService.ResultadoBanco;
import com.ues.edu.services.TicketService;

@Controller
@RequestMapping("/pago")
public class PagoController {

    @Autowired
    private OrderServices orderServices;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private TicketService ticketService;

    // 1. Pantalla de pago para una orden específica
    @GetMapping("/{orderId}")
    public String mostrarPagoOrden(@PathVariable("orderId") int orderId,
                                   HttpSession session,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        User cliente = (User) session.getAttribute("usuarioLogueado");
        Object staff = session.getAttribute("adminLogueado");

        if (cliente == null && staff == null) {
            return "redirect:/login";
        }

        Orders order = orderServices.getOrderById(orderId);
        if (order == null) {
            redirectAttributes.addFlashAttribute("message", "La orden solicitada no existe.");
            return cliente != null ? "redirect:/user/home" : "redirect:/admin/services";
        }

        // Si ya está pagada, ir directo al ticket
        if ("PAGADO".equalsIgnoreCase(order.getEstadoPago())) {
            return "redirect:/pago/ticket/" + orderId;
        }

        model.addAttribute("order", order);
        model.addAttribute("esStaff", staff != null); // True si es Cajero/Admin (habilita Efectivo)
        return "Pago";
    }

    // 2. Procesar pago en EFECTIVO (Cajero / Admin)
    @PostMapping("/efectivo/{orderId}")
    public String procesarPagoEfectivo(@PathVariable("orderId") int orderId,
                                       @RequestParam("montoRecibido") double montoRecibido,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLogueado") == null && session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }

        Orders order = orderServices.getOrderById(orderId);
        if (order == null) return "redirect:/user/home";

        if (montoRecibido < order.getTotalAmmout()) {
            redirectAttributes.addFlashAttribute("error",
                    String.format("El efectivo recibido ($%.2f) es insuficiente para cubrir el total ($%.2f).",
                            montoRecibido, order.getTotalAmmout()));
            return "redirect:/pago/" + orderId;
        }

        double cambio = Math.round((montoRecibido - order.getTotalAmmout()) * 100.0) / 100.0;
        TicketCompra ticket = ticketService.generarTicket(order.getUser().getUname(), order.getTotalAmmout());

        order.setMetodoPago("EFECTIVO");
        order.setEstadoPago("PAGADO");
        order.setMontoRecibido(montoRecibido);
        order.setCambio(cambio);
        order.setCodigoTicket("TKT-" + ticket.getCodigo());
        orderServices.saveOrder(order);

        return "redirect:/pago/ticket/" + orderId;
    }

    // 3. Procesar pago con TARJETA (Cliente / Cajero)
    @PostMapping("/tarjeta/{orderId}")
    public String procesarPagoTarjeta(@PathVariable("orderId") int orderId,
                                      @RequestParam("cardNumber") String cardNumber,
                                      @RequestParam("cardHolder") String cardHolder,
                                      @RequestParam("expiryDate") String expiryDate,
                                      @RequestParam("cvv") String cvv,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminLogueado") == null && session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }

        Orders order = orderServices.getOrderById(orderId);
        if (order == null) return "redirect:/user/home";

        ResultadoBanco resultado = paymentService.procesarConApiBanco(
                cardNumber, cardHolder, expiryDate, cvv, order.getTotalAmmout());

        if (!resultado.isAprobado()) {
            redirectAttributes.addFlashAttribute("error", "❌ " + resultado.getMensaje());
            return "redirect:/pago/" + orderId;
        }

        order.setMetodoPago("TARJETA " + resultado.getFranquicia());
        order.setEstadoPago("PAGADO");
        order.setMontoRecibido(order.getTotalAmmout());
        order.setCambio(0.0);
        order.setCodigoTicket(resultado.getCodigoAutorizacion());
        orderServices.saveOrder(order);

        return "redirect:/pago/ticket/" + orderId;
    }

    // 4. Visualizar e imprimir Ticket de compra
    @GetMapping("/ticket/{orderId}")
    public String verTicket(@PathVariable("orderId") int orderId,
                            HttpSession session,
                            Model model) {
        if (session.getAttribute("adminLogueado") == null && session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }

        Orders order = orderServices.getOrderById(orderId);
        if (order == null) return "redirect:/user/home";

        model.addAttribute("order", order);
        model.addAttribute("esStaff", session.getAttribute("adminLogueado") != null);
        return "ticket";
    }
}
