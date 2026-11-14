package com.ues.edu.services;

import java.time.YearMonth;
import java.util.UUID;
import org.springframework.stereotype.Service;
import com.ues.edu.entities.CreditCard;

@Service
public class PaymentService {

    public static class ResultadoBanco {
        private final boolean aprobado;
        private final String mensaje;
        private final String codigoAutorizacion;
        private final String franquicia;

        public ResultadoBanco(boolean aprobado, String mensaje, String codigoAutorizacion, String franquicia) {
            this.aprobado = aprobado;
            this.mensaje = mensaje;
            this.codigoAutorizacion = codigoAutorizacion;
            this.franquicia = franquicia;
        }

        public boolean isAprobado() { return aprobado; }
        public String getMensaje() { return mensaje; }
        public String getCodigoAutorizacion() { return codigoAutorizacion; }
        public String getFranquicia() { return franquicia; }
    }

    public boolean validateCreditCard(CreditCard creditCard) {
        if (creditCard == null || creditCard.getCardNumber() == null
                || creditCard.getExpiryDate() == null || creditCard.getCvv() == null) {
            return false;
        }
        String cleanNumber = creditCard.getCardNumber().replaceAll("[\\s-]+", "");
        if (!isValidExpiryDate(creditCard.getExpiryDate().trim())) {
            return false;
        }
        if (!isValidCVV(cleanNumber, creditCard.getCvv().trim())) {
            return false;
        }
        return isValidCardNumber(cleanNumber);
    }

    /**
     * Valida datos de tarjeta y simula consulta a API del Banco Emisor
     */
    public ResultadoBanco procesarConApiBanco(String rawCardNumber, String titular, String expiryDate, String cvv, double monto) {
        if (rawCardNumber == null || titular == null || expiryDate == null || cvv == null) {
            return new ResultadoBanco(false, "Todos los campos de la tarjeta son obligatorios.", null, null);
        }

        String cardNumber = rawCardNumber.replaceAll("[\\s-]+", "");
        String franquicia = detectarFranquicia(cardNumber);

        if (titular.trim().length() < 3) {
            return new ResultadoBanco(false, "Ingrese el nombre completo del titular de la tarjeta.", null, franquicia);
        }
        if (!isValidCardNumber(cardNumber)) {
            return new ResultadoBanco(false, "Número de tarjeta inválido (falló verificación Luhn o longitud).", null, franquicia);
        }
        if (!isValidExpiryDate(expiryDate.trim())) {
            return new ResultadoBanco(false, "La tarjeta está vencida o el formato MM/AA es inválido.", null, franquicia);
        }
        if (!isValidCVV(cardNumber, cvv.trim())) {
            return new ResultadoBanco(false, "El código CVV no corresponde al tipo de tarjeta.", null, franquicia);
        }

        // Simulación de latencia de red hacia API Bancaria
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simulación de respuestas del banco según terminación de tarjeta:
        if (cardNumber.endsWith("0000")) {
            return new ResultadoBanco(false, "API Banco: Fondos insuficientes en la cuenta.", null, franquicia);
        }
        if (cardNumber.endsWith("9999")) {
            return new ResultadoBanco(false, "API Banco: Tarjeta bloqueada o rechazada por el emisor.", null, franquicia);
        }

        String authCode = "AUTH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new ResultadoBanco(true, "Transacción aprobada por " + franquicia, authCode, franquicia);
    }

    private static boolean isValidExpiryDate(String expiryDate) {
        if (!expiryDate.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            return false;
        }
        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = 2000 + Integer.parseInt(parts[1]);
        YearMonth exp = YearMonth.of(year, month);
        return !exp.isBefore(YearMonth.now());
    }

    private static boolean isValidCVV(String cardNumber, String cvv) {
        if (!cvv.matches("\\d+")) return false;
        if (cardNumber.startsWith("34") || cardNumber.startsWith("37")) {
            return cvv.length() == 4;
        }
        return cvv.length() == 3;
    }

    private static boolean isValidCardNumber(String cardNumber) {
        if (!cardNumber.matches("\\d+")) return false;
        if (cardNumber.startsWith("34") || cardNumber.startsWith("37")) {
            if (cardNumber.length() < 15 || cardNumber.length() > 19) return false;
        } else {
            if (cardNumber.length() < 16 || cardNumber.length() > 19) return false;
        }
        return isLuhnValid(cardNumber);
    }

    private static boolean isLuhnValid(String cardNumber) {
        int[] digits = new int[cardNumber.length()];
        for (int i = 0; i < cardNumber.length(); i++) {
            digits[i] = Character.getNumericValue(cardNumber.charAt(i));
        }
        for (int i = digits.length - 2; i >= 0; i -= 2) {
            int doubleDigit = digits[i] * 2;
            if (doubleDigit > 9) doubleDigit -= 9;
            digits[i] = doubleDigit;
        }
        int sum = 0;
        for (int digit : digits) sum += digit;
        return sum % 10 == 0;
    }

    private static String detectarFranquicia(String cardNumber) {
        if (cardNumber.startsWith("4")) return "VISA";
        if (cardNumber.matches("^5[1-5].*") || cardNumber.matches("^2[2-7].*")) return "MASTERCARD";
        if (cardNumber.startsWith("34") || cardNumber.startsWith("37")) return "AMEX";
        return "TARJETA";
    }
}
