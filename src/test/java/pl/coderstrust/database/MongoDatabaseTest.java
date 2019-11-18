package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static pl.coderstrust.generators.InvoiceGenerator.generateRandomInvoice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.coderstrust.model.Invoice;

@DataMongoTest
@ExtendWith(SpringExtension.class)
public class MongoDatabaseTest {

    @Autowired
    private MongoTemplate mongoTemplate;
    private Database database;

    @BeforeEach
    public void init() {
        database = new MongoDatabase(mongoTemplate);
    }

    @Test
    void shouldAddInvoice() throws DatabaseOperationException {
        Invoice invoiceToAdd = generateRandomInvoice();
        Invoice expectedInvoice = Invoice.builder()
                .withInvoice(invoiceToAdd)
                .build();

        Invoice addedInvoice = database.save(invoiceToAdd);

        assertEquals(expectedInvoice, addedInvoice);
        assertNotNull(addedInvoice.getMongoId());
    }
}
