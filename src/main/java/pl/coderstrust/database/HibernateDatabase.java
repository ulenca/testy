package pl.coderstrust.database;

import java.util.Collection;
import java.util.Optional;
import org.springframework.data.domain.Example;
import pl.coderstrust.model.Invoice;

public class HibernateDatabase implements Database {

    private final InvoiceRepository repository;

    public HibernateDatabase(InvoiceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Invoice save(Invoice invoice) throws DatabaseOperationException {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null");
        }
        try {
            return repository.save(invoice);
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occurred during saving invoice.", e);
        }
    }

    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        try {
            return repository.findById(id);
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occurred during getting invoice by id.", e);
        }
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        if (number == null) {
            throw new IllegalArgumentException("Number cannot be null");
        }
        try {
            Example<Invoice> invoiceExample = Example.of(Invoice.builder()
                    .withNumber(number)
                    .build());
            return repository.findOne(invoiceExample);
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occurred during getting invoice by number.", e);
        }
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        try {
            return repository.findAll();
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occurred during getting all invoices.", e);
        }
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be lower then one");
        }
        try {
            if (repository.existsById(id)) {
                repository.deleteById(id);
            }else {
                throw new DatabaseOperationException("Invoice does not exist");
            }
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occurred during deleting invoice.", e);
        }
    }

    @Override
    public void deleteAll() throws DatabaseOperationException {
        try {
            repository.deleteAll();
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occurred during deleting all invoices.", e);
        }
    }

    @Override
    public boolean exists(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be lower then zero");
        }
        try {
            return repository.existsById(id);
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occurred during checking if invoice exists.", e);
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        try {
            return repository.count();
        } catch (Exception e) {
            throw new DatabaseOperationException("An error occurred during getting number of invoices.", e);
        }
    }
}
