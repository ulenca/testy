package pl.coderstrust.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.ServiceConfigurationError;
import pl.coderstrust.database.Database;
import pl.coderstrust.database.DatabaseOperationException;
import pl.coderstrust.model.Invoice;

public class InvoiceService {
    private Database repository;

    public InvoiceService(Database repository) {
        this.repository = repository;
    }

    public Collection<Invoice> getAllInvoices() throws InvoiceOperationException {
        try {
            return new ArrayList<>(repository.getAll());
        } catch (DatabaseOperationException e) {
            e.getStackTrace();
            throw new InvoiceOperationException("Failed to get all invoices");
        }
    }

    public Optional<Invoice> getById(Long id) throws InvoiceOperationException {
        try {
            return repository.getById(id);
        } catch (DatabaseOperationException e) {
            e.printStackTrace();
            throw new InvoiceOperationException("Failed to get invoice by id");
        }
    }

    public Optional<Invoice> getByNumber(Long number) throws InvoiceOperationException {
        try {
            return repository.getByNumber(number);
        } catch (DatabaseOperationException e) {
            e.printStackTrace();
            throw new InvoiceOperationException("Failed to get invoice by number");
        }
    }

    public Invoice addInvoice(Invoice invoice) throws InvoiceOperationException {
        try {
            return repository.save(invoice);
        } catch (DatabaseOperationException e) {
            e.printStackTrace();
            throw new InvoiceOperationException("Failed to add invoice");
        }
    }

    public Invoice updateInvoice(Invoice invoice) throws InvoiceOperationException {
        if(!invoiceExists(invoice.getId())) {
            throw new InvoiceOperationException("Failed to update invoice. There is no invoice with id= " + invoice.getId());
        }
        try {
            return repository.save(invoice);
        } catch (DatabaseOperationException e) {
            e.printStackTrace();
            throw new InvoiceOperationException("Failed to update invoice");
        }
    }

    public void deleteInvoiceById(Long id) throws InvoiceOperationException {
        if(!invoiceExists(id)) {
            throw new InvoiceOperationException("Failed to delete invoice. There is no invoice with id= " + id);
        }
        try {
            repository.delete(id);
        } catch (DatabaseOperationException e) {
            e.printStackTrace();
            throw new InvoiceOperationException("Failed to delete invoice");
        }
    }

    public void deleteAllInvoices() throws InvoiceOperationException {
        try {
            repository.deleteAll();
        } catch (DatabaseOperationException e) {
            e.printStackTrace();
            throw new InvoiceOperationException("Failed to delete invoices");
        }
    }

    public boolean invoiceExists(Long id) throws InvoiceOperationException {
        try {
            return repository.exists(id);
        } catch (DatabaseOperationException e) {
            e.printStackTrace();
            throw new InvoiceOperationException("Failed to perform check");
        }
    }
}

