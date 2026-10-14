package com.ues.edu.services;

import com.ues.edu.entities.TicketCompra;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 *
 * @author Carlos Alex
 */
@Service
public class TicketService {

    public TicketCompra generarTicket(String titular, double monto) {
        String codigo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new TicketCompra(codigo, titular, monto, LocalDateTime.now());
    }

//    public byte[] generarTicketPDF(TicketCompra ticket) throws Exception {
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        Document document = new Document();
//        PdfWriter.getInstance(document, out);
//        document.open();
//        document.add(new Paragraph("Ticket de Compra"));
//        document.add(new Paragraph("Código: " + ticket.getCodigo()));
//        document.add(new Paragraph("Titular: " + ticket.getTitular()));
//        document.add(new Paragraph("Monto: $" + ticket.getMonto()));
//        document.add(new Paragraph("Fecha: " + ticket.getFecha()));
//        document.close();
//        return out.toByteArray();
//    }

}
