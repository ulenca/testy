package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pl.coderstrust.generators.InvoiceGenerator.generateRandomInvoice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.coderstrust.model.Invoice;

public class InMemoryDatabaseTest {

    private Database database;
    private Map<Long, Invoice> storage;

    @BeforeEach
    public void init() {
        storage = new HashMap<Long, Invoice>();
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
    public void shouldThrowExceptionForNullInvoice() throws IOException {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> database.save(null));
        assertEquals("Invoice cannot be null", exception.getMessage());
    }

/*
    @Test
    void saveMethodShouldUpdateInvoice() {
        Invoice invoice1 = database.save(getRandomInvoiceWithSpecificId(1L));
        Assertions.assertEquals(invoice1, storage.get(1L));
        Invoice invoice = database.save(generateRandomInvoice());
        Assertions.assertEquals(invoice1, storage.get(1L));
    }

    @Test
    void getByIdMethodShouldThrowExceptionForNullId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> database.getById(null));
        assertEquals("Id cannot be null", exception.getMessage());
    }

    @Test
    void shouldGetInvoiceById() {
        Invoice invoice = database.save(generateRandomInvoice());
        Assertions.assertEquals(invoice, storage.get(invoice.getId()));
    }

    @Test
    void getByNumberMethodShouldThrowExceptionForNullNumber() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> database.getByNumber(null));
        assertEquals("Number cannot be null", exception.getMessage());
    }

    @Test
    void shouldGetInvoiceByNumber() {
        Optional<Invoice> invoice1 = Optional.ofNullable(database.save(generateRandomInvoice()));
        Assertions.assertEquals(invoice1, storage.values()
            .stream()
            .filter(invoice -> invoice.getNumber().equals(invoice.getNumber()))
            .findFirst());
    }

    @Test
    void shouldGetAllInvoices() {
        storage.put(1L, generateRandomInvoice());
        storage.put(2L, generateRandomInvoice());
        Assertions.assertEquals(storage.values(), database.getAll());
    }

    @Test
    void deleteMethodShouldThrowExceptionForNullId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> database.delete(null));
        assertEquals("Id cannot be null", exception.getMessage());
    }

    @Test
    void deleteMethodShouldThrowExceptionForNoSuchId() throws IOException {
        database.save(generateRandomInvoice());
        Exception exception = assertThrows(DatabaseOperationException.class, () -> database.delete(3L));
        assertEquals("There is no invoice with such id", exception.getMessage());
    }

    @Test
    void shouldDeleteInvoice() throws DatabaseOperationException {
        Invoice invoice = database.save(generateRandomInvoice());
        database.delete(invoice.getId());
        Assertions.assertFalse(storage.containsKey(invoice.getId()));
    }

    @Test
    void shouldDeleteAllInvoices() {
        database.save(generateRandomInvoice());
        database.deleteAll();
        Assertions.assertTrue(storage.isEmpty());
    }

    @Test
    void existsMethodShouldThrowExceptionForNullId() throws IOException {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> database.exists(null));
        assertEquals("Id cannot be null", exception.getMessage());
    }

    @Test
    void shouldCheckIfInvoiceExists() {
        Invoice invoice = database.save(generateRandomInvoice());
        Assertions.assertTrue(storage.containsKey(invoice.getId()));
    }

    @Test
    void shouldCountAmountOfInvoices() {
        database.save(generateRandomInvoice());
        Assertions.assertEquals(1, database.count());
    }*/
}
