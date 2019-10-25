package pl.coderstrust.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.junit.jupiter.api.BeforeEach;
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
    void getAllInvoicesTest() throws ServiceOperationException, DatabaseOperationException {
        when(database.getAll()).thenReturn(createInvoices());
        assertEquals(createInvoices(),invoiceService.getAllInvoices());
    }

    @Test
    void getAllInvoicesThrowsErrorTest() throws DatabaseOperationException {
        when(database.getAll()).thenThrow(new DatabaseOperationException());
        assertThrows(ServiceOperationException.class, () -> invoiceService.getAllInvoices());
    }

    @Test
    void getByIdTest() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = createInvoiceNo1();
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
        Invoice invoice = createInvoiceNo1();
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
        Invoice invoice = createInvoiceNo1();
        when(database.save(invoice)).thenReturn(invoice);
        assertEquals(invoice, invoiceService.addInvoice(invoice));
    }

    @Test
    void addInvoiceThrowsErrorTest() throws DatabaseOperationException {
        Invoice invoice = createInvoiceNo1();
        when(database.save(invoice)).thenThrow(new DatabaseOperationException());
        assertThrows(IllegalArgumentException.class, () -> invoiceService.addInvoice(null));
        assertThrows(ServiceOperationException.class, () -> invoiceService.addInvoice(invoice));
    }

    @Test
    void updateInvoice() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = createInvoiceNo1();
        when(database.save(invoice)).thenReturn(invoice);
        when(database.exists(invoice.getId())).thenReturn(true);
        assertEquals(invoice, invoiceService.updateInvoice(invoice));
    }

    @Test
    void updateInvoiceThrowsErrorTest() throws DatabaseOperationException {
        Invoice invoice = createInvoiceNo1();
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
    void deleteInvoiceByIdThrowsErrorTest() throws DatabaseOperationException {
        doThrow(new DatabaseOperationException()).when(database).deleteAll();
        assertThrows(ServiceOperationException.class, () -> invoiceService.deleteAllInvoices());
    }

    @Test
    void invoiceExistsTest() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = createInvoiceNo1();
        when(database.exists(invoice.getId())).thenReturn(true);
        assertTrue(invoiceService.invoiceExists(invoice.getId()));
    }

    @Test
    void invoiceExistsThrowErrorTest() throws DatabaseOperationException {
        Long id = 1L;
        when(database.exists(id)).thenThrow(new DatabaseOperationException());
        assertThrows(IllegalArgumentException.class, () -> invoiceService.invoiceExists(null));
        assertThrows(ServiceOperationException.class, () -> invoiceService.invoiceExists(id));
    }

    private Collection<Invoice> createInvoices() {
        Collection<Invoice> invoices = new ArrayList<>();
        invoices.add(createInvoiceNo1());
        return invoices;
    }

    private Invoice createInvoiceNo1() {
        List<InvoiceEntry> entries = new ArrayList<>();
        entries.add(new InvoiceEntry(1L, "descr", 22L, new BigDecimal("1.25"), new BigDecimal("1.25"), new BigDecimal("1.25"), Vat.VAT_23));
        Company seller = new Company(1L, "TTX", "Radomska", "88", "2222", "9999999999", "sss@o2.pl");
        Company buyer = new Company(2L, "TT5", "Radomska", "88", "2222", "9999999999", "sss@o2.pl");
        return new Invoice(1, "999/99", LocalDate.now(), LocalDate.now(), seller, buyer, entries);
    }
}