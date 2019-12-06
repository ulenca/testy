package pl.coderstrust.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import com.icegreen.greenmail.util.ServerSetupTest;
import java.io.IOException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
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

    private GreenMail server;

    @BeforeEach
    public void init() {
        ServerSetup setup = new ServerSetup(mailProperties.getPort(), mailProperties.getHost(), mailProperties.getProtocol());
        server = new GreenMail(setup);
        server.setUser(mailProperties.getUsername(), mailProperties.getUsername(), mailProperties.getPassword());
        server.start();
        System.out.println("Greenmail server started");
    }

    @AfterEach
    public void shutDownServers() {
        server.stop();
    }

    @Test
    public void shouldSendEmail() throws MessagingException, IOException {
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();

        emailService.sendEmailWithInvoice(invoice);

        assertTrue(server.waitForIncomingEmail(5000, 1));
        MimeMessage[] messages = server.getReceivedMessages();
        assertEquals(1, messages.length);
        assertEquals(mailProperties.getProperties().get("subject"), messages[0].getSubject());
        String emailBody = GreenMailUtil.getBody(messages[0]).replaceAll("=\r?\n", "");
        assertEquals(mailProperties.getProperties().get("content"), emailBody);
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenInvoiceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> emailService.sendEmailWithInvoice(null));
    }
}
