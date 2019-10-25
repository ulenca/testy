package pl.coderstrust.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import pl.coderstrust.database.Database;
import pl.coderstrust.database.DatabaseOperationException;
import pl.coderstrust.model.Invoice;

public class InvoiceService {
    private Database repository;

    public InvoiceService(Database repository) {
        this.repository = Objects.requireNonNull(repository, "Repository cannot be null");
    }

    public Collection<Invoice> getAllInvoices() throws ServiceOperationException {
        try {
            return new ArrayList<>(repository.getAll());
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("Failed to get all invoices");
        }
    }

    public Optional<Invoice> getById(Long id) throws ServiceOperationException {
        Objects.requireNonNull(id, "Id cannot be null");
        try {
            return repository.getById(id);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("Failed to get invoice by id");
        }
    }

    public Optional<Invoice> getByNumber(String number) throws ServiceOperationException {
        Objects.requireNonNull(number, "Number cannot be null");
        try {
            return repository.getByNumber(number);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("Failed to get invoice by number");
        }
    }

    public Invoice addInvoice(Invoice invoice) throws ServiceOperationException {
        Objects.requireNonNull(invoice, "Invoice cannot be null");
        try {
            return repository.save(invoice);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("Failed to add invoice");
        }
    }

    public Invoice updateInvoice(Invoice invoice) throws ServiceOperationException {
        Objects.requireNonNull(invoice, "Invoice cannot be null");
        if(!invoiceExists(invoice.getId())) {
            throw new ServiceOperationException("Failed to update invoice. There is no invoice with id= " + invoice.getId());
        }
        try {
            return repository.save(invoice);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("Failed to update invoice");
        }
    }

    public void deleteInvoiceById(Long id) throws ServiceOperationException {
        Objects.requireNonNull(id, "Id cannot be null");
        if(!invoiceExists(id)) {
            throw new ServiceOperationException("Failed to delete invoice. There is no invoice with id= " + id);
        }
        try {
            repository.delete(id);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("Failed to delete invoice");
        }
    }

    public void deleteAllInvoices() throws ServiceOperationException {
        try {
            repository.deleteAll();
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("Failed to delete invoices");
        }
    }

    public boolean invoiceExists(Long id) throws ServiceOperationException {
        Objects.requireNonNull(id, "Id cannot be null");
        try {
            return repository.exists(id);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("Failed to perform check");
        }
    }
}

