package pl.coderstrust.services;

import java.util.Collection;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.coderstrust.database.Database;
import pl.coderstrust.database.DatabaseOperationException;
import pl.coderstrust.model.Invoice;

@Service
public class InvoiceService {

    private static Logger log = LoggerFactory.getLogger(InvoiceService.class);

    private Database database;

    @Autowired
    public InvoiceService(Database database) {
        this.database = database;
    }

    public Collection<Invoice> getAllInvoices() throws ServiceOperationException {
        try {
            log.debug("Getting all invoices");
            return database.getAll();
        } catch (DatabaseOperationException e) {
            log.error("An error occurred during getting all invoices", e);
            throw new ServiceOperationException("Failed to get all invoices", e);
        }
    }

    public Optional<Invoice> getById(Long id) throws ServiceOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        try {
            log.debug("Getting invoice by ID");
            return database.getById(id);
        } catch (DatabaseOperationException e) {
            log.error("An error occurred during getting invoice by ID", e);
            throw new ServiceOperationException("Failed to get invoice by id", e);
        }
    }

    public Optional<Invoice> getByNumber(String number) throws ServiceOperationException {
        if (number == null) {
            throw new IllegalArgumentException("Number cannot be null");
        }
        try {
            log.debug("Getting invoice by number");
            return database.getByNumber(number);
        } catch (DatabaseOperationException e) {
            log.error("An error occurred during getting invoice by number", e);
            throw new ServiceOperationException("Failed to get invoice by number", e);
        }
    }

    public Invoice addInvoice(Invoice invoice) throws ServiceOperationException {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null");
        }
        try {
            Long invoiceId = invoice.getId();
            if (invoiceId != null && database.exists(invoiceId)) {
                throw new ServiceOperationException("Invoice already exists in database.");
            }
            log.debug(String.format("Adding invoice: %s", invoice));
            return database.save(invoice);
        } catch (DatabaseOperationException e) {
            log.error("An error occurred during adding invoice", e);
            throw new ServiceOperationException("Failed to add invoice", e);
        }
    }

    public Invoice updateInvoice(Invoice invoice) throws ServiceOperationException {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null");
        }
        try {
            Long invoiceId = invoice.getId();
            if (invoiceId == null || !database.exists(invoiceId)) {
                throw new ServiceOperationException("Invoice does not exist in database.");
            }
            log.debug(String.format("Updating invoice: %s", invoice));
            return database.save(invoice);
        } catch (DatabaseOperationException e) {
            log.error("An error occurred during adding invoice", e);
            throw new ServiceOperationException("Failed to update invoice", e);
        }
    }

    public void deleteInvoiceById(Long id) throws ServiceOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        try {
            if (!database.exists(id)) {
                throw new ServiceOperationException("Invoice does not exist in database.");
            }
            log.debug("Deleting invoice by ID");
            database.delete(id);
        } catch (DatabaseOperationException e) {
            log.error("An error occurred during deleting invoice by ID", e);
            throw new ServiceOperationException("Failed to delete invoice", e);
        }
    }

    public void deleteAllInvoices() throws ServiceOperationException {
        try {
            log.debug("Deleting all invoices");
            database.deleteAll();
        } catch (DatabaseOperationException e) {
            log.error("An error occurred during deleting all invoices", e);
            throw new ServiceOperationException("Failed to delete invoices", e);
        }
    }

    public boolean invoiceExists(Long id) throws ServiceOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        try {
            log.debug("Checking is that invoice existing");
            return database.exists(id);
        } catch (DatabaseOperationException e) {
            log.error("An error occurred during checking is that invoice existing", e);
            throw new ServiceOperationException("Failed to perform check", e);
        }
    }
}
