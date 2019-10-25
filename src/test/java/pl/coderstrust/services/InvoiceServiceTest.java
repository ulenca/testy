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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.coderstrust.database.Database;
import pl.coderstrust.database.DatabaseOperationException;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.Vat;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    private Database mockRepository;
    private InvoiceService invoiceService;

    @BeforeEach
    void init() {
        mockRepository = mock(Database.class);
        invoiceService = new InvoiceService(mockRepository);
    }

    @Test
    void invoiceServiceConstructorThrowsErrorTest() {
        assertThrows(NullPointerException.class, () -> new InvoiceService(null));
    }

    @Test
    void getAllInvoicesTest() throws ServiceOperationException, DatabaseOperationException {
        when(mockRepository.getAll()).thenReturn(createInvoices());
        assertEquals(createInvoices(),invoiceService.getAllInvoices());
    }

    @Test
    void getByIdTest() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = createInvoiceNo1();
        Optional<Invoice> optionalInvoice = Optional.of(invoice);
        when(mockRepository.getById(invoice.getId())).thenReturn(optionalInvoice);
        assertEquals(optionalInvoice, invoiceService.getById(invoice.getId()));
    }

    @Test
    void getByIdThrowsErrorsTest() throws DatabaseOperationException {
        Long id = 1L;
        when(mockRepository.getById(id)).thenThrow(new DatabaseOperationException());
        assertThrows(NullPointerException.class, () -> invoiceService.getById(null));
        assertThrows(ServiceOperationException.class, () -> invoiceService.getById(id));
    }

    @Test
    void getByNumberTest() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = createInvoiceNo1();
        Optional<Invoice> optionalInvoice = Optional.of(invoice);
        when(mockRepository.getByNumber(invoice.getNumber())).thenReturn(optionalInvoice);
        assertEquals(optionalInvoice, invoiceService.getByNumber(invoice.getNumber()));
    }

    @Test
    void getByNumberThrowsErrorTest() throws DatabaseOperationException {
        String name = "name";
        when(mockRepository.getByNumber(name)).thenThrow(new DatabaseOperationException());
        assertThrows(NullPointerException.class,() -> invoiceService.getByNumber(null));
        assertThrows(ServiceOperationException.class,() -> invoiceService.getByNumber(name));
    }

    @Test
    void addInvoiceTest() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = createInvoiceNo1();
        when(mockRepository.save(invoice)).thenReturn(invoice);
        assertEquals(invoice, invoiceService.addInvoice(invoice));
    }

    @Test
    void addInvoiceThrowsErrorTest() throws DatabaseOperationException {
        Invoice invoice = createInvoiceNo1();
        when(mockRepository.save(invoice)).thenThrow(new DatabaseOperationException());
        assertThrows(NullPointerException.class, () -> invoiceService.addInvoice(null));
        assertThrows(ServiceOperationException.class, () -> invoiceService.addInvoice(invoice));
    }

    @Test
    void updateInvoice() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = createInvoiceNo1();
        when(mockRepository.save(invoice)).thenReturn(invoice);
        when(mockRepository.exists(invoice.getId())).thenReturn(true);
        assertEquals(invoice, invoiceService.updateInvoice(invoice));
    }

    @Test
    void updateInvoiceThrowsErrorTest() throws DatabaseOperationException {
        Invoice invoice = createInvoiceNo1();
        when(mockRepository.save(invoice)).thenThrow(new DatabaseOperationException());
        when(mockRepository.exists(invoice.getId())).thenReturn(true);
        assertThrows(NullPointerException.class, () -> invoiceService.updateInvoice(null));
        assertThrows(ServiceOperationException.class, () -> invoiceService.updateInvoice(invoice));
    }

    @Test
    void deleteInvoiceByIdTest() throws DatabaseOperationException, ServiceOperationException {
        Long id = 1L;
        doNothing().when(mockRepository).delete(id);
        when(mockRepository.exists(id)).thenReturn(true);
        invoiceService.deleteInvoiceById(id);
    }

    @Test
    void deleteInvoiceByIdThrowsErrorTest() throws DatabaseOperationException {
        doThrow(new DatabaseOperationException()).when(mockRepository).deleteAll();
        assertThrows(ServiceOperationException.class, () -> invoiceService.deleteAllInvoices());
    }

    @Test
    void invoiceExistsTest() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = createInvoiceNo1();
        when(mockRepository.exists(invoice.getId())).thenReturn(true);
        assertTrue(invoiceService.invoiceExists(invoice.getId()));
    }

    @Test
    void invoiceExistsThrowErrorTest() throws DatabaseOperationException {
        Long id = 1L;
        when(mockRepository.exists(id)).thenThrow(new DatabaseOperationException());
        assertThrows(NullPointerException.class, () -> invoiceService.invoiceExists(null));
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