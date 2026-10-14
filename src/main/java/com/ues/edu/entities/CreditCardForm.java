package com.ues.edu.entities;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 *
 * @author Carlos Alex
 */
public class CreditCardForm {

    @NotBlank(message = "Numero de Tarjeta Incorrecto")
    @Pattern(regexp = "\\d{16}", message = "Minimo 16 digitos")
    private String cardNumber;

    @NotBlank(message = "Cardholder name is required")
    private String cardholderName;

    @NotBlank(message = "Expiration month is required")
    @Pattern(regexp = "^(0[1-9]|1[0-2])$", message = "Invalid expiration month (MM)")
    private String expMonth;

    @NotBlank(message = "Expiration year is required")
    @Pattern(regexp = "^(20\\d{2})$", message = "Invalid expiration year (YYYY)")
    private String expYear;

    @NotBlank(message = "CVV es obligatorio")
    @Size(min = 3, max = 4, message = "CVV son 3 digitos")
    private String cvv;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(String expMonth) {
        this.expMonth = expMonth;
    }

    public String getExpYear() {
        return expYear;
    }

    public void setExpYear(String expYear) {
        this.expYear = expYear;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

}
