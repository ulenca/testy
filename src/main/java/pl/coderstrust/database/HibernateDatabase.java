package pl.coderstrust.database;

import java.util.Collection;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Repository;
import pl.coderstrust.model.Invoice;

@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "hibernate")
@Repository
public class HibernateDatabase implements Database {

    private final InvoiceRepository repository;

    private static Logger log = LoggerFactory.getLogger(HibernateDatabase.class);

    @Autowired
    public HibernateDatabase(InvoiceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Invoice save(Invoice invoice) throws DatabaseOperationException {
        if (invoice == null) {
            log.error("An error occurred during saving invoice");
            throw new IllegalArgumentException("Invoice cannot be null");
        }
        try {
            log.debug("Saving invoice");
            return repository.save(invoice);
        } catch (Exception e) {
            log.error("An error occurred during saving invoice", e);
            throw new DatabaseOperationException("An error occurred during saving invoice.", e);
        }
    }

    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("An error occurred during getting invoice by ID");
            throw new IllegalArgumentException("Id cannot be null");
        }
        try {
            log.debug("Getting invoice by ID");
            return repository.findById(id);
        } catch (Exception e) {
            log.error("An error occurred during getting invoice by ID", e);
            throw new DatabaseOperationException("An error occurred during getting invoice by id.", e);
        }
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        if (number == null) {
            log.error("An error occurred during getting invoice by number");
            throw new IllegalArgumentException("Number cannot be null");
        }
        try {
            Example<Invoice> invoiceExample = Example.of(Invoice.builder()
                    .withNumber(number)
                    .build());
            log.debug("Getting invoice by number");
            return repository.findOne(invoiceExample);
        } catch (Exception e) {
            log.error("An error occurred during getting invoice by number", e);
            throw new DatabaseOperationException("An error occurred during getting invoice by number.", e);
        }
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        try {
            log.debug("Getting all invoices");
            return repository.findAll();
        } catch (Exception e) {
            log.error("An error occurred during getting all invoices", e);
            throw new DatabaseOperationException("An error occurred during getting all invoices.", e);
        }
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("An error occurred during deleting invoice");
            throw new IllegalArgumentException("Id cannot be lower then one");
        }
        try {
            if (!repository.existsById(id)) {
                log.error("An error occurred during deleting invoice");
                throw new DatabaseOperationException("Invoice does not exist");
            }
            log.debug("Deleting invoice");
            repository.deleteById(id);
        } catch (Exception e) {
            log.error("An error occurred during deleting invoice", e);
            throw new DatabaseOperationException("An error occurred during deleting invoice.", e);
        }
    }

    @Override
    public void deleteAll() throws DatabaseOperationException {
        try {
            log.debug("Deleting all invoices");
            repository.deleteAll();
        } catch (Exception e) {
            log.error("An error occurred during deleting all invoices", e);
            throw new DatabaseOperationException("An error occurred during deleting all invoices.", e);
        }
    }

    @Override
    public boolean exists(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("An error occurred during checking is that invoice exist");
            throw new IllegalArgumentException("Id cannot be null");
        }
        try {
            log.debug("Checking is that invoice exist");
            return repository.existsById(id);
        } catch (Exception e) {
            log.error("An error occurred during checking is that invoice exist", e);
            throw new DatabaseOperationException("An error occurred during checking if invoice exists.", e);
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        try {
            log.debug("Counting all invoices");
            return repository.count();
        } catch (Exception e) {
            log.error("An error occurred during counting all invoices", e);
            throw new DatabaseOperationException("An error occurred during getting number of invoices.", e);
        }
    }
}
