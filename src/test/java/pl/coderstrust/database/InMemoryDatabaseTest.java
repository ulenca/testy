package pl.coderstrust.database;

import static java.util.Comparator.comparing;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.coderstrust.generators.InvoiceGenerator.generateRandomInvoice;
import static pl.coderstrust.generators.InvoiceGenerator.generateRandomInvoices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
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
    void shouldAddInvoice() throws DatabaseOperationException {
        Invoice invoiceToAdd = generateRandomInvoice();
        Invoice expectedInvoice = Invoice.builder()
            .withInvoice(invoiceToAdd)
            .withId(1L)
            .build();

        Invoice addedInvoice = database.save(invoiceToAdd);

        assertEquals(expectedInvoice, addedInvoice);
        assertEquals(addedInvoice, storage.get(addedInvoice.getId()));
    }

    ////shouldAddInvoiceWithNullId
    @Test
    void shouldAddMultipleInvoices() throws DatabaseOperationException {
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

    private Invoice changeInvoiceId(Invoice invoice, Long id) {
        return Invoice.builder()
            .withInvoice(invoice)
            .withId(id)
            .build();
    }

    @Test
    void shouldUpdateInvoice() throws DatabaseOperationException {
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
    public void saveMethodShouldThrowExceptionForNullInvoice() throws IOException {
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
        for (Invoice invoice : invoicesInDatabase){
            storage.put(invoice.getId(), invoice);
        }

        Optional<Invoice> invoice = database.getById(invoicesInDatabase.get(1).getId());

        assertTrue(invoice.isPresent());
        assertEquals(invoicesInDatabase.get(1), invoice.get());
    }

    @Test
    void shouldThrowExceptionForNullNumber() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> database.getByNumber(null));
        assertEquals("Number cannot be null", exception.getMessage());
    }

    @Test
    void shouldGetInvoiceByNumber() throws DatabaseOperationException {
        Optional<Invoice> invoiceInDatabase = Optional.ofNullable(database.save(generateRandomInvoice()));
        assertEquals(invoiceInDatabase, storage.values()
            .stream()
            .filter(invoice -> invoice.getNumber().equals(invoice.getNumber()))
            .findFirst());
    }

    @Test
    void shouldGetAllInvoices() throws DatabaseOperationException {
        storage.put(1L, generateRandomInvoice());
        storage.put(2L, generateRandomInvoice());
        assertEquals(storage.values(), database.getAll());
    }

    @Test
    void deleteMethodShouldThrowExceptionForNullId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> database.delete(null));
        assertEquals("Id cannot be null", exception.getMessage());
    }

    @Test
    void deleteMethodShouldThrowExceptionForNoSuchId() throws IOException, DatabaseOperationException {
        database.save(generateRandomInvoice());
        Exception exception = assertThrows(DatabaseOperationException.class, () -> database.delete(3L));
        assertEquals("There is no invoice with such id", exception.getMessage());
    }

    @Test
    void shouldDeleteInvoice() throws DatabaseOperationException {
        Invoice invoice = database.save(generateRandomInvoice());
        database.delete(invoice.getId());
        assertFalse(storage.containsKey(invoice.getId()));
    }

    @Test
    void shouldDeleteAllInvoices() throws DatabaseOperationException {
        database.save(generateRandomInvoice());
        database.deleteAll();
        assertTrue(storage.isEmpty());
    }

    @Test
    void existsMethodShouldThrowExceptionForNullId() throws IOException {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> database.exists(null));
        assertEquals("Id cannot be null", exception.getMessage());
    }

    @Test
    void shouldCheckIfInvoiceExists() throws DatabaseOperationException {
        Invoice invoice = database.save(generateRandomInvoice());
        assertTrue(storage.containsKey(invoice.getId()));
    }

    @Test
    void shouldCountAmountOfInvoices() throws DatabaseOperationException {
        database.save(generateRandomInvoice());
        Assertions.assertEquals(1, storage.size());
    }
}
