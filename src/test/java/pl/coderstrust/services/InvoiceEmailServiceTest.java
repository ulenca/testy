package pl.coderstrust.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import com.icegreen.greenmail.util.ServerSetupTest;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.apache.catalina.Server;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;

@EnableAutoConfiguration
@SpringBootTest(classes = {InvoiceEmailService.class})
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application-greenmail.properties")
public class InvoiceEmailServiceTest {

    @Autowired
    InvoiceEmailService emailService;

    @Autowired
    Environment environment;

    ServerSetup serverSendingSetup;
    private GreenMail server;

    @BeforeEach
    public void init() {
        server = new GreenMail(ServerSetupTest.ALL);
        server.setUser(
                environment.getProperty("spring.mail.properties.mail.smtp.from"),
                environment.getProperty("spring.mail.username"),
                environment.getProperty("spring.mail.password"));
        server.start();
        System.out.println("Greenmail server started");
    }

    @AfterEach
    public void shutDownServers() {
        server.stop();
    }

    @Test
    public void shouldSendEmail() throws MessagingException {

        Invoice invoice = InvoiceGenerator.generateRandomInvoice();

        emailService.sendSimpleMessage(invoice);

        assertTrue(server.waitForIncomingEmail(5000, 1));
        MimeMessage[] messages = server.getReceivedMessages();
        assertEquals(1, messages.length);
        assertEquals(environment.getProperty("spring.mail.properties.subject"), messages[0].getSubject());
        String emailBody = GreenMailUtil.getBody(messages[0]).replaceAll("=\r?\n", "");
        assertEquals("", emailBody);
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenInvoiceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> emailService.sendSimpleMessage(null));
    }

}
