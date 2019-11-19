package pl.coderstrust.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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
import pl.coderstrust.model.Invoice;

@ExtendWith({MockitoExtension.class})
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
    void getByIdMethodShouldThrowExceptionForNullInvoiceId() throws DatabaseOperationException {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.getById(null));
        verify(database, never()).getById(anyLong());
    }

    @Test
    void shouldReturnInvoiceByNumber() throws DatabaseOperationException, ServiceOperationException {
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
        verify(database).getByNumber(anyString());
    }

    @Test
    void getByNumberMethodShouldThrowExceptionForNullInvoiceId() throws DatabaseOperationException {
        assertThrows(IllegalArgumentException.class,() -> invoiceService.getByNumber(null));
        verify(database, never()).getByNumber(anyString());
    }

    @Test
    void shouldAddInvoice() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoiceToAdd = InvoiceGenerator.generateRandomInvoice();
        Invoice addedInvoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(false).when(database).exists(invoiceToAdd.getId());
        doReturn(addedInvoice).when(database).save(invoiceToAdd);

        Invoice result = invoiceService.addInvoice(invoiceToAdd);

        assertEquals(addedInvoice, result);
        verify(database).save(invoiceToAdd);
        verify(database).exists(invoiceToAdd.getId());
    }

    @Test
    void shouldAddInvoiceWithNullId() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoiceToAdd = InvoiceGenerator.generateRandomInvoiceWithNullId();
        Invoice addedInvoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(addedInvoice).when(database).save(invoiceToAdd);

        Invoice result = invoiceService.addInvoice(invoiceToAdd);

        assertEquals(addedInvoice, result);
        verify(database).save(invoiceToAdd);
    }

    @Test
    void addInvoiceMethodShouldThrowExceptionWhenAnErrorOccurDuringAddingInvoiceToDatabase() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(false).when(database).exists(invoice.getId());
        doThrow(new DatabaseOperationException()).when(database).save(invoice);
        assertThrows(ServiceOperationException.class, () -> invoiceService.addInvoice(invoice));
        verify(database).exists(invoice.getId());
        verify(database).save(invoice);
    }

    @Test
    void addInvoiceMethodShouldThrowExceptionForNullInvoice() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        assertThrows(IllegalArgumentException.class, () -> invoiceService.addInvoice(null));
        verify(database, never()).save(invoice);
    }

    @Test
    void addInvoiceMethodShouldThrowExceptionWhenInvoiceExistsInDatabase() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(database).exists(invoice.getId());
        assertThrows(ServiceOperationException.class, () -> invoiceService.addInvoice(invoice));
        verify(database).exists(invoice.getId());
    }

    @Test
    void shouldUpdateInvoice() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoiceToUpdate = InvoiceGenerator.generateRandomInvoice();
        Invoice invoiceUpdated = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(database).exists(invoiceToUpdate.getId());
        doReturn(invoiceUpdated).when(database).save(invoiceToUpdate);

        Invoice result = invoiceService.updateInvoice(invoiceToUpdate);

        assertEquals(invoiceUpdated, result);
        verify(database).save(invoiceToUpdate);
        verify(database).exists(invoiceToUpdate.getId());
    }

    @Test
    void updateInvoiceMethodShouldThrowExceptionWhenAnErrorOccurDuringUpdateInvoiceInDatabase() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doThrow(new DatabaseOperationException()).when(database).save(invoice);
        doReturn(true).when(database).exists(invoice.getId());
        assertThrows(ServiceOperationException.class, () -> invoiceService.updateInvoice(invoice));
        verify(database).exists(invoice.getId());
        verify(database).save(invoice);
    }

    @Test
    void updateInvoiceMethodShouldThrowExceptionForNullInvoice() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        assertThrows(IllegalArgumentException.class, () -> invoiceService.updateInvoice(null));
        verify(database, never()).save(invoice);
    }

    @Test
    void updateInvoiceMethodShouldThrowExceptionForNullInvoiceId() throws DatabaseOperationException {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.updateInvoice(null));
        verify(database, never()).save(InvoiceGenerator.generateRandomInvoice());
    }

    @Test
    void updateInvoiceMethodShouldThrowExceptionWhenInvoiceNotExistInDatabase() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(false).when(database).exists(invoice.getId());
        assertThrows(ServiceOperationException.class, () -> invoiceService.updateInvoice(invoice));
    }

    @Test
    void shouldDeleteInvoice() throws DatabaseOperationException, ServiceOperationException {
        Long id = 1L;
        doNothing().when(database).delete(id);
        doReturn(true).when(database).exists(id);
        invoiceService.deleteInvoiceById(id);
        verify(database).exists(id);
        verify(database).delete(id);
    }

    @Test
    void deleteInvoiceMethodShouldThrowExceptionWhenAnErrorOccurDuringDeletingInvoiceByIdFromDatabase() throws DatabaseOperationException, ServiceOperationException {
        Long id = 1L;
        doReturn(true).when(database).exists(id);
        doThrow(DatabaseOperationException.class).when(database).delete(id);
        assertThrows(ServiceOperationException.class, () -> invoiceService.deleteInvoiceById(id));
        verify(database).exists(id);
        verify(database).delete(id);
    }

    @Test
    void deleteInvoiceMethodShouldThrowExceptionForNullInvoiceId() throws DatabaseOperationException {
        Long id = 1L;
        assertThrows(IllegalArgumentException.class, () -> invoiceService.deleteInvoiceById(null));
        verify(database, never()).exists(anyLong());
        verify(database, never()).delete(anyLong());
    }

    @Test
    void deleteInvoiceMethodShouldThrowExceptionWhenInvoiceDoesNotExistInDatabase() throws DatabaseOperationException {
        Long id = 1L;
        doReturn(false).when(database).exists(id);
        assertThrows(ServiceOperationException.class, () -> invoiceService.deleteInvoiceById(id));
        verify(database, never()).delete(anyLong());
    }

    @Test
    void deleteAllInvoicesTest() throws DatabaseOperationException, ServiceOperationException {
        doNothing().when(database).deleteAll();
        invoiceService.deleteAllInvoices();
        verify(database).deleteAll();
    }

    @Test
    void deleteAllMethodShouldThrowExceptionWhenAnErrorOccurDuringDeletingAllInvoicesFromDatabase() throws DatabaseOperationException, ServiceOperationException {
        doThrow(DatabaseOperationException.class).when(database).deleteAll();
        assertThrows(ServiceOperationException.class, () -> invoiceService.deleteAllInvoices());
        verify(database).deleteAll();
    }

    @Test
    void shouldReturnTrueWhenInvoiceExistsInDatabase() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(database).exists(invoice.getId());
        assertTrue(invoiceService.invoiceExists(invoice.getId()));
        verify(database).exists(invoice.getId());
    }

    @Test
    void shouldReturnFalseWhenInvoiceDoesNotExistInDatabase() throws DatabaseOperationException, ServiceOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(false).when(database).exists(invoice.getId());
        assertFalse(invoiceService.invoiceExists(invoice.getId()));
        verify(database).exists(invoice.getId());
    }

    @Test
    void invoiceExistsMethodShouldThrowExceptionWhenAnErrorOccurDuringCheckingInvoiceExists() throws DatabaseOperationException {
        Long id = 1L;
        doThrow(new DatabaseOperationException()).when(database).exists(id);
        assertThrows(ServiceOperationException.class, () -> invoiceService.invoiceExists(id));
        verify(database).exists(anyLong());
    }

    @Test
    void invoiceExistsMethodShouldThrowExceptionForNullInvoiceId() throws DatabaseOperationException {
        assertThrows(IllegalArgumentException.class, () -> invoiceService.invoiceExists(null));
        verify(database, never()).exists(anyLong());
    }
}
