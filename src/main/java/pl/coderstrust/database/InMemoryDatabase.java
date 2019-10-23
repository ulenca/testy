package pl.coderstrust.database;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import pl.coderstrust.model.Invoice;

public class InMemoryDatabase implements Database {

    private Map<Long, Invoice> storage;
    private AtomicLong nextId = new AtomicLong(1);

    public InMemoryDatabase(Map<Long, Invoice> storage) {
        this.storage = storage;
    }

    @Override
    public synchronized Invoice save(Invoice invoice) throws DatabaseOperationException {
        if (invoice.getId() != null || storage.containsKey(invoice.getId())) {
            return update(invoice);
        }
        return add(invoice);
    }

    private Invoice update(Invoice invoice) {
        if (invoice == null) {
            throw new IllegalArgumentException("Inovice cannot be null");
        }
        // find the id and update the content
        return invoice;
    }

    private Invoice add(Invoice invoice) {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null");
        }
        Invoice invoiceToAdd =
        storage.put(invoice.getId(), invoice);
        return invoice;
    }

    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        if (number == null) {
            throw new IllegalArgumentException("Number cannot be null");
        }
        return storage.values().stream()
                .filter(i -> i.getNumber().equals(number))
                .findFirst();
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        return storage.values();
    }

    @Override
    public synchronized void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        storage.remove(id);
    }

    @Override
    public synchronized void deleteAll() throws DatabaseOperationException {
        storage.clear();
    }

    @Override
    public boolean exists(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return storage.containsKey(id);
    }

    @Override
    public long count() throws DatabaseOperationException {
        return storage.size();
    }
}
