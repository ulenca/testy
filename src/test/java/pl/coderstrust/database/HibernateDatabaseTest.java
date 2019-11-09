package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;

@ExtendWith(MockitoExtension.class)
class HibernateDatabaseTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private HibernateDatabase database;

    @Test
    void saveMethodShouldThrowExceptionForNullAsInvoice() {
        assertThrows(IllegalArgumentException.class, () -> database.save(null));
        verify(invoiceRepository, never()).save(null);
    }

    @Test
    void shouldSaveInvoice() throws DatabaseOperationException {
        Invoice invoiceToSave = InvoiceGenerator.generateRandomInvoice();
        Invoice savedInvoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(savedInvoice).when(invoiceRepository).save(invoiceToSave);

        Invoice actualInvoice = database.save(invoiceToSave);

        assertEquals(savedInvoice, actualInvoice);
        verify(invoiceRepository).save(invoiceToSave);
    }

    @Test
    void getByIdShouldThrowExceptionForNullAsId() {
        assertThrows(IllegalArgumentException.class, () -> database.getById(null));
        verify(invoiceRepository, never()).findById(null);
    }

    @Test
    void shouldGetInvoiceById() throws DatabaseOperationException {
        Invoice invoiceToGet = InvoiceGenerator.generateRandomInvoice();
        Invoice receivedInvoice = InvoiceGenerator.generateRandomInvoice();
        doReturn(Optional.of(receivedInvoice)).when(invoiceRepository).findById(invoiceToGet.getId());

        Optional<Invoice> actualInvoice = database.getById(invoiceToGet.getId());

        assertTrue(actualInvoice.isPresent());
        assertEquals(receivedInvoice, actualInvoice.get());
        verify(invoiceRepository).findById(invoiceToGet.getId());
    }

    @Test
    void getByNumberShouldThrowExceptionForNullAsNumber() {
        assertThrows(IllegalArgumentException.class, () -> database.getByNumber(null));
        verify(invoiceRepository, never()).findOne(null);
    }

    @ParameterizedTest
    @ValueSource(strings = {"4","12","Fv12/12"})
    void shouldGetInvoiceByNumber(){
        Invoice invoiceToGet = InvoiceGenerator.generateRandomInvoice();
        Invoice getInvoice = InvoiceGenerator.generateRandomInvoice();

    }

    @Test
    void shouldGetAllInvoices(){



    }
}