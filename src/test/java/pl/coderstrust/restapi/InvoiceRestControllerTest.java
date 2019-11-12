package pl.coderstrust.restapi;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.doReturn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.services.InvoiceService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import pl.coderstrust.services.ServiceOperationException;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@AutoConfigureMockMvc
public class InvoiceRestControllerTest {

    @Autowired
    private InvoiceRestController invoiceController;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvoiceService invoiceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAllMethodShouldReturnInvoices() throws Exception {
        Invoice invoice1 = InvoiceGenerator.generateRandomInvoice();
        Invoice invoice2 = InvoiceGenerator.generateRandomInvoice();
        Invoice invoice3 = InvoiceGenerator.generateRandomInvoice();
        Collection<Invoice> invoicesToReturn = Arrays.asList(invoice1, invoice2, invoice3);
        doReturn(invoicesToReturn).when(invoiceService).getAllInvoices();

        mockMvc.perform(get("/invoices"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(invoicesToReturn)));

        verify(invoiceService).getAllInvoices();
    }

    @Test
    public void getAllMethodReturnsInternalServerErrorWhenInvoiceServiceThrowsException() throws Exception {
        doThrow(new ServiceOperationException()).when(invoiceService).getAllInvoices();

        mockMvc.perform(get("/invoices"))
                .andExpect(status().isInternalServerError());

        verify(invoiceService).getAllInvoices();
    }

    @Test
    public void shouldSaveNewInvoice() throws Exception {
        Invoice invoiceToAdd = InvoiceGenerator.generateRandomInvoice();
        Invoice addedInvoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(false).when(invoiceService).invoiceExists(invoiceToAdd.getId());
        doReturn(addedInvoice).when(invoiceService).addInvoice(invoiceToAdd);

        mockMvc.perform(
                post("/invoices")
                        .content(toJson(invoiceToAdd))
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated())
                .andExpect(content().json(toJson(addedInvoice)))
                .andReturn();

        verify(invoiceService).addInvoice(invoiceToAdd);
        verify(invoiceService).invoiceExists(invoiceToAdd.getId());
    }

    @Test
    public void addMethodReturnsBadRequestWhenInvoiceToAddIsNull() throws Exception {
        mockMvc.perform(
                post("/invoices")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addMethodReturnsConflictWhenInvoiceAlreadyExists() throws Exception {
        Invoice invoiceToAdd = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(invoiceService).invoiceExists(invoiceToAdd.getId());

        mockMvc.perform(
                post("/invoices")
                        .content(toJson(invoiceToAdd))
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isConflict());

        verify(invoiceService).invoiceExists(invoiceToAdd.getId());
    }

    @Test
    public void addMethodReturnsInternalServerErrorWhenInvoiceServiceThrowsException() throws Exception {
        doThrow(new ServiceOperationException()).when(invoiceService).addInvoice(any());

        mockMvc.perform(
                post("/invoices")
                        .content(toJson(InvoiceGenerator.generateRandomInvoice()))
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isInternalServerError());

        verify(invoiceService).addInvoice(any());
    }

    private String toJson(Invoice invoice) throws JsonProcessingException {
        return objectMapper.writeValueAsString(invoice);
    }

}
