package pl.coderstrust.services;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import java.io.IOException;
import java.io.InputStream;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;

@EnableAutoConfiguration
@SpringBootTest(classes = {InvoiceEmailServiceImpl.class})
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application-email.properties")
public class InvoiceEmailServiceImplTest {

    @Autowired
    private InvoiceEmailServiceImpl emailService;

    @Autowired
    MailProperties mailProperties;

    @MockBean
    private InvoicePdfService invoicePdfService;

    private GreenMail server;

    @BeforeEach
    public void init() {
        ServerSetup setup = new ServerSetup(mailProperties.getPort(), mailProperties.getHost(), mailProperties.getProtocol());
        server = new GreenMail(setup);
        server.setUser(mailProperties.getUsername(), mailProperties.getUsername(), mailProperties.getPassword());
        server.start();
    }

    @AfterEach
    public void shutDownServers() {
        server.stop();
    }

    @Test
    public void shouldSendEmail() throws MessagingException, ServiceOperationException, IOException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        byte[] invoiceAsPdf = SerializationUtils.serialize(invoice);
        doReturn(invoiceAsPdf).when(invoicePdfService).createPdf(invoice);

        emailService.sendEmailWithInvoice(invoice);

        assertTrue(server.waitForIncomingEmail(5000, 1));
        MimeMessage[] messages = server.getReceivedMessages();
        assertEquals(1, messages.length);
        assertEquals(mailProperties.getProperties().get("subject"), messages[0].getSubject());
        MimeMultipart mail = (MimeMultipart) messages[0].getContent();

        String emailBody = (String) ((MimeMultipart) mail.getBodyPart(0).getContent()).getBodyPart(0).getContent();
        assertEquals(mailProperties.getProperties().get("content"), emailBody);

        BodyPart attachment = mail.getBodyPart(1);
        assertTrue(attachment.getContentType().equalsIgnoreCase(String.format("application/pdf; name=%s.pdf", invoice.getNumber())));
        InputStream attachmentStream = (InputStream) attachment.getContent();
        byte[] pdf = IOUtils.toByteArray(attachmentStream);
        assertArrayEquals(invoiceAsPdf, pdf);
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenInvoiceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> emailService.sendEmailWithInvoice(null));
    }
}
