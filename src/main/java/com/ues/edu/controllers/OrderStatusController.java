package com.ues.edu.controllers;

import com.ues.edu.entities.CreditCardForm;
import com.ues.edu.entities.Order;
import com.ues.edu.entities.OrderStatus;
import com.ues.edu.services.OrderStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Carlos Alex
 */
@Controller
public class OrderStatusController {

    @Autowired
    OrderStatusService orderStatusService;

    @GetMapping("/order/{id}")
    public String viewOrderStatus(@PathVariable("id") int id, Model model) {
        Order order = orderStatusService.getOrderDetails(id);
        if (order == null) {
            // Handle order not found, e.g., redirect to an error page
            return "errorPage";
        }
        model.addAttribute("order", order);
        return "orderStatus"; // JSP or Thymeleaf template for displaying order status
    }
    @GetMapping("/OrderStatus")
        public String viewOrderStatus(Model model) {
        model.addAttribute("orderStatus", new Order());
        return "orderStatus";
    }

//    @PostMapping("/order/{id}/updateStatus")
//    public String updateOrderStatus(@PathVariable("id") int id,
//            @RequestParam("newStatus") OrderStatus newStatus) {
//        orderStatusService.updateOrderStatus(id, newStatus);
//        return "redirect:/order/" + id; // Redirect back to the order status page
//    }

}
