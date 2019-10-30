package pl.coderstrust.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import java.util.Optional;
import pl.coderstrust.model.Invoice;

public class InFileDatabase implements Database {

    FileHelper fileHelper = new FileHelper();

    @Override
    public Invoice save(Invoice invoice) throws DatabaseOperationException, JsonProcessingException {
        //
        if(invoice == null) {
            throw new IllegalArgumentException("Invoice can not be null");
        }

        ObjectMapper mapper = new ObjectMapper();
        String invoiceJson = mapper.writeValueAsString(invoice);
        return fileHelper.writeToFile(invoiceJson);
    }

    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        return Optional.empty();
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        return Optional.empty();
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        return null;
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {

    }

    @Override
    public void deleteAll() throws DatabaseOperationException {

    }

    @Override
    public boolean exists(Long id) throws DatabaseOperationException {
        return false;
    }

    @Override
    public long count() throws DatabaseOperationException {
        return 0;
    }
}
