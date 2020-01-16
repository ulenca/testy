package pl.coderstrust.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import pl.coderstrust.configuration.InFileDatabaseProperties;
import pl.coderstrust.helpers.FileHelper;
import pl.coderstrust.model.Invoice;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "in-file")
public class InFileDatabase implements Database {

    private FileHelper fileHelper;
    private ObjectMapper mapper;
    private InFileDatabaseProperties properties;
    private AtomicLong nextId;

    private static Logger log = LoggerFactory.getLogger(InFileDatabase.class);

    public InFileDatabase(InFileDatabaseProperties properties, ObjectMapper mapper, FileHelper fileHelper) throws IOException {
        this.properties = properties;
        this.mapper = mapper;
        this.fileHelper = fileHelper;
        init();
    }

    private void init() throws IOException {
        if (!fileHelper.exist(properties.getFilePath())) {
            fileHelper.create(properties.getFilePath());
        }
        nextId = new AtomicLong(getLastInvoiceId());
    }

    private long getLastInvoiceId() throws IOException {
        String lastLine = fileHelper.readLastLine(properties.getFilePath());
        if (lastLine == null) {
            return 0;
        }
        Invoice invoice = deserializeToInvoice(lastLine);
        if (invoice == null) {
            return 0;
        }
        log.debug("Getting last id of last invoice");
        return invoice.getId();
    }

    @Override
    public synchronized Invoice save(Invoice invoice) throws DatabaseOperationException {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null");
        }
        try {
            log.debug(String.format("Saving invoice: %s", invoice));
            if (invoice.getId() != null && invoiceExists(invoice.getId())) {
                return update(invoice);
            }
            return add(invoice);
        } catch (IOException e) {
            log.error("An error occurred during saving invoice", e);
            throw new DatabaseOperationException();
        }
    }

    private boolean invoiceExists(Long id) throws IOException {
        log.debug(String.format("Checking is that invoice exist: %s", id));
        return getAllInvoices().stream()
            .anyMatch(invoice -> invoice.getId().equals(id));
    }

    private Invoice deserializeToInvoice(String json) {
        try {
            log.debug(String.format("Deserialize invoice: %s", json));
            return mapper.readValue(json, Invoice.class);
        } catch (IOException e) {
            log.error("An error occurred during deserialize invoice", e);
            return null;
        }
    }

    private List<Invoice> getAllInvoices() throws IOException {
        log.debug("Getting all invoices");
        return fileHelper.readLines(properties.getFilePath()).stream()
            .map(this::deserializeToInvoice)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private Invoice update(Invoice invoice) throws DatabaseOperationException, IOException {
        Invoice invoiceToUpdate = Invoice.builder()
            .withInvoice(invoice)
            .build();
        log.debug(String.format("Update invoice: %s", invoice));
        deleteById(invoiceToUpdate.getId());
        fileHelper.writeLine(properties.getFilePath(), mapper.writeValueAsString(invoiceToUpdate));
        return invoiceToUpdate;
    }

    private Invoice add(Invoice invoice) throws IOException {
        Invoice invoiceToAdd = Invoice.builder()
            .withInvoice(invoice)
            .withId(nextId.incrementAndGet())
            .build();
        log.debug(String.format("Add invoice: %s", invoice));
        fileHelper.writeLine(properties.getFilePath(), mapper.writeValueAsString(invoiceToAdd));
        return invoiceToAdd;
    }

    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        try {
            log.debug(String.format("Getting invoice by ID: %s", id));
            return getAllInvoices().stream()
                .filter(x -> x.getId().equals(id))
                .findFirst();
        } catch (IOException e) {
            log.error("An error occurred during getting invoice by ID", e);
            throw new DatabaseOperationException("An error occurred during getting invoice by id", e);
        }
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        if (number == null) {
            throw new IllegalArgumentException("Number cannot be null");
        }
        try {
            log.debug(String.format("Getting invoice by number: %s", number));
            return getAllInvoices().stream()
                .filter(x -> x.getNumber().equals(number))
                .findFirst();
        } catch (IOException e) {
            log.error("An error occurred during getting invoice by number", e);
            throw new DatabaseOperationException("An error occurred during getting invoice by number", e);
        }
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        try {
            log.debug("Getting all invoices");
            return getAllInvoices();
        } catch (IOException e) {
            log.error("An error occurred during getting all invoices", e);
            throw new DatabaseOperationException("An error occurred during getting all invoices", e);
        }
    }

    @Override
    public synchronized void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        try {
            log.debug(String.format("Deleting invoice: %s", id));
            deleteById(id);
        } catch (IOException e) {
            log.error("An error occurred during deleting invoice", e);
            throw new DatabaseOperationException("An error occurred during getting id", e);
        }
    }

    private void deleteById(Long id) throws DatabaseOperationException, IOException {
        log.debug(String.format("Deleting invoice: %s", id));
        fileHelper.removeLine(properties.getFilePath(), getPositionInDatabase(id));
    }

    private int getPositionInDatabase(Long id) throws DatabaseOperationException, IOException {
        List<Invoice> invoices = getAllInvoices();
        log.debug(String.format("Getting position on invoice in database: %s", id));
        Optional<Invoice> invoice = invoices.stream()
            .filter(x -> x.getId().equals(id))
            .findFirst();
        if (!invoice.isPresent()) {
            throw new DatabaseOperationException("There is no invoice in database");
        }
        return invoices.indexOf(invoice.get()) + 1;
    }

    @Override
    public synchronized void deleteAll() throws DatabaseOperationException {
        try {
            log.debug("Deleting all invoices");
            fileHelper.clear(properties.getFilePath());
        } catch (IOException e) {
            log.error("An error occurred during deleting all invoices", e);
            throw new DatabaseOperationException("An error occurred during deleting all invoices", e);
        }
    }

    @Override
    public boolean exists(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        try {
            log.debug(String.format("Checking is that invoice exist: %s", id));
            return getAllInvoices().stream()
                .anyMatch(x -> x.getId().equals(id));
        } catch (Exception e) {
            log.error("An error occurred during checking is that invoice exist", e);
            throw new DatabaseOperationException("An error occurred during getting invoice by id", e);
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        try {
            log.debug("Counting all invoices");
            return getAllInvoices().size();
        } catch (IOException e) {
            log.error("An error occurred during counting all invoices", e);
            throw new DatabaseOperationException("An error occurred during getting number of invoices", e);
        }
    }
}
