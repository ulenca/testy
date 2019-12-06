package pl.coderstrust.services;

import pl.coderstrust.model.Invoice;

public interface InvoiceEmailService {
    void sendEmailWithInvoice(Invoice invoice);
}
