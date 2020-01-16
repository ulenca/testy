package pl.coderstrust.database;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import pl.coderstrust.model.Invoice;

@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "in-memory")
@Repository
public class InMemoryDatabase implements Database {

    private Map<Long, Invoice> storage;
    private AtomicLong nextId = new AtomicLong(1);

    private static Logger log = LoggerFactory.getLogger(InMemoryDatabase.class);

    @Autowired
    public InMemoryDatabase(Map<Long, Invoice> storage) {
        this.storage = storage;
    }

    @Override
    public synchronized Invoice save(Invoice invoice) {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null");
        }
        if (invoice.getId() != null && storage.containsKey(invoice.getId())) {
            return update(invoice);
        }
        log.debug(String.format("Saving invoice : %s", invoice));
        return add(invoice);
    }

    private Invoice update(Invoice invoice) {
        log.debug(String.format("Updating invoice : %s", invoice));
        Invoice invoiceToUpdate = Invoice.builder()
                .withInvoice(invoice)
                .build();
        storage.put(invoiceToUpdate.getId(), invoiceToUpdate);
        return invoiceToUpdate;
    }

    private Invoice add(Invoice invoice) {
        log.debug(String.format("Adding invoice : %s", invoice));
        Invoice invoiceToAdd = Invoice.builder()
                .withInvoice(invoice)
                .withId(nextId.getAndIncrement())
                .build();
        storage.put(invoiceToAdd.getId(), invoiceToAdd);
        return invoiceToAdd;
    }

    @Override
    public Optional<Invoice> getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        log.debug(String.format("Getting invoice by ID : %s", id));
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Invoice> getByNumber(String number) {
        if (number == null) {
            throw new IllegalArgumentException("Number cannot be null");
        }
        log.debug(String.format("Getting invoice by number: %s", number));
        return storage.values().stream()
                .filter(i -> i.getNumber().equals(number))
                .findFirst();
    }

    @Override
    public Collection<Invoice> getAll() {
        log.debug("Getting all invoices");
        return storage.values();
    }

    @Override
    public synchronized void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (!storage.containsKey(id)) {
            throw new DatabaseOperationException("There is no invoice with such id");
        }
        log.debug(String.format("Deleting invoice: %s", id));
        storage.remove(id);
    }

    @Override
    public synchronized void deleteAll() {
        log.debug("Deleting all invoices");
        storage.clear();
    }

    @Override
    public boolean exists(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        log.debug(String.format("Checking is that invoice exist: %s", id));
        return storage.containsKey(id);
    }

    @Override
    public long count() {
        log.debug("Counting all invoices");
        return storage.size();
    }
}
