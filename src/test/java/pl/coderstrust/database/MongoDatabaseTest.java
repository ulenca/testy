package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.mongomodel.InvoiceMongo;
import pl.coderstrust.mongomodel.mapper.InvoiceMapper;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
public class MongoDatabaseTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private MongoDatabase database;

    @Spy
    InvoiceMapper mapper = Mappers.getMapper(InvoiceMapper.class);

    @Test
    void shouldAddInvoice() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        Query findId = Query.query(Criteria.where("id").is(invoice.getId()));
        doReturn(null).when(mongoTemplate).findOne(findId, InvoiceMongo.class);
        InvoiceMongo invoiceMongoToBeInserted = mapper.invoiceToInvoiceMongo(Invoice.builder().withInvoice(invoice).withId(1L).build());
        doReturn(invoiceMongoToBeInserted).when(mongoTemplate).save(invoiceMongoToBeInserted);

        Invoice insertedInvoice = database.save(invoice);

        assertEquals(mapper.invoiceMongoToInvoice(invoiceMongoToBeInserted), insertedInvoice);
        verify(mongoTemplate).findOne(findId, InvoiceMongo.class);
        verify(mongoTemplate).save(invoiceMongoToBeInserted);
    }

    @Test
    public void addInvoiceShouldThrowIllegalArgumentExceptionWhenInvoiceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> database.save(null));

        verify(mongoTemplate, never()).save(any());
    }

    @Test
    void shouldUpdateInvoice() throws DatabaseOperationException {
        Invoice invoiceInDatabase = InvoiceGenerator.generateRandomInvoice();
        Query findId = Query.query(Criteria.where("id").is(invoiceInDatabase.getId()));
        doReturn(mapper.invoiceToInvoiceMongo(invoiceInDatabase)).when(mongoTemplate).findOne(findId, InvoiceMongo.class);
        InvoiceMongo invoiceToBeUpdated = InvoiceMongo.builder()
                .withInvoiceMongo(mapper.invoiceToInvoiceMongo(invoiceInDatabase))
                .withNumber("23232")
                .withMongoId(mapper.invoiceToInvoiceMongo(invoiceInDatabase).getMongoId())
                .build();
        doReturn(invoiceToBeUpdated).when(mongoTemplate).save(invoiceToBeUpdated);

        Invoice updatedInvoice = database.save(mapper.invoiceMongoToInvoice(invoiceToBeUpdated));

        assertEquals(mapper.invoiceMongoToInvoice(invoiceToBeUpdated), updatedInvoice);
        verify(mongoTemplate).findOne(findId, InvoiceMongo.class);
        verify(mongoTemplate).save(invoiceToBeUpdated);
    }

    @Test
    public void shouldGetInvoiceById() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        Query findInvoice = Query.query(Criteria.where("id").is(invoice.getId()));
        doReturn(mapper.invoiceToInvoiceMongo(invoice)).when(mongoTemplate).findOne(findInvoice, InvoiceMongo.class);

        assertEquals(invoice, database.getById(invoice.getId()).get());
        verify(mongoTemplate).findOne(findInvoice, InvoiceMongo.class);
    }

    @Test
    public void getByIdShouldThrowIllegalArgumentExceptionWhenInvoiceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> database.getById(null));
        verify(mongoTemplate, never()).findOne(any(), any());
    }

    @Test
    public void getByIdShouldReturnEmptyOptionalWhenInvoiceIsNotPresent() throws DatabaseOperationException {
        Query findInvoice = Query.query(Criteria.where("id").is(1L));
        doReturn(null).when(mongoTemplate).findOne(findInvoice, InvoiceMongo.class);

        assertEquals(Optional.empty(), database.getById(1L));
        verify(mongoTemplate).findOne(findInvoice, InvoiceMongo.class);
    }

    @Test
    public void shouldGetInvoiceByNumber() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        Query findInvoice = Query.query(Criteria.where("number").is(invoice.getNumber()));
        doReturn(mapper.invoiceToInvoiceMongo(invoice)).when(mongoTemplate).findOne(findInvoice, InvoiceMongo.class);

        assertEquals(invoice, database.getByNumber(invoice.getNumber()).get());
        verify(mongoTemplate).findOne(findInvoice, InvoiceMongo.class);
    }

    @Test
    public void getByNumberShouldThrowIllegalArgumentExceptionWhenInvoiceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> database.getById(null));
        verify(mongoTemplate, never()).findOne(any(), any());
    }

    @Test
    public void getByNumberShouldReturnEmptyOptionalWhenInvoiceIsNotPresent() throws DatabaseOperationException {
        Query findInvoice = Query.query(Criteria.where("number").is("1234"));
        doReturn(null).when(mongoTemplate).findOne(findInvoice, InvoiceMongo.class);

        assertEquals(Optional.empty(), database.getByNumber("1234"));
        verify(mongoTemplate).findOne(findInvoice, InvoiceMongo.class);
    }

    @Test
    public void shouldGetAllInvoices() throws DatabaseOperationException {
        Collection<Invoice> invoices = InvoiceGenerator.generateRandomInvoices(3);
        doReturn(mapper.invoicesToInvoicesMongo(invoices)).when(mongoTemplate).findAll(InvoiceMongo.class);

        assertEquals(invoices, database.getAll());
        verify(mongoTemplate).findAll(InvoiceMongo.class);
    }

    @Test
    public void shouldDeleteInvoice() throws DatabaseOperationException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        Query findById = Query.query(Criteria.where("id").is(invoice.getId()));
        doReturn(mapper.invoiceToInvoiceMongo(invoice)).when(mongoTemplate).findAndRemove(findById, InvoiceMongo.class);

        database.delete(invoice.getId());
        verify(mongoTemplate).findAndRemove(findById, InvoiceMongo.class);
    }

    @Test
    public void deleteInvoiceShouldThrowIllegalArgumentExceptionWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> database.delete(null));

        verify(mongoTemplate, never()).findAndRemove(any(), any());
    }

    @Test
    public void deleteInvoiceShouldThrowExceptionWhenInvoiceDoesNotExist() {
        Query findById = Query.query(Criteria.where("id").is(1L));
        doReturn(null).when(mongoTemplate).findAndRemove(findById, InvoiceMongo.class);

        assertThrows(DatabaseOperationException.class, () -> database.delete(1L));

        verify(mongoTemplate).findAndRemove(findById, InvoiceMongo.class);
    }

    @Test
    public void shouldDeleteAllInvoices() throws DatabaseOperationException {
        doNothing().when(mongoTemplate).dropCollection(InvoiceMongo.class);

        database.deleteAll();
        verify(mongoTemplate).dropCollection(InvoiceMongo.class);
    }

    @Test
    public void shouldReturnTrueWhenInvoiceExistsInDatabase() throws DatabaseOperationException {
        Query findById = Query.query(Criteria.where("id").is(1L));
        doReturn(true).when(mongoTemplate).exists(findById, InvoiceMongo.class);

        assertTrue(database.exists(1L));
        verify(mongoTemplate).exists(findById, InvoiceMongo.class);
    }

    @Test
    public void shouldReturnFalseWhenInvoiceDoesNotExistInDatabase() throws DatabaseOperationException {
        Query findById = Query.query(Criteria.where("id").is(1L));
        doReturn(false).when(mongoTemplate).exists(findById, InvoiceMongo.class);

        assertFalse(database.exists(1L));
        verify(mongoTemplate).exists(findById, InvoiceMongo.class);
    }

    @Test
    public void existsShouldThrowIllegalArgumentExceptionWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> database.delete(null));
        verify(mongoTemplate, never()).exists(any(), anyString());
    }

    @Test
    public void shouldReturnNumberOfInvoices() throws DatabaseOperationException {
        doReturn(3L).when(mongoTemplate).count(new Query(), InvoiceMongo.class);

        assertEquals(3L, database.count());
        verify(mongoTemplate).count(new Query(), InvoiceMongo.class);
    }

}
