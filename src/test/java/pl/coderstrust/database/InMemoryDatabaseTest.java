package pl.coderstrust.database;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.coderstrust.model.Invoice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pl.coderstrust.generators.InvoiceGenerator.createInvoice1;
import static pl.coderstrust.generators.InvoiceGenerator.createInvoice2;

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
        Invoice invoice = db.save(createInvoice1());
        Assertions.assertEquals(invoice, storage.get(1L));
    }

    @Test
    void saveMethodShouldUpdateInvoice() {
        Invoice invoice1 = db.save(createInvoice1());
        Assertions.assertEquals(invoice1, storage.get(1L));
        Invoice invoice2 = db.save(createInvoice2());
        Assertions.assertEquals(invoice2, storage.get(1L));
    }

    @Test
    void getByIdMethodShouldThrowExceptionForNullId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> db.getById(null));
        assertEquals("Id cannot be null", exception.getMessage());
    }

    @Test
    void shouldGetInvoiceById() {
        Invoice invoice = db.save(createInvoice1());
        Assertions.assertEquals(invoice, storage.get(1L));
    }

    @Test
    void getByNumberMethodShouldThrowExceptionForNullNumber() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> db.getByNumber(null));
        assertEquals("Number cannot be null", exception.getMessage());
    }

    @Test
    void shouldGetInvoiceByNumber() {
        Optional<Invoice> invoice1 = Optional.ofNullable(db.save(createInvoice1()));
        Assertions.assertEquals(invoice1, storage.values()
                .stream()
                .filter(invoice -> invoice.getNumber().equals("FV2019001"))
                .findFirst());
    }

    @Test
    void shouldGetAllInvoices() {
        db.save(createInvoice1());
        db.save(createInvoice2());
        Assertions.assertEquals(storage.values(), db.getAll());
    }

    @Test
    void deleteMethodShouldThrowExceptionForNullId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> db.delete(null));
        assertEquals("Id cannot be null", exception.getMessage());
    }

    @Test
    void deleteMethodShouldThrowExceptionForNoSuchId() throws IOException {
        db.save(createInvoice1());
        Exception exception = assertThrows(DatabaseOperationException.class, () -> db.delete(3L));
        assertEquals("There is no invoice with such id", exception.getMessage());
    }

    @Test
    void shouldDeleteInvoice() throws DatabaseOperationException {
        db.save(createInvoice1());
        db.delete(createInvoice1().getId());
        Assertions.assertFalse(storage.containsKey(1L));
    }

    @Test
    void shouldDeleteAllInvoices() {
        db.save(createInvoice1());
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
        db.save(createInvoice1());
        Assertions.assertTrue(storage.containsKey(1L));
    }

    @Test
    void shouldCountAmountOfInvoices() {
        db.save(createInvoice1());
        Assertions.assertEquals(1, db.count());
    }
}
