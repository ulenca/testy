package pl.coderstrust.database;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.coderstrust.model.Invoice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertEquals("df", exception.getMessage());
    }

   /* @Test
    void saveMethodShouldAddInvoice() {
        db.save(invoiceToTest);
        Assertions.assertEquals(invoiceToTest, storage.get(1L));
    }

    @Test
    void saveMethodShouldUpdateInvoice() {
    }

    @Test
    void shouldGetInvoiceById() {
        db.save(invoiceToTest);
        Assertions.assertEquals(invoiceToTest, storage.get(1L));
    }

    @Test
    void shouldGetInvoiceByNumber() {
        db.save(invoiceToTest);
        Assertions.assertEquals(invoiceToTest, storage.values()
                .stream()
                .filter(invoice -> invoice.getNumber().equals("FV2019001")));
    }

    @Test
    void shouldGetAllInvoices() {
        db.save(invoiceToTest);
      //  Assertions.assertEquals(?, storage.values());
    }

    @Test
    void shouldDeleteInvoice() throws DatabaseOperationException {
        db.save(invoiceToTest);
        db.delete(invoiceToTest.getId());
        Assertions.assertFalse(storage.containsKey(1L));
    }

    @Test
    void shouldDeleteAllInvoices() {
        db.save(invoiceToTest);
        db.deleteAll();
        Assertions.assertTrue(storage.isEmpty());
    }

    @Test
    void shouldCheckIfInvoiceExists() {
        db.save(invoiceToTest);
        Assertions.assertTrue(storage.containsKey(1L));
    }

    @Test
    void shouldCountAmountOfInvoices() {
        db.save(invoiceToTest);
        Assertions.assertEquals(1, db.count());
    }*/
}
