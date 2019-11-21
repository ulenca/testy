package pl.coderstrust.database;

import static java.util.Comparator.comparing;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.coderstrust.generators.InvoiceGenerator.generateRandomInvoice;
import static pl.coderstrust.generators.InvoiceGenerator.generateRandomInvoiceWithNullId;
import static pl.coderstrust.generators.InvoiceGenerator.generateRandomInvoices;
import static pl.coderstrust.generators.InvoiceGenerator.getRandomInvoiceWithSpecificId;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.coderstrust.model.Invoice;

public class InMemoryDatabaseTest {

    private Database database;
    private Map<Long, Invoice> storage;

    @BeforeEach
    public void init() {
        storage = new HashMap<>();
        database = new InMemoryDatabase(storage);
    }

    @Test
    void shouldAddInvoice() throws DatabaseOperationException, JsonProcessingException {
        Invoice invoiceToAdd = generateRandomInvoice();
        Invoice expectedInvoice = Invoice.builder()
            .withInvoice(invoiceToAdd)
            .withId(1L)
            .build();

        Invoice addedInvoice = database.save(invoiceToAdd);

        assertEquals(expectedInvoice, addedInvoice);
        assertEquals(addedInvoice, storage.get(addedInvoice.getId()));
    }

    @Test
    void shouldAddMultipleInvoices() throws DatabaseOperationException, JsonProcessingException {
        List<Invoice> invoicesToAdd = generateRandomInvoices(3);
        List<Invoice> expectedInvoices = List.of(changeInvoiceId(invoicesToAdd.get(0), 1L),
            changeInvoiceId(invoicesToAdd.get(1), 2L),
            changeInvoiceId(invoicesToAdd.get(2), 3L));

        List<Invoice> addedInvoices = new ArrayList<>();
        for (Invoice invoice : invoicesToAdd) {
            addedInvoices.add(database.save(invoice));
        }

        assertEquals(expectedInvoices, addedInvoices);
        assertEquals(addedInvoices, storage.values().stream().sorted(comparing(Invoice::getId)).collect(Collectors.toList()));
    }

    @Test
    void shouldAddInvoiceWithNullId() throws DatabaseOperationException, JsonProcessingException {
        Invoice invoiceToAdd = generateRandomInvoiceWithNullId();
        Invoice expectedInvoice = Invoice.builder()
            .withInvoice(invoiceToAdd)
            .withId(1L)
            .build();

        Invoice addedInvoice = database.save(invoiceToAdd);

        assertEquals(expectedInvoice, addedInvoice);
        assertEquals(addedInvoice, storage.get(addedInvoice.getId()));
    }

    private Invoice changeInvoiceId(Invoice invoice, Long id) {
        return Invoice.builder()
            .withInvoice(invoice)
            .withId(id)
            .build();
    }

    @Test
    void shouldUpdateInvoice() throws DatabaseOperationException, JsonProcessingException {
        Invoice invoiceInDatabase = generateRandomInvoice();
        storage.put(invoiceInDatabase.getId(), invoiceInDatabase);
        Invoice invoiceToUpdate = Invoice.builder()
            .withInvoice(invoiceInDatabase)
            .withNumber("asdasdasdas")
            .build();

        Invoice updatedInvoice = database.save(invoiceToUpdate);

        assertEquals(invoiceToUpdate, updatedInvoice);
        assertEquals(updatedInvoice, storage.get(updatedInvoice.getId()));
    }

