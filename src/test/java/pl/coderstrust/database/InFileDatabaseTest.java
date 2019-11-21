package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.coderstrust.configuration.InFileDatabaseProperties;
import pl.coderstrust.helpers.FileHelper;

@ExtendWith(MockitoExtension.class)
class InFileDatabaseTest {

    @Mock
    private FileHelper fileHelper;

    @Mock
    private InFileDatabaseProperties properties;

    @InjectMocks
    private InFileDatabase inFileDatabase;

    private ObjectMapper mapper;
    private AtomicLong nextId;

    @Test
    void saveMethodShouldThrowExceptionForNullAsInvoice() {
        assertThrows(IllegalArgumentException.class, () -> inFileDatabase.save(null));
    }
}