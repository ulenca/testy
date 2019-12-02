package pl.coderstrust.database;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;
import pl.coderstrust.model.Invoice;

@Repository
public class InMemoryDatabase implements Database {

    private Map<Long, Invoice> storage;
    private AtomicLong nextId = new AtomicLong(1);

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
        return add(invoice);
    }

    private Invoice update(Invoice invoice) {
        Invoice invoiceToUpdate = Invoice.builder()
                .withInvoice(invoice)
                .build();
        storage.put(invoiceToUpdate.getId(), invoiceToUpdate);
        return invoiceToUpdate;
    }

    private Invoice add(Invoice invoice) {
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
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Invoice> getByNumber(String number) {
        if (number == null) {
            throw new IllegalArgumentException("Number cannot be null");
        }
        return storage.values().stream()
                .filter(i -> i.getNumber().equals(number))
                .findFirst();
    }

    @Override
    public Collection<Invoice> getAll() {
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
        storage.remove(id);
    }

    @Override
    public synchronized void deleteAll() {
        storage.clear();
    }

    @Override
    public boolean exists(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return storage.containsKey(id);
    }

    @Override
    public long count() {
        return storage.size();
    }
}
