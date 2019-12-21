package pl.coderstrust.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
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
import pl.coderstrust.services.InvoiceEmailService;
import pl.coderstrust.services.InvoicePdfService;
import pl.coderstrust.services.InvoiceService;
import pl.coderstrust.services.ServiceOperationException;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@AutoConfigureMockMvc
public class InvoiceControllerTest {

    @Autowired
    private InvoiceController invoiceController;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvoiceService invoiceService;

    @MockBean
    private InvoicePdfService pdfService;

    @MockBean
    private InvoiceEmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldReturnAllInvoices() throws Exception {
        Collection<Invoice> invoicesToReturn = InvoiceGenerator.generateRandomInvoices(3);
        doReturn(invoicesToReturn).when(invoiceService).getAllInvoices();

        mockMvc.perform(get("/invoices"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(invoicesToReturn)));

        verify(invoiceService).getAllInvoices();
    }

    @Test
    public void shouldReturnEmptyListOfInvoicesWhenThereAreNoInvoicesInTheDatabase() throws Exception {
        Collection<Invoice> emptyList = new ArrayList<>();
        doReturn(emptyList).when(invoiceService).getAllInvoices();

        mockMvc.perform(get("/invoices"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(emptyList)));

        verify(invoiceService).getAllInvoices();
    }

    @Test
    public void shouldReturnInternalServerErrorStatusDuringGettingAllInvoicesWhenSomethingWentWrongOnServer() throws Exception {
        doThrow(new ServiceOperationException()).when(invoiceService).getAllInvoices();

        mockMvc.perform(get("/invoices"))
                .andExpect(status().isInternalServerError());

        verify(invoiceService).getAllInvoices();
    }

    @Test
    public void shouldAddInvoice() throws Exception {
        Invoice invoiceToAdd = InvoiceGenerator.generateRandomInvoice();
        Invoice addedInvoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(false).when(invoiceService).invoiceExists(invoiceToAdd.getId());
        doReturn(addedInvoice).when(invoiceService).addInvoice(invoiceToAdd);
        doNothing().when(emailService).sendEmailWithInvoice(addedInvoice);

        mockMvc.perform(
                post("/invoices")
                        .content(objectMapper.writeValueAsString(invoiceToAdd))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(addedInvoice)))
                .andReturn();

