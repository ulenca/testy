package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pl.coderstrust.generators.InvoiceGenerator.generateRandomInvoice;
import static pl.coderstrust.generators.InvoiceGenerator.getRandomInvoiceWithSpecificId;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.coderstrust.model.Invoice;

public class InMemoryDatabaseTest {

    private InMemoryDatabase db;
    private Map<Long, Invoice> storage;

    @BeforeEach
    public void init() {
        storage = new HashMap<>();
        db = new InMemoryDatabase(storage);
    }

    @Test
    public void saveMethodShouldThrowExceptionForNullInvoice() throws IOException {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> db.save(null));
        assertEquals("Invoice cannot be null", exception.getMessage());
    }

    @Test
    void saveMethodShouldAddInvoice() {
        Invoice invoice = db.save(generateRandomInvoice());
        Assertions.assertEquals(invoice, storage.get(invoice.getId()));
    }

    @Test
    void saveMethodShouldUpdateInvoice() {
        Invoice invoice1 = db.save(getRandomInvoiceWithSpecificId(1L));
        Assertions.assertEquals(invoice1, storage.get(1L));
        Invoice invoice = db.save(generateRandomInvoice());
        Assertions.assertEquals(invoice1, storage.get(1L));
    }

    @Test
    void getByIdMethodShouldThrowExceptionForNullId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> db.getById(null));
        assertEquals("Id cannot be null", exception.getMessage());
    }

    @Test
    void shouldGetInvoiceById() {
        Invoice invoice = db.save(generateRandomInvoice());
        Assertions.assertEquals(invoice, storage.get(invoice.getId()));
    }

    @Test
    void getByNumberMethodShouldThrowExceptionForNullNumber() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> db.getByNumber(null));
        assertEquals("Number cannot be null", exception.getMessage());
    }

    @Test
    void shouldGetInvoiceByNumber() {
        Optional<Invoice> invoice1 = Optional.ofNullable(db.save(generateRandomInvoice()));
        Assertions.assertEquals(invoice1, storage.values()
            .stream()
            .filter(invoice -> invoice.getNumber().equals(invoice.getNumber()))
            .findFirst());
    }

    @Test
    void shouldGetAllInvoices() {
        storage.put(1L, generateRandomInvoice());
        storage.put(2L, generateRandomInvoice());
        Assertions.assertEquals(storage.values(), db.getAll());
    }

    @Test
    void deleteMethodShouldThrowExceptionForNullId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> db.delete(null));
        assertEquals("Id cannot be null", exception.getMessage());
    }

    @Test
    void deleteMethodShouldThrowExceptionForNoSuchId() throws IOException {
        db.save(generateRandomInvoice());
        Exception exception = assertThrows(DatabaseOperationException.class, () -> db.delete(3L));
        assertEquals("There is no invoice with such id", exception.getMessage());
    }

    @Test
    void shouldDeleteInvoice() throws DatabaseOperationException {
        Invoice invoice = db.save(generateRandomInvoice());
        db.delete(invoice.getId());
        Assertions.assertFalse(storage.containsKey(invoice.getId()));
    }

    @Test
    void shouldDeleteAllInvoices() {
        db.save(generateRandomInvoice());
        db.deleteAll();
        Assertions.assertTrue(storage.isEmpty());
    }

    @Test
    void existsMethodShouldThrowExceptionForNullId() throws IOException {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> db.exists(null));
        assertEquals("Id cannot be null", exception.getMessage());
    }

    @Test
    void shouldCheckIfInvoiceExists() {
        Invoice invoice = db.save(generateRandomInvoice());
        Assertions.assertTrue(storage.containsKey(invoice.getId()));
    }

    @Test
    void shouldCountAmountOfInvoices() {
        db.save(generateRandomInvoice());
        Assertions.assertEquals(1, db.count());
    }
}
