package pl.coderstrust.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import java.util.Optional;
import pl.coderstrust.helpers.FileHelper;
import pl.coderstrust.model.Invoice;

public class InFileDatabase implements Database {

    private FileHelper fileHelper;
    private ObjectMapper mapper;

    public InFileDatabase(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
        this.mapper = mapper;
    }

    @Override
    public Invoice save(Invoice invoice) throws DatabaseOperationException, JsonProcessingException {
        if(invoice == null) {
            throw new IllegalArgumentException("Invoice can not be null");
        }
        //check if fileDatabase exists and if not create it

        //check if the invoice exists in the fileDatabase - if yes  return update(Inovice)

        // return add(Invoice)

        ObjectMapper mapper = new ObjectMapper();
        String invoiceJson = mapper.writeValueAsString(invoice);
        return null;
    }

    private Invoice update(Invoice invoice) {
        //use the invoice id  to get the invoice and update the values
        return invoice;
    }

    private Invoice add(Invoice invoice) {
        //write the invoice to the fileDatabase
        return invoice;
    }
    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        // find the invoice with the given id and return it
        return Optional.empty();
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        if (number == null) {
            throw new IllegalArgumentException("Number cannot be null");
        }
        //find the invoice with the given number and return it
        return Optional.empty();
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        //return all invoices from the fileDatabase
        return null;
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        //find the invoice with given id
    }

    @Override
    public void deleteAll() throws DatabaseOperationException {

    }

    @Override
    public boolean exists(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return false;
    }

    @Override
    public long count() throws DatabaseOperationException {
        return 0;
    }
}
