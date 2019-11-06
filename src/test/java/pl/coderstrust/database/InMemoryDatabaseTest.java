package pl.coderstrust.database;

import static java.util.Comparator.comparing;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.coderstrust.generators.InvoiceGenerator.generateRandomInvoice;
import static pl.coderstrust.generators.InvoiceGenerator.generateRandomInvoiceWithNullId;
import static pl.coderstrust.generators.InvoiceGenerator.generateRandomInvoices;
import static pl.coderstrust.generators.InvoiceGenerator.getRandomInvoiceWithSpecificId;

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

    // shouldAddInvoiceWithNullId()

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
        Invoice invoice = null;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> database.save(invoice));

        assertEquals("Invoice cannot be null", exception.getMessage());
    }

    @Test
    void getByIdMethodShouldThrowExceptionForNullId() {
        Invoice invoice = generateRandomInvoiceWithNullId();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> database.getById(invoice.getId()));

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
    void shouldThrowExceptionForNullNumber() {
        Invoice invoice = Invoice.builder()
            .withNumber(null)
            .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> database.getByNumber(invoice.getNumber()));

        assertEquals("Number cannot be null", exception.getMessage());
    }

    @Test
    void shouldGetInvoiceByNumber() throws DatabaseOperationException {
        Invoice invoice1 = generateRandomInvoice();

        //when
        Optional<Invoice> invoiceInDatabase = Optional.ofNullable(database.save(invoice1));

        assertEquals(invoiceInDatabase, storage.values()
            .stream()
            .filter(invoice -> invoice.getNumber().equals(invoice.getNumber()))
            .findFirst());
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
        Invoice invoice = generateRandomInvoiceWithNullId();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> database.delete(invoice.getId()));

        assertEquals("Id cannot be null", exception.getMessage());
    }

    @Test
    void deleteMethodShouldThrowExceptionForNoSuchId() throws IOException, DatabaseOperationException {
        Invoice invoice = getRandomInvoiceWithSpecificId(1L);

        database.save(invoice);
        Exception exception = assertThrows(DatabaseOperationException.class, () -> database.delete(3L));

        assertEquals("There is no invoice with such id", exception.getMessage());
    }

    @Test
    void shouldDeleteInvoice() throws DatabaseOperationException {
        List<Invoice> invoicesInDatabase = generateRandomInvoices(3);
        for (Invoice invoice : invoicesInDatabase) {
            storage.put(invoice.getId(), invoice);
        }

        // delete

        //assertFalse(storage.containsKey(invoicesInDatabase.getId()));
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
    void existsMethodShouldThrowExceptionForNullId() throws IOException {
        Invoice invoice = Invoice.builder()
            .withId(null)
            .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> database.exists(invoice.getId()));

        assertEquals("Id cannot be null", exception.getMessage());
    }

    @Test
    void shouldCheckIfInvoiceExists() throws DatabaseOperationException {
        Invoice invoiceInDatabase = generateRandomInvoice();

        Invoice invoice = database.save(invoiceInDatabase);

        assertTrue(storage.containsKey(invoice.getId()));
    }

    @Test
    void shouldCountAmountOfInvoices() throws DatabaseOperationException {
        List<Invoice> invoicesInDatabase = generateRandomInvoices(3);

        for (Invoice invoice : invoicesInDatabase) {
            database.save(invoice);
        }

        Assertions.assertEquals(3, storage.size());
    }

    @Test
    void shouldCountZeroInvoices() throws DatabaseOperationException {
        List<Invoice> invoicesInDatabase = generateRandomInvoices(0);

        for (Invoice invoice : invoicesInDatabase) {
            database.save(invoice);
        }

        Assertions.assertEquals(0, storage.size());
    }
}
