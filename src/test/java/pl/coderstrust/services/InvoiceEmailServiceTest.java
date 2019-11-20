package pl.coderstrust.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@EnableAutoConfiguration
@SpringBootTest(classes = {InvoiceEmailService.class})
@ExtendWith(SpringExtension.class)
public class InvoiceEmailServiceTest {

    @Autowired
    InvoiceEmailService emailService;

    private final GreenMail greenMail = new GreenMail(ServerSetup.ALL);

    @Test
    public void shouldSendEmail() throws MessagingException {
        String to = "to@localhost.com";
        String from = "from@localhost.com";
        String subject = "New invoice added";
        String body = "Invoice data";

        greenMail.start();
        emailService.sendSimpleMessage(to, from, subject, body);

        assertTrue(greenMail.waitForIncomingEmail(5000, 1));
        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length);
        assertEquals(subject, messages[0].getSubject());
        String emailBody = GreenMailUtil.getBody(messages[0]).replaceAll("=\r?\n", "");
        assertEquals(body, emailBody);
        greenMail.stop();
    }
}
