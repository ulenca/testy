package pl.coderstrust.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
import pl.coderstrust.generators.InvoiceGenerator;
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
        List<Invoice> invoices = List.of(InvoiceGenerator.generateRandomInvoice(), InvoiceGenerator.generateRandomInvoice());
        doReturn(invoices).when(database).getAll();
        assertEquals(invoices, invoiceService.getAllInvoices());
    }

    @Test
    void getAllInvoicesMethodShouldThrowExceptionWhenAnErrorOccurDuringGettingAllInvoicesFromDatabase() throws DatabaseOperationException {
        doThrow(new DatabaseOperationException()).when(database).getAll();
        assertThrows(ServiceOperationException.class, () -> invoiceService.getAllInvoices());
        verify(database).getAll();
    }

    @Test
    void shouldReturnInvoiceById() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(Optional.of(invoice)).when(database).getById(invoice.getId());
        Optional<Invoice> result = invoiceService.getById(invoice.getId());
        assertTrue(result.isPresent());
        assertEquals(invoice, result.get());
        verify(database).getById(invoice.getId());
    }

    @Test
    void getInvoiceByIdMethodShouldThrowExceptionWhenAnErrorOccurDuringGettingInvoiceByIdFromDatabase() throws DatabaseOperationException {
        Long id = 1L;
        doThrow(new DatabaseOperationException()).when(database).getById(id);
        assertThrows(ServiceOperationException.class, () -> invoiceService.getById(id));
        verify(database).getById(id);
    }

    @Test
    void getByIdMethodShouldThrowExceptionForNullInvoiceId() {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.getById(null));
    }

    @Test
    void getByNumberTest() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(Optional.of(invoice)).when(database).getByNumber(invoice.getNumber());
        Optional<Invoice> result = invoiceService.getByNumber(invoice.getNumber());
        assertTrue(result.isPresent());
        assertEquals(invoice, result.get());
        verify(database).getByNumber(invoice.getNumber());
    }

    @Test
    void getInvoiceByNumberMethodShouldThrowExceptionWhenAnErrorOccurDuringGettingInvoiceByIdFromDatabase() throws DatabaseOperationException {
        String name = "name";
        doThrow(new DatabaseOperationException()).when(database).getByNumber(name);
        assertThrows(ServiceOperationException.class,() -> invoiceService.getByNumber(name));
    }

    @Test
    void getByNumberMethodShouldThrowExceptionForNullInvoiceId() {
        assertThrows(IllegalArgumentException.class,() -> invoiceService.getByNumber(null));
    }

    @Test
    void addInvoiceTest() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(invoice).when(database).save(invoice);
        assertEquals(invoice, invoiceService.addInvoice(invoice));
    }

    @Test
    void addInvoiceThrowsErrorTest() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doThrow(new DatabaseOperationException()).when(database).save(invoice);
        assertThrows(IllegalArgumentException.class, () -> invoiceService.addInvoice(null));
        assertThrows(ServiceOperationException.class, () -> invoiceService.addInvoice(invoice));
    }

    @Test
    void updateInvoice() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(invoice).when(database).save(invoice);
        doReturn(true).when(database).exists(invoice.getId());
        assertEquals(invoice, invoiceService.updateInvoice(invoice));
    }

    @Test
    void updateInvoiceThrowsErrorTest() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doThrow(new DatabaseOperationException()).when(database).save(invoice);
        doReturn(true).when(database).exists(invoice.getId());
        assertThrows(IllegalArgumentException.class, () -> invoiceService.updateInvoice(null));
        assertThrows(ServiceOperationException.class, () -> invoiceService.updateInvoice(invoice));
    }

    @Test
    void shouldDeleteInvoice() throws DatabaseOperationException, ServiceOperationException {
        Long id = 1L;
        doNothing().when(database).delete(id);
        doReturn(true).when(database).exists(id);
        invoiceService.deleteInvoiceById(id);
        verify(database).delete(id);
    }

    @Test
    void deleteInvoiceMethodShouldThrowExceptionWhenAnErrorOccurDuringDeletingInvoiceByIdFromDatabase() throws DatabaseOperationException, ServiceOperationException {
        Long id = 1L;
        doReturn(true).when(database).exists(id);
        doThrow(DatabaseOperationException.class).when(database).delete(id);
        assertThrows(ServiceOperationException.class, () -> invoiceService.deleteInvoiceById(id));
        verify(database).delete(id);
    }

    @Test
    void deleteInvoiceMethodShouldThrowExceptionForNullInvoiceId() {
        Long id = 1L;
        assertThrows(IllegalArgumentException.class, () -> invoiceService.deleteInvoiceById(null));
    }

    @Test
    void deleteInvoiceMethodShouldThrowExceptionWhenInvoiceDoesNotExistInDatabase() throws DatabaseOperationException {
        Long id = 1L;
        doReturn(false).when(database).exists(id);
        assertThrows(ServiceOperationException.class, () -> invoiceService.deleteInvoiceById(id));
    }

    @Test
    void deleteAllInvoicesTest() throws DatabaseOperationException, ServiceOperationException {
        doNothing().when(database).deleteAll();
        invoiceService.deleteAllInvoices();
        verify(database).deleteAll();
    }

    @Test
    void deleteAllInvoicesTestThrowsError() throws DatabaseOperationException, ServiceOperationException {
        doThrow(DatabaseOperationException.class).when(database).deleteAll();
        assertThrows(ServiceOperationException.class, () -> invoiceService.deleteAllInvoices());
    }

    @Test
    void shouldReturnTrueWhenInvoiceExistsInDatabase() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(database).exists(invoice.getId());
        assertTrue(invoiceService.invoiceExists(invoice.getId()));
    }

    @Test
    void shouldReturnFalseWhenInvoiceDoesNotExistInDatabase() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(false).when(database).exists(invoice.getId());
        assertFalse(invoiceService.invoiceExists(invoice.getId()));
    }

    @Test
    void invoiceExistsMethodShouldThrowExceptionWhenAnErrorOccurDuringCheckingInvoiceExists() throws DatabaseOperationException {
        Long id = 1L;
        doThrow(new DatabaseOperationException()).when(database).exists(id);
        assertThrows(ServiceOperationException.class, () -> invoiceService.invoiceExists(id));
    }

    @Test
    void invoiceExistsMethodShouldThrowExceptionForNullInvoiceId() {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.invoiceExists(null));
    }

    private Collection<Invoice> createInvoices() {
        Collection<Invoice> invoices = new ArrayList<>();
        invoices.add(InvoiceGenerator.generateRandomInvoice());
        return invoices;
    }
}
