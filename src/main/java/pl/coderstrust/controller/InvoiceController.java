package pl.coderstrust.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.services.EmailService;
import pl.coderstrust.services.InvoiceService;
import pl.coderstrust.services.ServiceOperationException;

@RestController
@RequestMapping("/invoices")
@Api(value = "/invoices")
public class InvoiceController {

    private InvoiceService invoiceService;
    private EmailService emailService;

    public InvoiceController(InvoiceService invoiceService, EmailService emailService) {
    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
        this.emailService = emailService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Add new invoice",
            notes = "Add new invoice to database",
            response = Invoice.class)
    @ApiResponses({
            @ApiResponse(code = 201, message = "Created", response = Invoice.class),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 406, message = "Not acceptable format"),
            @ApiResponse(code = 409, message = "Invoice exists"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @ApiImplicitParam(required = true, name = "invoice", value = "New invoice data", dataType = "Invoice")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> add(@RequestBody(required = false) Invoice invoice) {
        if (invoice == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            if (invoice.getId() != null && invoiceService.invoiceExists(invoice.getId())) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            Invoice addedInvoice = invoiceService.addInvoice(invoice);
            emailService.sendSimpleMessage(addedInvoice);
            return new ResponseEntity<>(addedInvoice, HttpStatus.CREATED);
        } catch (ServiceOperationException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get all invoices", notes = "Get all invoices from database", response = Invoice[].class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = Invoice[].class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll() {
        try {
            return new ResponseEntity<>(invoiceService.getAllInvoices(), HttpStatus.OK);
        } catch (ServiceOperationException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get invoice by ID",
            notes = "Get invoice from database by ID",
            response = Invoice.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = Invoice.class),
            @ApiResponse(code = 404, message = "Invoice not found"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @ApiImplicitParam(required = true, name = "id", value = "Id of the invoice to get", dataType = "Long")
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

    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get invoice by number",
            notes = "Get invoice from database by number",
            response = Invoice.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = Invoice.class),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Invoice not found"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @ApiImplicitParam(required = true, name = "number", value = "Number of the invoice to get", dataType = "String")
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

    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update invoice",
            notes = "Update invoice with provided id",
            response = Invoice.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Updated", response = Invoice.class),
            @ApiResponse(code = 404, message = "Invoice not found"),
            @ApiResponse(code = 406, message = "Not acceptable format"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(required = true, name = "id", value = "Id of invoice to update", dataType = "Long"),
            @ApiImplicitParam(required = true, name = "invoice", value = "Invoice with updated data", dataType = "Invoice")
    })
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
            return new ResponseEntity<>(invoiceService.updateInvoice(invoice), HttpStatus.OK);
        } catch (ServiceOperationException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Remove invoice",
            notes = "Remove invoice by ID")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Removed"),
            @ApiResponse(code = 404, message = "Invoice not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
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

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Remove all invoices",
            notes = "Remove all invoices from database")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Removed"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
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