    @Test
    public void saveMethodShouldThrowExceptionForNullInvoice() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> database.save(null));

        assertEquals("Invoice cannot be null", exception.getMessage());
    }

    @Test
    void getByIdMethodShouldThrowExceptionForNullId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> database.getById(null));

        assertEquals("Id cannot be null", exception.getMessage());
    }

    @Test
    void shouldGetInvoiceById() throws DatabaseOperationException {
        List<Invoice> invoicesInDatabase = generateRandomInvoices(3);
        for (Invoice invoice : invoicesInDatabase) {
            storage.put(invoice.getId(), invoice);
        }

        Optional<Invoice> invoice = database.getById(invoicesInDatabase.get(1).getId());

        assertTrue(invoice.isPresent());
        assertEquals(invoicesInDatabase.get(1), invoice.get());
    }

    @Test
    void getByNumberMethodShouldThrowExceptionForNullNumber() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> database.getByNumber(null));

        assertEquals("Number cannot be null", exception.getMessage());
    }

    @Test
    void shouldGetInvoiceByNumber() throws DatabaseOperationException {
        List<Invoice> invoicesInDatabase = generateRandomInvoices(3);
        for (Invoice invoice : invoicesInDatabase) {
            storage.put(invoice.getId(), invoice);
        }

        Optional<Invoice> invoice = database.getByNumber(invoicesInDatabase.get(1).getNumber());

        assertTrue(invoice.isPresent());
        assertEquals(invoicesInDatabase.get(1), invoice.get());
    }

    @Test
    void shouldGetAllInvoices() throws DatabaseOperationException {
        List<Invoice> invoicesInDatabase = generateRandomInvoices(3);

        for (Invoice invoice : invoicesInDatabase) {
            storage.put(invoice.getId(), invoice);
        }

        assertEquals(storage.values(), database.getAll());
    }

    @Test
    void deleteMethodShouldThrowExceptionForNullId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> database.delete(null));

        assertEquals("Id cannot be null", exception.getMessage());
    }

    @Test
    void deleteMethodShouldThrowExceptionWhenInvoiceDoesNotExist() {
        Invoice invoice = getRandomInvoiceWithSpecificId(1L);
        storage.put(invoice.getId(), invoice);

        Exception exception = assertThrows(DatabaseOperationException.class, () -> database.delete(3L));

        assertEquals("There is no invoice with such id", exception.getMessage());
    }

    @Test
    void shouldDeleteInvoice() throws DatabaseOperationException {
        List<Invoice> invoicesInDatabase = generateRandomInvoices(3);
        for (Invoice invoice : invoicesInDatabase) {
            storage.put(invoice.getId(), invoice);
        }

        Long id = invoicesInDatabase.get(1).getId();
        database.delete(id);

        assertFalse(storage.containsKey(id));
    }

    @Test
    void shouldDeleteAllInvoices() throws DatabaseOperationException {
        List<Invoice> invoicesInDatabase = generateRandomInvoices(3);
        for (Invoice invoice : invoicesInDatabase) {
            storage.put(invoice.getId(), invoice);
        }

        database.deleteAll();

        assertTrue(storage.isEmpty());
    }

    @Test
    void existsMethodShouldThrowExceptionForNullId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> database.exists(null));

        assertEquals("Id cannot be null", exception.getMessage());
    }

    @Test
    void shouldReturnTrueIfInvoiceExists() throws DatabaseOperationException {
        Invoice invoiceInDatabase = generateRandomInvoice();
        storage.put(invoiceInDatabase.getId(), invoiceInDatabase);

        assertTrue(database.exists(invoiceInDatabase.getId()));
    }

    @Test
    void shouldReturnFalseIfInvoiceExists() throws DatabaseOperationException {
        Invoice invoiceInDatabase = generateRandomInvoice();
        storage.put(invoiceInDatabase.getId(), invoiceInDatabase);

        assertFalse(database.exists(invoiceInDatabase.getId() + 1L));
    }

    @Test
    void shouldCountAmountOfInvoices() throws DatabaseOperationException {
        List<Invoice> invoicesInDatabase = generateRandomInvoices(3);

        for (Invoice invoice : invoicesInDatabase) {
            storage.put(invoice.getId(), invoice);
        }

        assertEquals(invoicesInDatabase.size(), database.count());
    }
}
