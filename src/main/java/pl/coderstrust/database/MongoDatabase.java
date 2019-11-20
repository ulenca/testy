package pl.coderstrust.database;

import com.mongodb.client.MongoCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pl.coderstrust.model.Invoice;

@Repository
public class MongoDatabase implements Database {

    private MongoTemplate mongoTemplate;

    @Autowired
    public MongoDatabase(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Invoice save(Invoice invoice) throws DatabaseOperationException {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null");
        }
        Optional<Invoice> invoiceToSave = getById(invoice.getId());
        if (invoice.getId() != null && invoiceToSave.isPresent()) {
            return update(invoice, invoiceToSave.get().getMongoId());
        }
        return add(invoice);
    }

    private Invoice update(Invoice invoice, String mongoId) {
        Invoice invoiceToUpdate = Invoice.builder()
                .withInvoice(invoice)
                .build();
        invoiceToUpdate.setMongoId(mongoId);
        return mongoTemplate.save(invoiceToUpdate);
    }

    private Invoice add(Invoice invoice) throws DatabaseOperationException {
        AtomicLong maxId;
        if (count() == 0) {
            maxId = new AtomicLong(0);
        } else {
            maxId = new AtomicLong(getAll().stream()
                    .max(Comparator.comparing(i -> i.getId()))
                    .get().getId());
        }
        Invoice invoiceToAdd = Invoice.builder()
                .withInvoice(invoice)
                .withId(maxId.incrementAndGet())
                .build();
        return mongoTemplate.save(invoiceToAdd);
    }

    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        Query query = Query.query(Criteria.where("id").is(id));
        return Optional.ofNullable(mongoTemplate.findOne(query, Invoice.class));
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        if (number == null) {
            throw new IllegalArgumentException("Number cannot be null");
        }
        Query query = Query.query(Criteria.where("number").is(number));
        return Optional.ofNullable(mongoTemplate.findOne(query, Invoice.class));
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        return mongoTemplate.findAll(Invoice.class);
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (!exists(id)) {
            throw new DatabaseOperationException("There is no invoice with such id");
        }
        Query query = Query.query(Criteria.where("id").is(id));
        mongoTemplate.findAndRemove(query, Invoice.class);
    }

    @Override
    public void deleteAll() throws DatabaseOperationException {
        MongoCollection collection = mongoTemplate.getCollection("invoice");
        collection.drop();
    }

    @Override
    public boolean exists(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return getById(id).isPresent();
    }

    @Override
    public long count() throws DatabaseOperationException {
        return getAll().size();
    }
}
