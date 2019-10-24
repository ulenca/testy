package pl.coderstrust.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import pl.coderstrust.database.Database;
import pl.coderstrust.database.DatabaseOperationException;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.Vat;

@RunWith(MockitoJUnitRunner.class)
class InvoiceServiceTest {

    private Database mockRepository;
    private InvoiceService invoiceService;

    @BeforeEach
    public void init() {
        mockRepository = Mockito.mock(Database.class);
        invoiceService = new InvoiceService(mockRepository);
    }

    @Test
    public void getAllInvoicesTest() {
        try {
            Mockito.when(mockRepository.getAll()).thenReturn(createInvoices());
            assertEquals(createInvoices(),invoiceService.getAllInvoices());
        } catch (DatabaseOperationException | InvoiceOperationException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getByIdTest() {
        Invoice invoice = createInvoiceNo1();
        Optional<Invoice> optionalInvoice = Optional.of(invoice);
        try {
            Mockito.when(mockRepository.getById(invoice.getId())).thenReturn(optionalInvoice);
            assertEquals(optionalInvoice, invoiceService.getById(invoice.getId()));
        } catch (DatabaseOperationException | InvoiceOperationException e) {
            e.printStackTrace();
        }
    }

    /*
    @Test
    void getByNumberTest() {
        Invoice invoice = createInvoiceNo1();
        Optional<Invoice> optionalInvoice = Optional.of(invoice);
        try {
            Mockito.when(mockRepository.getByNumber(invoice.getNumber())).thenReturn(optionalInvoice);
            assertEquals(optionalInvoice, invoiceService.getById(invoice.getNumber()));
        } catch (DatabaseOperationException | InvoiceOperationException e) {
            e.printStackTrace();
        }
    }
    */

    @Test
    void addInvoiceTest() {
        Invoice invoice = createInvoiceNo1();
        try {
            Mockito.when(mockRepository.save(invoice)).thenReturn(invoice);
            assertEquals(invoice, invoiceService.addInvoice(invoice));
        } catch (DatabaseOperationException | InvoiceOperationException e) {
            e.printStackTrace();
        }
    }

    @Test
    void updateInvoice() {
        Invoice invoice = createInvoiceNo1();
        try {
            Mockito.when(mockRepository.save(invoice)).thenReturn(invoice);
            assertEquals(invoice, invoiceService.updateInvoice(invoice));
        } catch (DatabaseOperationException | InvoiceOperationException e) {
            e.printStackTrace();
        }
    }

    @Test
    void invoiceExistsTest() {
        Invoice invoice = createInvoiceNo1();
        try {
            Mockito.when(mockRepository.exists(invoice.getId())).thenReturn(true);
            assertTrue(invoiceService.invoiceExists(invoice.getId()));
        } catch (DatabaseOperationException | InvoiceOperationException e) {
            e.printStackTrace();
        }
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