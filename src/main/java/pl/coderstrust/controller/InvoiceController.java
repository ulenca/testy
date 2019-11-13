package pl.coderstrust.controller;

import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.services.InvoiceService;
import pl.coderstrust.services.ServiceOperationException;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> add(@RequestBody(required = false) Invoice invoice) {
        if (invoice == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            if (invoice.getId() != null && invoiceService.invoiceExists(invoice.getId())) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(invoiceService.addInvoice(invoice), HttpStatus.CREATED);
        } catch (ServiceOperationException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        try {
            return new ResponseEntity<>(invoiceService.getAllInvoices(), HttpStatus.OK);
        } catch (ServiceOperationException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            Optional<Invoice> invoice = invoiceService.getById(id);
            if (invoice.isPresent()) {
                return new ResponseEntity<>(invoice.get(), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ServiceOperationException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/byNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getByNumber(@RequestParam(required = false) String number) {
        if (number == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            Optional<Invoice> invoice = invoiceService.getByNumber(number);
            if (invoice.isPresent()) {
                return new ResponseEntity<>(invoice.get(), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ServiceOperationException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody(required = false) Invoice invoice) {
        if (invoice == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            if (!id.equals(invoice.getId())) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (!invoiceService.invoiceExists(id)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(invoiceService.updateInvoice(invoice), HttpStatus.CREATED);
        } catch (ServiceOperationException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable Long id) {
        try {
            if (invoiceService.invoiceExists(id)) {
                invoiceService.deleteInvoiceById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ServiceOperationException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> removeAll() {
        try {
            invoiceService.deleteAllInvoices();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ServiceOperationException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
