package pl.coderstrust.restapi;

import java.util.ArrayList;
import java.util.Collection;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.coderstrust.database.ServiceOperationException;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.services.InvoiceService;

@RestController
@RequestMapping("/invoices")
public class InvoiceRestController {

    private InvoiceService invoiceService;
    public InvoiceRestController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> add(@RequestBody(required = false) Invoice invoice) {
        if (invoice == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(invoice.getId() != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        try {
            invoiceService.addInvoice(invoice);
            return ResponseEntity.ok().build();
        } catch (ServiceOperationException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/invoices")
    public Collection<Invoice> getAll() {
        try {
            return invoiceService.getAllInvoices();
        } catch (ServiceOperationException e) {
            e.printStackTrace();
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            return new ArrayList<>();
        }
    }
}
