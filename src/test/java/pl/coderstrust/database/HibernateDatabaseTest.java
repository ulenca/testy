package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.data.domain.Example;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;

@ExtendWith(MockitoExtension.class)
class HibernateDatabaseTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private HibernateDatabase database;

    @Test
    void saveMethodShouldThrowExceptionForNullAsInvoice() {
        assertThrows(IllegalArgumentException.class, () -> database.save(null));
        verify(invoiceRepository, never()).save(null);
    }

    @Test
    void shouldSaveInvoice() throws DatabaseOperationException {
        Invoice invoiceToSave = InvoiceGenerator.generateRandomInvoice();
        Invoice savedInvoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(savedInvoice).when(invoiceRepository).save(invoiceToSave);

        Invoice actualInvoice = database.save(invoiceToSave);

        assertEquals(savedInvoice, actualInvoice);
        verify(invoiceRepository).save(invoiceToSave);
    }

    @Test
    void saveInvoicesShouldThrowExceptionWhenAnErrorOccurDuringSavingInvoices() {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doThrow(new NonTransientDataAccessException("") {
        }).when(invoiceRepository).save(invoice);

        assertThrows(DatabaseOperationException.class, () -> database.save(invoice));

        verify(invoiceRepository).save(invoice);
    }

    @Test
    void getByIdMethodShouldThrowExceptionForNullAsId() {
        assertThrows(IllegalArgumentException.class, () -> database.getById(null));
        verify(invoiceRepository, never()).findById(null);
    }

    @Test
    void getByIdMethodShouldThrowExceptionWhenAnErrorOccurDuringGettingInvoice() {
        Long testInstanceValue = 1L;
        doThrow(new NonTransientDataAccessException("") {
        }).when(invoiceRepository).findById(testInstanceValue);

        assertThrows(DatabaseOperationException.class, () -> database.getById(testInstanceValue));

        verify(invoiceRepository).findById(anyLong());
    }

    @Test
    void shouldReturnInvoiceById() throws DatabaseOperationException {
        Invoice invoiceToGet = InvoiceGenerator.generateRandomInvoice();

        doReturn(Optional.of(invoiceToGet)).when(invoiceRepository).findById(invoiceToGet.getId());

        Optional<Invoice> invoice = database.getById(invoiceToGet.getId());

        assertTrue(invoice.isPresent());
        assertEquals(invoiceToGet, invoice.get());
        verify(invoiceRepository).findById(invoiceToGet.getId());
    }

    @Test
    void getByNumberMethodShouldThrowExceptionForNullAsNumber() {
        assertThrows(IllegalArgumentException.class, () -> database.getByNumber(null));
        verify(invoiceRepository, never()).findOne(null);
    }

    @Test
    void getByNumberMethodShouldThrowExceptionWhenAnErrorOccurDuringGettingInvoiceByNumber() {
        doThrow(new NonTransientDataAccessException("") {
        }).when(invoiceRepository).findOne(any(Example.class));
        assertThrows(DatabaseOperationException.class, () -> database.getByNumber("1"));
        verify(invoiceRepository).findOne(any(Example.class));
    }

    @Test
    void shouldReturnInvoiceByNumber() throws DatabaseOperationException {
        Invoice invoiceToGet = InvoiceGenerator.generateRandomInvoice();
        doReturn(Optional.of(invoiceToGet)).when(invoiceRepository).findOne(any(Example.class));

        Optional<Invoice> invoice = database.getByNumber(invoiceToGet.getNumber());

        assertTrue(invoice.isPresent());
        assertEquals(invoiceToGet, invoice.get());
        verify(invoiceRepository).findOne(any(Example.class));
    }

    @Test
    void getAllMethodShouldThrowExceptionWhenAnErrorOccurDuringGettingAllInvoicesFromDatabase() {
        doThrow(new NonTransientDataAccessException("") {
        }).when(invoiceRepository).findAll();
        assertThrows(DatabaseOperationException.class, () -> database.getAll());
        verify(invoiceRepository).findAll();
    }

    @Test
    void shouldReturnAllInvoices() throws DatabaseOperationException {
        List<Invoice> invoices = List.of(InvoiceGenerator.generateRandomInvoice(), InvoiceGenerator.generateRandomInvoice(), InvoiceGenerator.generateRandomInvoice());
        doReturn(invoices).when(invoiceRepository).findAll();

        Collection<Invoice> result = database.getAll();

        assertEquals(invoices, result);
    }

    @Test
    void deleteMethodShouldThrownExceptionForNullAsId() {
        assertThrows(IllegalArgumentException.class, () -> database.delete(null));
        verify(invoiceRepository, never()).deleteById(null);
    }

    @Test
    void deleteMethodShouldThrowExceptionWhenInvoiceDoesNotExist() {
        Long id = 69L;
        doReturn(false).when(invoiceRepository).existsById(id);
        assertThrows(DatabaseOperationException.class, () -> database.delete(id));
        verify(invoiceRepository, never()).deleteById(id);
    }

    @Test
    void deleteMethodShouldThrowExceptionWhenAnErrorOccurDuringDeletingInvoiceByIdFromDatabase() {
        Long id = 69L;
        doReturn(true).when(invoiceRepository).existsById(id);
        doThrow(new NonTransientDataAccessException("") {
        }).when(invoiceRepository).deleteById(id);
        assertThrows(DatabaseOperationException.class, () -> database.delete(id));
        verify(invoiceRepository).existsById(id);
        verify(invoiceRepository).deleteById(id);
    }

    @Test
    void shouldDeleteInvoice() throws DatabaseOperationException {
        Long id = 69L;
        doNothing().when(invoiceRepository).deleteById(id);
        doReturn(true).when(invoiceRepository).existsById(id);
        database.delete(id);
        verify(invoiceRepository).existsById(id);
        verify(invoiceRepository).deleteById(id);
    }

    @Test
    void deleteAllMethodShouldThrowExceptionWhenAnErrorOccurDuringDeletingAllInvoices() {
        doThrow(new NonTransientDataAccessException("") {
        }).when(invoiceRepository).deleteAll();
        assertThrows(DatabaseOperationException.class, () -> database.deleteAll());
        verify(invoiceRepository).deleteAll();
    }

    @Test
    void shouldDeleteAllInvoices() throws DatabaseOperationException {
        doNothing().when(invoiceRepository).deleteAll();
        database.deleteAll();
        verify(invoiceRepository).deleteAll();
    }

    @Test
    void shouldReturnTrueIfInvoiceExist() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(invoiceRepository).existsById(invoice.getId());
        assertTrue(database.exists(invoice.getId()));
        verify(invoiceRepository).existsById(invoice.getId());
    }

    @Test
    void shouldReturnFalseIfInvoiceDoesNotExist() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(false).when(invoiceRepository).existsById(invoice.getId());
        assertFalse(database.exists(invoice.getId()));
        verify(invoiceRepository).existsById(invoice.getId());
    }

    @Test
    void existMethodShouldThrownExceptionForNullAsId() {
        assertThrows(IllegalArgumentException.class, () -> database.exists(null));
        verify(invoiceRepository, never()).existsById(null);
    }

    @Test
    void existsMethodShouldThrowExceptionWhenAnErrorOccurDuringCheckingInvoiceExist() {
        Long id = 69L;
        doThrow(new NonTransientDataAccessException("") {
        }).when(invoiceRepository).existsById(id);
        assertThrows(DatabaseOperationException.class, () -> database.exists(id));
        verify(invoiceRepository).existsById(anyLong());
    }

    @Test
    void countMethodShouldThrowExceptionWhenAnErrorOccurDuringCountingInvoices() {
        doThrow(new NonTransientDataAccessException("") {
        }).when(invoiceRepository).count();
        assertThrows(DatabaseOperationException.class, () -> database.count());
        verify(invoiceRepository).count();
    }

    @Test
    void shouldReturnNumberOfInvoices() throws DatabaseOperationException {
        doReturn(5L).when(invoiceRepository).count();
        long result = database.count();
        assertEquals(5L, result);
    }
}
