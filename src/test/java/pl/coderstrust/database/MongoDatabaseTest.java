package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators.In;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class MongoDatabaseTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    MongoDatabase database;

    @Test
    void shouldAddInvoice() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        Query findId = Query.query(Criteria.where("id").is(invoice.getId()));
        doReturn(null).when(mongoTemplate).findOne(findId, Invoice.class);
        Invoice invoiceToBeInserted = Invoice.builder().withInvoice(invoice).withId(1L).build();
        doReturn(invoiceToBeInserted).when(mongoTemplate).save(invoiceToBeInserted);

        Invoice insertedInvoice = database.save(invoice);

        assertEquals(invoiceToBeInserted, insertedInvoice);
        verify(mongoTemplate).findOne(findId, Invoice.class);
        verify(mongoTemplate).save(invoiceToBeInserted);
    }

    @Test
    public void addInvoiceShouldThrowIllegalArgumentExceptionWhenInvoiceIsNull() throws DatabaseOperationException {
        assertThrows(IllegalArgumentException.class, () -> database.save(null));

        verify(mongoTemplate, never()).save(any());
    }

    @Test
    void shouldUpdateInvoice() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        Invoice invoiceInDatabase = Invoice.builder()
                .withInvoice(InvoiceGenerator.generateRandomInvoice())
                .withId(invoice.getId())
                .withMongoId("mongoId")
                .build();
        Query findId = Query.query(Criteria.where("id").is(invoice.getId()));
        doReturn(invoiceInDatabase).when(mongoTemplate).findOne(findId, Invoice.class);
        Invoice invoiceToBeUpdated = Invoice.builder().withInvoice(invoice).withMongoId(invoiceInDatabase.getMongoId()).build();
        doReturn(invoiceToBeUpdated).when(mongoTemplate).save(invoiceToBeUpdated);

        Invoice updatedInvoice = database.save(invoice);

        assertEquals(invoiceToBeUpdated, updatedInvoice);
        verify(mongoTemplate).findOne(findId, Invoice.class);
        verify(mongoTemplate).save(invoiceToBeUpdated);
    }

    @Test
    public void shouldGetInvoiceById() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        Query findInvoice = Query.query(Criteria.where("id").is(invoice.getId()));
        doReturn(invoice).when(mongoTemplate).findOne(findInvoice, Invoice.class);

        assertEquals(invoice, database.getById(invoice.getId()).get());

        verify(mongoTemplate).findOne(findInvoice, Invoice.class);
    }

    @Test
    public void getByIdShouldThrowIllegalArgumentExceptionWhenInvoiceIsNull() throws DatabaseOperationException {
        assertThrows(IllegalArgumentException.class, () -> database.getById(null));

        verify(mongoTemplate, never()).findOne(any(), any());
    }

    @Test
    public void getByIdShouldReturnEmptyOptionalWhenInvoiceIsNotPresent() throws DatabaseOperationException {
        Query findInvoice = Query.query(Criteria.where("id").is(1L));
        doReturn(null).when(mongoTemplate).findOne(findInvoice, Invoice.class);

        assertEquals(Optional.empty(), database.getById(1L));

        verify(mongoTemplate).findOne(findInvoice, Invoice.class);
    }

    @Test
    public void shouldGetInvoiceByNumber() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        Query findInvoice = Query.query(Criteria.where("number").is(invoice.getNumber()));
        doReturn(invoice).when(mongoTemplate).findOne(findInvoice, Invoice.class);

        assertEquals(invoice, database.getByNumber(invoice.getNumber()).get());

        verify(mongoTemplate).findOne(findInvoice, Invoice.class);
    }

    @Test
    public void getByNumberShouldThrowIllegalArgumentExceptionWhenInvoiceIsNull() throws DatabaseOperationException {
        assertThrows(IllegalArgumentException.class, () -> database.getById(null));

        verify(mongoTemplate, never()).findOne(any(), any());
    }

    @Test
    public void shouldGetAllInvoices() throws DatabaseOperationException {
        Collection<Invoice> invoices = InvoiceGenerator.generateRandomInvoices(3);
        doReturn(invoices).when(mongoTemplate).findAll(Invoice.class);

        assertEquals(invoices, database.getAll());

        verify(mongoTemplate).findAll(Invoice.class);
    }

    @Test
    public void shouldDeleteInvoice() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        Query findById = Query.query(Criteria.where("id").is(invoice.getId()));
        doReturn(invoice).when(mongoTemplate).findAndRemove(findById, Invoice.class);

        database.delete(invoice.getId());

        verify(mongoTemplate).findAndRemove(findById, Invoice.class);
    }

    @Test
    public void deleteInvoiceShouldThrowIllegalArgumentExceptionWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> database.delete(null));

        verify(mongoTemplate, never()).findAndRemove(any(), any());
    }

    @Test
    public void deleteInvoiceShouldThrowDatabaseOperationExceptionWhenInvoiceIsNull() throws DatabaseOperationException {
        Query findById = Query.query(Criteria.where("id").is(1L));
        doReturn(null).when(mongoTemplate).findAndRemove(findById, Invoice.class);

        assertThrows(DatabaseOperationException.class, () -> database.delete(1L));

        verify(mongoTemplate).findAndRemove(findById, Invoice.class);
    }

    @Test
    public void shouldDeleteAllInvoices() throws DatabaseOperationException {
        doNothing().when(mongoTemplate).dropCollection(Invoice.class);

        database.deleteAll();

        verify(mongoTemplate).dropCollection(Invoice.class);
    }

    @Test
    public void shouldReturnTrueWhenInvoiceExistsInDatabase() throws DatabaseOperationException {
        Query findById = Query.query(Criteria.where("id").is(1L));
        doReturn(true).when(mongoTemplate).exists(findById, Invoice.class);

        assertTrue(database.exists(1L));

        verify(mongoTemplate).exists(findById, Invoice.class);
    }

    @Test
    public void shouldReturnFalseWhenInvoiceDoesNotExistInDatabase() throws DatabaseOperationException {
        Query findById = Query.query(Criteria.where("id").is(1L));
        doReturn(false).when(mongoTemplate).exists(findById, Invoice.class);

        assertFalse(database.exists(1L));

        verify(mongoTemplate).exists(findById, Invoice.class);
    }

    @Test
    public void existsShouldThrowIllegalArgumentExceptionWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> database.delete(null));

        verify(mongoTemplate, never()).exists(any(), anyString());
    }

    @Test
    public void shouldCountInvoices() throws DatabaseOperationException {
        Query query = new Query();
        doReturn(3L).when(mongoTemplate).count(query, Invoice.class);

        assertEquals(3L, database.count());

        verify(mongoTemplate).count(query, Invoice.class);
    }

}
