package pl.coderstrust.restapi;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.doReturn;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.services.InvoiceService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@ExtendWith(SpringExtension.class)
//@SpringBootTest
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
        String expectedJson = objectMapper.writeValueAsString(invoicesToReturn);
        doReturn(invoicesToReturn).when(invoiceService).getAllInvoices();

        MvcResult result = mockMvc
                .perform(get("/invoices"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].number", is(invoice1.getNumber())))
                //.andExpect(jsonPath("$", is(objectMapper.readTree(expectedJson))))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals(expectedJson, content);

        verify(invoiceService).getAllInvoices();
    }

    @Test
    public void shouldSaveNewInvoice() throws Exception {
        Invoice invoiceToAdd = InvoiceGenerator.generateRandomInvoice();
        Invoice addedInvoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(false).when(invoiceService).invoiceExists(invoiceToAdd.getId());
        doReturn(addedInvoice).when(invoiceService).addInvoice(invoiceToAdd);

        MvcResult result = mockMvc.perform(
                post("/invoices")
               .content(toJson(invoiceToAdd))
               .contentType(MediaType.APPLICATION_JSON_UTF8)
       )
                .andDo(print())
                .andExpect(status().isCreated())
                //.andExpect(jsonPath("$", is(objectMapper.readTree(toJson(addedInvoice)))));
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals(toJson(addedInvoice), content);

        verify(invoiceService).addInvoice(invoiceToAdd);
        verify(invoiceService).invoiceExists(invoiceToAdd.getId());
    }

    private String toJson(Invoice invoice) throws JsonProcessingException {
        return objectMapper.writeValueAsString(invoice);
    }

}
