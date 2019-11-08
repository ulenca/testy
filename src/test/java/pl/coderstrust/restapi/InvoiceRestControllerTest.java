package pl.coderstrust.restapi;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.doReturn;

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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.services.InvoiceService;

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
        mockMvc
                .perform(get("/invoices"))
                .andDo(print())
                .andExpect(status().isOk());
   //             .andExpect(jsonPath("$"), hasSize(0));
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
               .contentType(MediaType.APPLICATION_JSON_UTF8)
       ).andExpect(status().isCreated());

        verify(invoiceService).addInvoice(invoiceToAdd);
        verify(invoiceService).invoiceExists(invoiceToAdd.getId());


    }

    private String toJson(Invoice invoice) throws JsonProcessingException {
        return objectMapper.writeValueAsString(invoice);
    }

}
