package com.ues.edu.controllers;

import com.ues.edu.entities.Tarjeta;
import com.ues.edu.entities.TicketCompra;
import com.ues.edu.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Carlos Alex
 */
@Controller
//@RequestMapping("/pago")
public class PagoController {

    @Autowired
    private TicketService ticketService;

    @GetMapping("/pago")
    public String mostrarFormulario(Model model) {
        model.addAttribute("tarjeta", new Tarjeta());
        return "pago-form";
    }

    @PostMapping("/procesarPago")
    public String procesarPago(@ModelAttribute Tarjeta tarjeta, Model model) {
        // Aquí podrías validar datos o simular el cobro
        TicketCompra ticket = ticketService.generarTicket(tarjeta.getNombreTitular(), tarjeta.getMonto());
        model.addAttribute("ticket", ticket);
        return "ticket";
    }
//        @GetMapping
//    public String mostrarFormulario(Model model) {
//        model.addAttribute("pago", new Pago());
//        return "pago";
//    }
//
//    @PostMapping
//    public String procesarPago(@Valid @ModelAttribute("pago") Pago pago,
//                               BindingResult result, Model model) {
//        if (result.hasErrors()) {
//            return "pago";
//        }
//
//        // Aquí podrías integrar un servicio real de pago (Stripe, PayPal, etc.)
//        model.addAttribute("mensaje", "Pago procesado exitosamente por $" + pago.getMonto());
//        return "pago";
//    }

}
