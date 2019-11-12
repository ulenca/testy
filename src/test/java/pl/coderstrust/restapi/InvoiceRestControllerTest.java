package pl.coderstrust.restapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import java.util.Optional;
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
        Collection<Invoice> invoicesToReturn = InvoiceGenerator.generateRandomInvoices(3);
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

    @Test
    public void getByIdMethodShouldReturnInvoice() throws Exception {
        Invoice invoiceToReturn = InvoiceGenerator.generateRandomInvoice();
        doReturn(Optional.of(invoiceToReturn)).when(invoiceService).getById(invoiceToReturn.getId());

        mockMvc.perform(get("/invoices?id=" + invoiceToReturn.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(invoiceToReturn)));

        verify(invoiceService).getById(invoiceToReturn.getId());
    }

    @Test
    public void getByIdMethodReturnsBedRequestWhenIdIsNull() throws Exception {
        mockMvc.perform(get("/invoices?id=" + null))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getByIdMethodReturnsInternalServerErrorWhenInvoiceServiceThrowsException() throws Exception {
        doThrow(new ServiceOperationException()).when(invoiceService).getById(anyLong());

        mockMvc.perform(get("/invoices?id=" + anyLong()))
                .andExpect(status().isInternalServerError());

        verify(invoiceService).getById(anyLong());
    }

    @Test
    public void getByNumberMethodShouldReturnInvoice() throws Exception {
        Invoice invoiceToReturn = InvoiceGenerator.generateRandomInvoice();
        doReturn(Optional.of(invoiceToReturn)).when(invoiceService).getByNumber(invoiceToReturn.getNumber());

        mockMvc.perform(get("/invoices?number=" + invoiceToReturn.getNumber()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(invoiceToReturn)));

        verify(invoiceService).getByNumber(invoiceToReturn.getNumber());
    }

    @Test
    public void getByNumberMethodReturnsBedRequestWhenNumberIsNull() throws Exception {
        mockMvc.perform(get("/invoices?number="))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getByNumberMethodReturnsInternalServerErrorWhenInvoiceServiceThrowsException() throws Exception {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doThrow(new ServiceOperationException()).when(invoiceService).getByNumber(invoice.getNumber());

        mockMvc.perform(get("/invoices?number=" + invoice.getNumber()))
                .andExpect(status().isInternalServerError());

        verify(invoiceService).getByNumber(invoice.getNumber());
    }

    @Test
    public void shouldUpdateInvoice() throws Exception {
        Invoice invoiceToUpdate = InvoiceGenerator.generateRandomInvoice();
        Invoice updatedInvoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(invoiceService).invoiceExists(invoiceToUpdate.getId());
        doReturn(updatedInvoice).when(invoiceService).updateInvoice(invoiceToUpdate);

        mockMvc.perform(
                put("/invoices")
                        .content(toJson(invoiceToUpdate))
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(updatedInvoice)))
                .andReturn();

        verify(invoiceService).updateInvoice(invoiceToUpdate);
        verify(invoiceService).invoiceExists(invoiceToUpdate.getId());
    }

    @Test
    public void updateMethodReturnsBadRequestWhenInvoiceToAddIsNull() throws Exception {
        mockMvc.perform(
                post("/invoices")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateMethodReturnsConflictWhenInvoiceDoesNotExist() throws Exception {
        Invoice invoiceToUpdate = InvoiceGenerator.generateRandomInvoice();
        doReturn(false).when(invoiceService).invoiceExists(invoiceToUpdate.getId());

        mockMvc.perform(
                put("/invoices")
                        .content(toJson(invoiceToUpdate))
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isConflict());

        verify(invoiceService).invoiceExists(invoiceToUpdate.getId());
    }

    @Test
    public void updateMethodReturnsInternalServerErrorWhenInvoiceServiceThrowsException() throws Exception {
        doReturn(true).when(invoiceService).invoiceExists(anyLong());
        doThrow(new ServiceOperationException()).when(invoiceService).updateInvoice(any());

        mockMvc.perform(
                put("/invoices")
                        .content(toJson(InvoiceGenerator.generateRandomInvoice()))
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isInternalServerError());

        verify(invoiceService).updateInvoice(any());
        verify(invoiceService).invoiceExists(anyLong());
    }

    @Test
    public void shouldDeleteInvoice() throws Exception {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(invoiceService).invoiceExists(invoice.getId());
        doNothing().when(invoiceService).deleteInvoiceById(invoice.getId());

        mockMvc.perform(
                delete("/invoices?id=" + invoice.getId()))
                .andExpect(status().isOk());

        verify(invoiceService).invoiceExists(invoice.getId());
        verify(invoiceService).deleteInvoiceById(invoice.getId());
    }

    @Test
    public void deleteByIdShouldReturnBadRequestWhenIdIsNull() throws Exception {
        mockMvc.perform(
                delete("/invoices?id=" + null))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteByIdShouldReturnConflictWhenInvoiceDoesNotExist() throws Exception {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(false).when(invoiceService).invoiceExists(invoice.getId());

        mockMvc.perform(
                delete("/invoices?id=" + invoice.getId()))
                .andExpect(status().isConflict());

        verify(invoiceService).invoiceExists(invoice.getId());
    }

    @Test
    public void deleteByIdShouldThrowInternalServerErrorWhenInvoiceServiceThrowsError() throws Exception {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doThrow(new ServiceOperationException()).when(invoiceService).deleteInvoiceById(invoice.getId());
        doReturn(true).when(invoiceService).invoiceExists(invoice.getId());

        mockMvc.perform(
                delete("/invoices?id=" + invoice.getId()))
                .andExpect(status().isInternalServerError());

        verify(invoiceService).deleteInvoiceById(invoice.getId());
    }

    @Test
    public void shouldDeleteAllInvoices() throws Exception {
        doNothing().when(invoiceService).deleteAllInvoices();

        mockMvc.perform(
                delete("/invoices"))
                .andExpect(status().isOk());

        verify(invoiceService).deleteAllInvoices();
    }

    @Test
    public void deleteAllInvoicesShouldThrowInternalServerErrorWhenInvoiceServiceThrowsError() throws Exception {
        doThrow(new ServiceOperationException()).when(invoiceService).deleteAllInvoices();

        mockMvc.perform(
                delete("/invoices"))
                .andExpect(status().isInternalServerError());

        verify(invoiceService).deleteAllInvoices();
    }

}
