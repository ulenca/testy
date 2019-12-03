package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.coderstrust.configuration.ApplicationConfiguration;
import pl.coderstrust.configuration.InFileDatabaseProperties;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.helpers.FileHelper;

@ExtendWith(MockitoExtension.class)
class InFileDatabaseTest {

    @Mock
    private FileHelper fileHelper;

    @Mock
    private InFileDatabaseProperties properties;

    private ObjectMapper objectMapper;

    private InFileDatabase database;

    @BeforeEach
    void setup() throws IOException {
        objectMapper = new ApplicationConfiguration().getObjectMapper();
        database = new InFileDatabase(fileHelper, objectMapper, properties);
    }

    @Test
    void saveMethodShouldThrowExceptionForNullAsInvoice() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> database.save(null));
    }

    @Test
    void shouldSaveInvoice() {
    }

    @Test
    void saveInvoicesShouldThrowExceptionWhenAnErrorOccursDuringSavingInvoices() {
    }

    @Test
    void getByIdMethodShouldThrowExceptionForNullAsId() {
        assertThrows(IllegalArgumentException.class, () -> database.getById(null));
    }

    @Test
    void getByIdMethodShouldThrowExceptionWhenAnErrorOccurDuringGettingInvoice() {
    }

    @Test
    void shouldReturnInvoiceById() {
    }

    @Test
    void getByNumberMethodShouldThrowExceptionForNullAsNumber() {
    }

    @Test
    void getByNumberMethodShouldThrowExceptionWhenAnErrorOccurDuringGettingInvoiceByNumber() {
    }

    @Test
    void shouldReturnInvoiceByNumber() {
    }

    @Test
    void getAllMethodShouldThrowExceptionWhenAnErrorOccursDuringGettingAllInvoicesFromDatabase() {
    }

    @Test
    void shouldReturnAllInvoices() {
    }

    @Test
    void deleteMethodShouldThrownExceptionForNullAsId() {
    }

    @Test
    void deleteMethodShouldThrowExceptionWhenInvoiceDoesNotExist() {
    }

    @Test
    void deleteMethodShouldThrowExceptionWhenAnErrorOccurDuringDeletingInvoiceByIdFromDatabase() {
    }

    @Test
    void shouldDeleteInvoice() {
    }

    @Test
    void deleteAllMethodShouldThrowExceptionWhenAnErrorOccurDuringDeletingAllInvoices() {
    }

    @Test
    void shouldDeleteAllInvoices() {
    }

    @Test
    void shouldReturnTrueIfInvoiceExist() {
    }

    @Test
    void shouldReturnFalseIfInvoiceDoesNotExist() {
    }

    @Test
    void existMethodShouldThrowExceptionForNullAsId() {
    }

    @Test
    void existsMethodShouldThrowExceptionWhenAnErrorOccurDuringCheckingInvoiceExist() {
    }

    @Test
    void countMethodShouldThrowExceptionWhenAnErrorOccurDuringCountingInvoices() {

    }

    @Test
    void shouldReturnNumberOfInvoices() throws IOException, DatabaseOperationException {
        List<String> invoices = List.of(objectMapper.writeValueAsString(InvoiceGenerator.generateRandomInvoice())
            , objectMapper.writeValueAsString(InvoiceGenerator.generateRandomInvoice())
            , objectMapper.writeValueAsString(InvoiceGenerator.generateRandomInvoice()));
        when(fileHelper.readLines(properties.getFilePath())).thenReturn(invoices);

        long result = database.count();

        assertEquals(invoices.size(), result);
    }
}