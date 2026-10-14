package com.ues.edu.controllers;

import com.ues.edu.entities.CreditCard;
import com.ues.edu.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Carlos Alex
 */
@Controller
@RequestMapping("/pay/validate")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<String> validateCreditCard(@RequestBody CreditCard creditCard) {
        boolean isValid = paymentService.validateCreditCard(creditCard);

        if (isValid) {
            return ResponseEntity.ok("Success!");
        } else {
            return ResponseEntity.badRequest().body("Invalid Credit Card information.");
        }
    }

}
