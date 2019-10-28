package pl.coderstrust.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.coderstrust.database.Database;
import pl.coderstrust.database.DatabaseOperationException;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.Vat;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private Database database;

    @InjectMocks
    private InvoiceService invoiceService;

    @Test
    void shouldReturnAllInvoicesTest() throws ServiceOperationException, DatabaseOperationException {
        List<Invoice> invoices = List.of(createSampleInvoice(), createSampleInvoice());
        when(database.getAll()).thenReturn(invoices);
        assertEquals(invoices, invoiceService.getAllInvoices());
    }

    @Test
    void getAllInvoicesThrowsErrorTest() throws DatabaseOperationException {
        when(database.getAll()).thenThrow(new DatabaseOperationException());
        assertThrows(ServiceOperationException.class, () -> invoiceService.getAllInvoices());
    }

    @Test
    void getByIdTest() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = createSampleInvoice();
        Optional<Invoice> optionalInvoice = Optional.of(invoice);
        when(database.getById(invoice.getId())).thenReturn(optionalInvoice);
        assertEquals(optionalInvoice, invoiceService.getById(invoice.getId()));
    }

    @Test
    void getByIdThrowsErrorsTest() throws DatabaseOperationException {
        Long id = 1L;
        when(database.getById(id)).thenThrow(new DatabaseOperationException());
        assertThrows(IllegalArgumentException.class, () -> invoiceService.getById(null));
        assertThrows(ServiceOperationException.class, () -> invoiceService.getById(id));
    }

    @Test
    void getByNumberTest() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = createSampleInvoice();
        Optional<Invoice> optionalInvoice = Optional.of(invoice);
        when(database.getByNumber(invoice.getNumber())).thenReturn(optionalInvoice);
        assertEquals(optionalInvoice, invoiceService.getByNumber(invoice.getNumber()));
    }

    @Test
    void getByNumberThrowsErrorTest() throws DatabaseOperationException {
        String name = "name";
        when(database.getByNumber(name)).thenThrow(new DatabaseOperationException());
        assertThrows(IllegalArgumentException.class,() -> invoiceService.getByNumber(null));
        assertThrows(ServiceOperationException.class,() -> invoiceService.getByNumber(name));
    }

    @Test
    void addInvoiceTest() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = createSampleInvoice();
        when(database.save(invoice)).thenReturn(invoice);
        assertEquals(invoice, invoiceService.addInvoice(invoice));
    }

    @Test
    void addInvoiceThrowsErrorTest() throws DatabaseOperationException {
        Invoice invoice = createSampleInvoice();
        when(database.save(invoice)).thenThrow(new DatabaseOperationException());
        assertThrows(IllegalArgumentException.class, () -> invoiceService.addInvoice(null));
        assertThrows(ServiceOperationException.class, () -> invoiceService.addInvoice(invoice));
    }

    @Test
    void updateInvoice() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = createSampleInvoice();
        when(database.save(invoice)).thenReturn(invoice);
        when(database.exists(invoice.getId())).thenReturn(true);
        assertEquals(invoice, invoiceService.updateInvoice(invoice));
    }

    @Test
    void updateInvoiceThrowsErrorTest() throws DatabaseOperationException {
        Invoice invoice = createSampleInvoice();
        when(database.save(invoice)).thenThrow(new DatabaseOperationException());
        when(database.exists(invoice.getId())).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> invoiceService.updateInvoice(null));
        assertThrows(ServiceOperationException.class, () -> invoiceService.updateInvoice(invoice));
    }

    @Test
    void deleteInvoiceByIdTest() throws DatabaseOperationException, ServiceOperationException {
        Long id = 1L;
        doNothing().when(database).delete(id);
        when(database.exists(id)).thenReturn(true);
        invoiceService.deleteInvoiceById(id);
    }

    @Test
    void deleteInvoiceMethodShouldThrowExceptionWhenAnErrorOccurDuringDeletingInvoiceByIdFromDatabase() throws DatabaseOperationException, ServiceOperationException {
        Long id = 1L;
        when(database.exists(id)).thenReturn(true);
        doThrow(DatabaseOperationException.class).when(database).delete(id);
        assertThrows(ServiceOperationException.class, () -> invoiceService.deleteInvoiceById(id));
    }

    @Test
    void deleteInvoiceMethodShouldThrowExceptionForNullInvoiceId() {
        Long id = 1L;
        assertThrows(IllegalArgumentException.class, () -> invoiceService.deleteInvoiceById(null));
    }

    @Test
    void deleteInvoiceMethodShouldThrowExceptionWhenInvoiceDoesNotExistInDatabase() throws DatabaseOperationException {
        Long id = 1L;
        when(database.exists(id)).thenReturn(false);
        assertThrows(ServiceOperationException.class, () -> invoiceService.deleteInvoiceById(id));
    }

    @Test
    void deleteAllInvoicesTest() throws DatabaseOperationException, ServiceOperationException {
        doNothing().when(database).deleteAll();
        invoiceService.deleteAllInvoices();
    }

    @Test
    void deleteAllInvoicesTestThrowsError() throws DatabaseOperationException, ServiceOperationException {
        doThrow(DatabaseOperationException.class).when(database).deleteAll();
        assertThrows(ServiceOperationException.class, () -> invoiceService.deleteAllInvoices());
    }

    @Test
    void shouldReturnTrueWhenInvoiceExistsInDatabase() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = createSampleInvoice();
        when(database.exists(invoice.getId())).thenReturn(true);
        assertTrue(invoiceService.invoiceExists(invoice.getId()));
    }

    @Test
    void shouldReturnFalseWhenInvoiceDoesNotExistInDatabase() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = createSampleInvoice();
        when(database.exists(invoice.getId())).thenReturn(false);
        assertFalse(invoiceService.invoiceExists(invoice.getId()));
    }

    @Test
    void invoiceExistsMethodShouldThrowExceptionWhenAnErrorOccurDuringCheckingInvoiceExists() throws DatabaseOperationException {
        Long id = 1L;
        when(database.exists(id)).thenThrow(new DatabaseOperationException());
        assertThrows(ServiceOperationException.class, () -> invoiceService.invoiceExists(id));
    }

    @Test
    void invoiceExistsMethodShouldThrowExceptionForNullInvoiceId() {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.invoiceExists(null));
    }

    private Collection<Invoice> createInvoices() {
        Collection<Invoice> invoices = new ArrayList<>();
        invoices.add(createSampleInvoice());
        return invoices;
    }

    private Invoice createSampleInvoice() {
        List<InvoiceEntry> entries = new ArrayList<>();
        entries.add(new InvoiceEntry(1L, "descr", 22L, new BigDecimal("1.25"), new BigDecimal("1.25"), new BigDecimal("1.25"), Vat.VAT_23));
        Company seller = new Company(1L, "TTX", "Radomska", "88", "2222", "9999999999", "sss@o2.pl");
        Company buyer = new Company(2L, "TT5", "Radomska", "88", "2222", "9999999999", "sss@o2.pl");
        return new Invoice(1, "999/99", LocalDate.now(), LocalDate.now(), seller, buyer, entries);
    }
}
