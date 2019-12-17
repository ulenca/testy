package pl.coderstrust.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;

class InvoicePdfServiceTest {

    @Test
    public void shouldReturnByteArray() throws ServiceOperationException {
        InvoicePdfService invoicePdfService = new InvoicePdfService();
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        byte[] result = invoicePdfService.createPdf(invoice);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    public void shouldThrowExceptionForNullAsInvoice() {
        InvoicePdfService invoicePdfService = new InvoicePdfService();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> invoicePdfService.createPdf(null));
        assertEquals("Invoice cannot be null", exception.getMessage());
    }
}
