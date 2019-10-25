package pl.coderstrust.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import pl.coderstrust.database.Database;
import pl.coderstrust.database.DatabaseOperationException;
import pl.coderstrust.model.Invoice;

public class InvoiceService {
    private Database database;

    public InvoiceService(Database database) {
        this.database = database;
    }

    public Collection<Invoice> getAllInvoices() throws ServiceOperationException {
        try {
            return database.getAll();
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("Failed to get all invoices");
        }
    }

    public Optional<Invoice> getById(Long id) throws ServiceOperationException {
        Objects.requireNonNull(id, "Id cannot be null");
        try {
            return database.getById(id);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("Failed to get invoice by id");
        }
    }

    public Optional<Invoice> getByNumber(String number) throws ServiceOperationException {
        Objects.requireNonNull(number, "Number cannot be null");
        try {
            return database.getByNumber(number);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("Failed to get invoice by number");
        }
    }

    public Invoice addInvoice(Invoice invoice) throws ServiceOperationException {
        Objects.requireNonNull(invoice, "Invoice cannot be null");
        try {
            return database.save(invoice);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("Failed to add invoice");
        }
    }

    public Invoice updateInvoice(Invoice invoice) throws ServiceOperationException {
        Objects.requireNonNull(invoice, "Invoice cannot be null");
        if (invoiceExists(invoice.getId())) {
            try {
                return database.save(invoice);
            } catch (DatabaseOperationException e) {
                throw new ServiceOperationException("Failed to update invoice");
            }
        } else {
            throw new ServiceOperationException("Failed to update invoice. There is no invoice with id= " + invoice.getId());
        }
    }

    public void deleteInvoiceById(Long id) throws ServiceOperationException {
        Objects.requireNonNull(id, "Id cannot be null");
        if (invoiceExists(id)) {
            try {
                database.delete(id);
            } catch (DatabaseOperationException e) {
                throw new ServiceOperationException("Failed to delete invoice");
            }
        } else {
            throw new ServiceOperationException("Failed to delete invoice. There is no invoice with id= " + id);
        }
    }

    public void deleteAllInvoices() throws ServiceOperationException {
        try {
            database.deleteAll();
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("Failed to delete invoices");
        }
    }

    public boolean invoiceExists(Long id) throws ServiceOperationException {
        Objects.requireNonNull(id, "Id cannot be null");
        try {
            return database.exists(id);
        } catch (DatabaseOperationException e) {
            throw new ServiceOperationException("Failed to perform check");
        }
    }
}
