package com.ues.edu.entities;

/**
 *
 * @author Carlos Alex
 */
public class CreditCard {

    private String cardNumber;
    private String expiryDate;
    private String cvv;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CreditCard{");
        sb.append("cardNumber=").append(cardNumber);
        sb.append(", expiryDate=").append(expiryDate);
        sb.append(", cvv=").append(cvv);
        sb.append('}');
        return sb.toString();
    }

}
