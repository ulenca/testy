package pl.coderstrust.services;

import org.junit.jupiter.api.Test;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;

class InvoicePdfServiceTest {

    @Test
    public void testForDoSomething() throws  ServiceOperationException {
        InvoicePdfService invoicePdfService = new InvoicePdfService();
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        invoicePdfService.createPdf(invoice);
    }

}