package pl.coderstrust.database;

import com.mongodb.client.MongoCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import modelMongo.InvoiceMongo;
import modelMongo.mapper.InvoiceMapper;
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
        InvoiceMongo invoiceMongo = mongoTemplate.findOne(query, InvoiceMongo.class);
        if (invoiceMongo == null) {
            lastId = new AtomicLong(0);
            return;
        }
        lastId = new AtomicLong(invoiceMongo.getId());
    }

    @Override
    public Invoice save(Invoice invoice) throws DatabaseOperationException {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null");
        }
        InvoiceMongo invoiceMongo = InvoiceMapper.INVOICE_MAPPER_INSTANCE.invoiceToInvoiceMongo(invoice);
        InvoiceMongo savedMongoInvoice;
        try {
            InvoiceMongo invoiceInMongoDatabase = getInvoiceById(invoice.getId());
            if (invoiceInMongoDatabase != null) {
                savedMongoInvoice = update(invoiceMongo, invoiceInMongoDatabase.getMongoId());
                return InvoiceMapper.INVOICE_MAPPER_INSTANCE.invoiceMongoToInvoice(savedMongoInvoice);
            }
            savedMongoInvoice = add(invoiceMongo);
            return InvoiceMapper.INVOICE_MAPPER_INSTANCE.invoiceMongoToInvoice(savedMongoInvoice);
        } catch (Exception e) {
            throw new DatabaseOperationException();
        }
    }

    private InvoiceMongo getInvoiceById(Long id) {
        return mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), InvoiceMongo.class);
    }

    private InvoiceMongo update(InvoiceMongo invoice, String mongoId) {
        InvoiceMongo invoiceToUpdate = InvoiceMongo.builder()
                .withInvoiceMongo(invoice)
                .withMongoId(mongoId)
                .build();
        return mongoTemplate.save(invoiceToUpdate);
    }

    private InvoiceMongo add(InvoiceMongo invoice) throws DatabaseOperationException {
        InvoiceMongo invoiceToAdd = InvoiceMongo.builder()
                .withInvoiceMongo(invoice)
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
            return Optional.ofNullable(InvoiceMapper.INVOICE_MAPPER_INSTANCE.invoiceMongoToInvoice(getInvoiceById(id)));
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
            return Optional.ofNullable(InvoiceMapper.INVOICE_MAPPER_INSTANCE.invoiceMongoToInvoice(mongoTemplate.findOne(query, InvoiceMongo.class)));
        } catch (Exception e) {
            throw new DatabaseOperationException();
        }
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        try {
            return mongoTemplate.findAll(InvoiceMongo.class).stream()
                    .map(invoice -> InvoiceMapper.INVOICE_MAPPER_INSTANCE.invoiceMongoToInvoice(invoice))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new DatabaseOperationException();
        }
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        InvoiceMongo invoice = null;
        try {
            invoice = mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(id)), InvoiceMongo.class);
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
            mongoTemplate.dropCollection(InvoiceMongo.class);
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
            return mongoTemplate.exists(Query.query(Criteria.where("id").is(id)), InvoiceMongo.class);
        } catch (Exception e) {
            throw new DatabaseOperationException();
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        try {
            return mongoTemplate.count(new Query(), InvoiceMongo.class);
        } catch (Exception e) {
            throw new DatabaseOperationException();
        }
    }
}
