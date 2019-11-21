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
    void saveMethodShouldThrowExceptionForNullAsInvoice() {
        assertThrows(IllegalArgumentException.class, () -> database.save(null));
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