        verify(invoiceService).addInvoice(invoiceToAdd);
        verify(invoiceService).invoiceExists(invoiceToAdd.getId());
        verify(emailService).sendEmailWithInvoice(addedInvoice);
    }

    @Test
    public void shouldReturnBadRequestStatusDuringAddingInvoiceWhenInvoiceIsNull() throws Exception {
        mockMvc.perform(
                post("/invoices")
                        .content(objectMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(invoiceService, never()).invoiceExists(anyLong());
        verify(invoiceService, never()).addInvoice(any());
    }

    @Test
    public void shouldReturnConflictStatusDuringAddingInvoiceWhenInvoiceAlreadyExist() throws Exception {
        Invoice invoiceToAdd = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(invoiceService).invoiceExists(invoiceToAdd.getId());

        mockMvc.perform(
                post("/invoices")
                        .content(objectMapper.writeValueAsString(invoiceToAdd))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        verify(invoiceService).invoiceExists(invoiceToAdd.getId());
        verify(invoiceService, never()).addInvoice(invoiceToAdd);
    }

    @Test
    public void shouldReturnInternalServerErrorStatusDuringAddingInvoiceWhenSomethingWentWrongOnServer() throws Exception {
        doThrow(new ServiceOperationException()).when(invoiceService).addInvoice(any());
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();

        mockMvc.perform(
                post("/invoices")
                        .content(objectMapper.writeValueAsString(invoice))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(invoiceService).addInvoice(invoice);
    }

    @Test
    public void shouldReturnInvoice() throws Exception {
        Invoice invoiceToReturn = InvoiceGenerator.generateRandomInvoice();
        doReturn(Optional.of(invoiceToReturn)).when(invoiceService).getById(invoiceToReturn.getId());

        mockMvc.perform(get("/invoices/{id}", invoiceToReturn.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(invoiceToReturn)));

        verify(invoiceService).getById(invoiceToReturn.getId());
    }

    @Test
    public void shouldReturnInternalServerErrorStatusDuringGettingInvoiceWhenSomethingWentWrongOnServer() throws Exception {
        doThrow(new ServiceOperationException()).when(invoiceService).getById(1L);

        mockMvc.perform(get("/invoices/1"))
                .andExpect(status().isInternalServerError());

        verify(invoiceService).getById(1L);
    }

    @Test
    public void shouldReturnNotFoundStatusDuringGettingInvoiceWhenInvoiceWithSpecificIdDoesNotExist() throws Exception {
        doReturn(Optional.empty()).when(invoiceService).getById(1L);

        mockMvc.perform(get("/invoices/1"))
                .andExpect(status().isNotFound());

        verify(invoiceService).getById(1L);
    }

    @Test
    public void shouldReturnInvoiceByNumber() throws Exception {
        Invoice invoiceToReturn = InvoiceGenerator.generateRandomInvoice();
        doReturn(Optional.of(invoiceToReturn)).when(invoiceService).getByNumber(invoiceToReturn.getNumber());

        mockMvc.perform(get("/invoices/byNumber?number={number}", invoiceToReturn.getNumber()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(invoiceToReturn)));

        verify(invoiceService).getByNumber(invoiceToReturn.getNumber());
    }

    @Test
    public void shouldReturnBadRequestStatusDuringGettingInvoiceByNumberWhenNumberIsNull() throws Exception {
        mockMvc.perform(get("/invoices/byNumber"))
                .andExpect(status().isBadRequest());

        verify(invoiceService, never()).getByNumber(anyString());
    }


    @Test
    public void shouldReturnInternalServerErrorStatusDuringGettingInvoiceByNumberWhenSomethingWentWrongOnServer() throws Exception {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doThrow(new ServiceOperationException()).when(invoiceService).getByNumber(invoice.getNumber());

        mockMvc.perform(get("/invoices/byNumber?number={number}", invoice.getNumber()))
                .andExpect(status().isInternalServerError());

        verify(invoiceService).getByNumber(invoice.getNumber());
    }

    @Test
    public void shouldReturnNotFoundStatusDuringGettingInvoiceByNumberWhenInvoiceWithSpecificNumberDoesNotExist() throws Exception {
        doReturn(Optional.empty()).when(invoiceService).getByNumber("1111");

        mockMvc.perform(get("/invoices/byNumber?number=1111"))
                .andExpect(status().isNotFound());

        verify(invoiceService).getByNumber("1111");
    }

    @Test
    public void shouldUpdateInvoice() throws Exception {
        Invoice invoiceToUpdate = InvoiceGenerator.generateRandomInvoice();
        Invoice updatedInvoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(invoiceService).invoiceExists(invoiceToUpdate.getId());
        doReturn(updatedInvoice).when(invoiceService).updateInvoice(invoiceToUpdate);

        mockMvc.perform(
                put("/invoices/{id}", invoiceToUpdate.getId())
                        .content(objectMapper.writeValueAsString(invoiceToUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedInvoice)))
                .andReturn();

        verify(invoiceService).updateInvoice(invoiceToUpdate);
        verify(invoiceService).invoiceExists(invoiceToUpdate.getId());
    }

    @Test
    public void shouldReturnBadRequestStatusDuringUpdatingInvoiceWhenInvoiceIsNull() throws Exception {
        mockMvc.perform(
                put("/invoices/5")
                        .content(objectMapper.writeValueAsString(null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(invoiceService, never()).updateInvoice(any());
        verify(invoiceService, never()).invoiceExists(5L);

    }

    @Test
    public void shouldReturnBadRequestStatusDuringUpdatingInvoiceWithWrongId() throws Exception {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();

        mockMvc.perform(
                put("/invoices/{id}", invoice.getId() + 1)
                        .content(objectMapper.writeValueAsString(invoice))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(invoiceService, never()).updateInvoice(any());
        verify(invoiceService, never()).invoiceExists(anyLong());
    }

    @Test
    public void shouldReturnNotFoundStatusDuringUpdatingNonExistingInvoice() throws Exception {
        Invoice invoiceToUpdate = InvoiceGenerator.generateRandomInvoice();
        doReturn(false).when(invoiceService).invoiceExists(invoiceToUpdate.getId());

        mockMvc.perform(
                put("/invoices/{id}", invoiceToUpdate.getId())
                        .content(objectMapper.writeValueAsString(invoiceToUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(invoiceService).invoiceExists(invoiceToUpdate.getId());
        verify(invoiceService, never()).updateInvoice(invoiceToUpdate);
    }

    @Test
    public void shouldReturnInternalServerErrorStatusDuringUpdatingInvoiceWhenSomethingWentWrongOnServer() throws Exception {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(invoiceService).invoiceExists(invoice.getId());
        doThrow(new ServiceOperationException()).when(invoiceService).updateInvoice(invoice);

        mockMvc.perform(
                put("/invoices/{id}", invoice.getId())
                        .content(objectMapper.writeValueAsString(invoice))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(invoiceService).updateInvoice(invoice);
        verify(invoiceService).invoiceExists(invoice.getId());
    }

    @Test
    public void shouldDeleteInvoice() throws Exception {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(true).when(invoiceService).invoiceExists(invoice.getId());
        doNothing().when(invoiceService).deleteInvoiceById(invoice.getId());

        mockMvc.perform(
                delete("/invoices/{id}", invoice.getId()))
                .andExpect(status().isNoContent());

        verify(invoiceService).invoiceExists(invoice.getId());
        verify(invoiceService).deleteInvoiceById(invoice.getId());
    }

    @Test
    public void shouldReturnNotFoundStatusDuringRemovingNonExistingInvoice() throws Exception {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(false).when(invoiceService).invoiceExists(invoice.getId());

        mockMvc.perform(
                delete("/invoices/{id}", invoice.getId()))
                .andExpect(status().isNotFound());

        verify(invoiceService).invoiceExists(invoice.getId());
    }

    @Test
    public void shouldReturnInternalServerErrorStatusDuringRemovingInvoiceWhenSomethingWentWrongOnServer() throws Exception {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doThrow(new ServiceOperationException()).when(invoiceService).deleteInvoiceById(invoice.getId());
        doReturn(true).when(invoiceService).invoiceExists(invoice.getId());

        mockMvc.perform(
                delete("/invoices/{id}", invoice.getId()))
                .andExpect(status().isInternalServerError());

        verify(invoiceService).invoiceExists(invoice.getId());
        verify(invoiceService).deleteInvoiceById(invoice.getId());
    }

    @Test
    public void shouldDeleteAllInvoices() throws Exception {
        doNothing().when(invoiceService).deleteAllInvoices();

        mockMvc.perform(
                delete("/invoices"))
                .andExpect(status().isNoContent());

        verify(invoiceService).deleteAllInvoices();
    }

    @Test
    public void shouldReturnInternalServerErrorStatusDuringRemovingAllInvoicesWhenSomethingWentWrongOnServer() throws Exception {
        doThrow(new ServiceOperationException()).when(invoiceService).deleteAllInvoices();

        mockMvc.perform(
                delete("/invoices"))
                .andExpect(status().isInternalServerError());

        verify(invoiceService).deleteAllInvoices();
    }

    @Test
    public void shouldReturnInvoiceAsPdf() throws Exception {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(Optional.of(invoice)).when(invoiceService).getById(invoice.getId());
        byte[] invoiceAsPdf = "blablabla".getBytes();
        doReturn(invoiceAsPdf).when(pdfService).createPdf(invoice);

        mockMvc.perform(get("/invoices/pdf/{id}", invoice.getId()))
                .andExpect(content().bytes(invoiceAsPdf))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(status().isOk());

        verify(invoiceService).getById(invoice.getId());
        verify(pdfService).createPdf(invoice);
    }

    @Test
    public void shouldReturnInternalServerErrorStatusDuringGettingInvoiceAsPdfWhenSomethingWentWrongOnServer() throws Exception {
        doThrow(new ServiceOperationException()).when(invoiceService).getById(1L);

        mockMvc.perform(
                get("/invoices/pdf/1"))
                .andExpect(status().isInternalServerError());

        verify(invoiceService).getById(1L);
        verify(pdfService, never()).createPdf(any());
    }

    @Test
    public void shouldReturnNotFoundStatusDuringGettingInvoiceAsPdfWhenInvoiceWithSpecificIdDoesNotExist() throws Exception {
        doReturn(Optional.empty()).when(invoiceService).getById(1L);

        mockMvc.perform(get("/invoices/pdf/1"))
                .andExpect(status().isNotFound());

        verify(invoiceService).getById(1L);
        verify(pdfService, never()).createPdf(any());
    }
}
