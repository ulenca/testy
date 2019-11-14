package pl.coderstrust.database;

import com.mongodb.client.MongoClients;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import pl.coderstrust.model.Invoice;

public class MongoDatabase implements Database {

    private MongoOperations mongoOperations;

    @Autowired
    public MongoDatabase(MongoTemplate mongoTemplate) {
        this.mongoOperations = mongoTemplate;
    }

    @Override
    public Invoice save(Invoice invoice) throws DatabaseOperationException {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null");
        }
        return mongoOperations.save(invoice);
    }

    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        return Optional.empty();
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        return Optional.empty();
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        return null;
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {

    }

    @Override
    public void deleteAll() throws DatabaseOperationException {

    }

    @Override
    public boolean exists(Long id) throws DatabaseOperationException {
        return false;
    }

    @Override
    public long count() throws DatabaseOperationException {
        return 0;
    }
}
