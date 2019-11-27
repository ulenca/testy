package pl.coderstrust.database;

import com.mongodb.client.MongoCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pl.coderstrust.model.Invoice;

@Repository
public class MongoDatabase implements Database {

    private MongoTemplate mongoTemplate;
    private AtomicLong lastId;

    @Autowired
    public MongoDatabase(MongoTemplate mongoTemplate) throws DatabaseOperationException {
        try {
            this.mongoTemplate = mongoTemplate;
            init();
        } catch (Exception e) {
            throw new DatabaseOperationException();
        }
    }

    private void init() {
        Query query = new Query();
        query.with(new Sort(Sort.Direction.DESC, "id"));
        query.limit(1);
        Invoice invoice = mongoTemplate.findOne(query, Invoice.class);
        if (invoice == null) {
            lastId = new AtomicLong(0);
            return;
        }
        lastId = new AtomicLong(invoice.getId());
    }

    @Override
    public Invoice save(Invoice invoice) throws DatabaseOperationException {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null");
        }
        try {
            Invoice invoiceInDatabase = getInvoiceById(invoice.getId());
            if (invoiceInDatabase != null) {
                return update(invoice, invoiceInDatabase.getMongoId());
            }
            return add(invoice);
        } catch (Exception e) {
            throw new DatabaseOperationException();
        }
    }

    private Invoice getInvoiceById(Long id) {
        return mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), Invoice.class);
    }

    private Invoice update(Invoice invoice, String mongoId) {
        Invoice invoiceToUpdate = Invoice.builder()
                .withInvoice(invoice)
                .withMongoId(mongoId)
                .build();
        return mongoTemplate.save(invoiceToUpdate);
    }

    private Invoice add(Invoice invoice) throws DatabaseOperationException {
        Invoice invoiceToAdd = Invoice.builder()
                .withInvoice(invoice)
                .withId(lastId.incrementAndGet())
                .build();
        return mongoTemplate.save(invoiceToAdd);
    }

    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        try {
            return Optional.ofNullable(getInvoiceById(id));
        } catch (Exception e) {
            throw new DatabaseOperationException();
        }
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        if (number == null) {
            throw new IllegalArgumentException("Number cannot be null");
        }
        try {
            Query query = Query.query(Criteria.where("number").is(number));
            return Optional.ofNullable(mongoTemplate.findOne(query, Invoice.class));
        } catch (Exception e) {
            throw new DatabaseOperationException();
        }
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        try {
            return mongoTemplate.findAll(Invoice.class);
        } catch (Exception e) {
            throw new DatabaseOperationException();
        }
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        Invoice invoice = null;
        try {
            invoice = mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(id)), Invoice.class);
        } catch (Exception e) {
            throw new DatabaseOperationException();
        }
        if (invoice == null) {
            throw new DatabaseOperationException(String.format("There is no invoice with id: %s", id));
        }
    }

    @Override
    public void deleteAll() throws DatabaseOperationException {
        try {
            mongoTemplate.dropCollection(Invoice.class);
        } catch (Exception e) {
            throw new DatabaseOperationException();
        }
    }

    @Override
    public boolean exists(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        try {
            return mongoTemplate.exists(Query.query(Criteria.where("id").is(id)), Invoice.class);
        } catch (Exception e) {
            throw new DatabaseOperationException();
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        try {
            return mongoTemplate.count(new Query(), Invoice.class);
        } catch (Exception e) {
            throw new DatabaseOperationException();
        }
    }
}
