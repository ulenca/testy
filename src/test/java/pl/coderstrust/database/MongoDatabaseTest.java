package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static pl.coderstrust.generators.InvoiceGenerator.generateRandomInvoice;

import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.Invoice.InvoiceBuilder;

@DataMongoTest
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class MongoDatabaseTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    MongoDatabase mongoDatabase;

    @Test
    void shouldAddFirstInvoice() throws DatabaseOperationException {
        Invoice invoiceToAdd = generateRandomInvoice();
        Invoice invoiceToAddWithUpdatedId = Invoice.builder()
                .withInvoice(invoiceToAdd)
                .withId(1L)
                .build();
        Invoice addedInvoice = generateRandomInvoice();
        Query query = Query.query(Criteria.where("id").is(invoiceToAdd.getId()));
        doReturn(new ArrayList<Invoice>()).when(mongoTemplate).findAll(Invoice.class);
        doReturn(null).when(mongoTemplate).findOne(query, Invoice.class);
        doReturn(addedInvoice).when(mongoTemplate).save(invoiceToAddWithUpdatedId);

        Invoice expectedInvoice = mongoDatabase.save(invoiceToAdd);

        assertEquals(expectedInvoice, addedInvoice);
        verify(mongoTemplate).findAll(Invoice.class);
        verify(mongoTemplate).findOne(query, Invoice.class);
        verify(mongoTemplate).save(invoiceToAddWithUpdatedId);
    }
}
