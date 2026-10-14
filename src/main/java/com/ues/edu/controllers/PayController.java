package com.ues.edu.controllers;

import com.ues.edu.entities.CreditCardForm;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author Carlos Alex
 */
@Controller
public class PayController {

    @GetMapping("/payment")
    public String showPaymentForm(Model model) {
        model.addAttribute("creditCardForm", new CreditCardForm());
        return "creditCardForm";
    }

    @PostMapping("/processPayment")
    public String processPayment(@ModelAttribute("creditCardForm") @Valid CreditCardForm creditCardForm,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "creditCardForm"; // Return to the form if there are validation errors
        }
        // Process the payment with the valid creditCardForm data
        // For example, integrate with a payment gateway here
        model.addAttribute("message", "Payment processed successfully!");
        return "success"; // Redirect to a success page
    }

}
