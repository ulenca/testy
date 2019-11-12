package pl.coderstrust.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import pl.coderstrust.configuration.InFileDatabaseProperties;
import pl.coderstrust.helpers.FileHelper;
import pl.coderstrust.model.Invoice;

public class InFileDatabase implements Database {

    private FileHelper fileHelper;
    private ObjectMapper mapper;
    private InFileDatabaseProperties properties;
    private AtomicLong nextId;

    public InFileDatabase(FileHelper fileHelper, ObjectMapper mapper, InFileDatabaseProperties properties) throws IOException {
        this.fileHelper = fileHelper;
        this.mapper = mapper;
        this.properties = properties;
        init();
    }

    private void init() throws IOException {
        if(!fileHelper.exist(properties.getFilePath())) {
            fileHelper.create(properties.getFilePath());
        }
        nextId = new AtomicLong(getLastInvoiceId());
    }

    private long getLastInvoiceId() throws IOException {
        String lastLine = fileHelper.readLastLine(properties.getFilePath());
        if(lastLine == null) {
            return 0;
        }
        Invoice invoice = mapper.readValue(lastLine, Invoice.class);
        return invoice.getId();
    }

    @Override
    public Invoice save(Invoice invoice) throws DatabaseOperationException, JsonProcessingException {
        if(invoice == null) {
            throw new IllegalArgumentException("Invoice can not be null");
        }
        //check if the invoice exists in the fileDatabase - if yes  return update(Inovice)

        // return add(Invoice)

        ObjectMapper mapper = new ObjectMapper();
        String invoiceString = mapper.writeValueAsString(invoice);
        return null;
    }

    private Invoice update(Invoice invoice) {
        if(invoice == null) {
            throw new IllegalArgumentException("Invoice can not be null");
        }
        //use the invoice id  to get the invoice and update the values
        return invoice;
    }

    private Invoice add(Invoice invoice) {
        if(invoice == null) {
            throw new IllegalArgumentException("Invoice can not be null");
        }
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
            //clear the content of the fileDatabase or delete the file?
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
