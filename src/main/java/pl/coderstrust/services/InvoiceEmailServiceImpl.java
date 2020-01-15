package pl.coderstrust.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.coderstrust.model.Invoice;

@Service
public class InvoiceEmailServiceImpl implements InvoiceEmailService {

    private static Logger log = LoggerFactory.getLogger(InvoiceEmailServiceImpl.class);

    private JavaMailSender emailSender;
    private MailProperties mailProperties;

    @Autowired
    public InvoiceEmailServiceImpl(JavaMailSender emailSender, MailProperties mailProperties) {
        this.emailSender = emailSender;
        this.mailProperties = mailProperties;
    }

    @Async
    public void sendEmailWithInvoice(Invoice invoice) {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null!");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailProperties.getProperties().get("to"));
        message.setFrom(mailProperties.getUsername());
        message.setSubject(mailProperties.getProperties().get("subject"));
        message.setText(mailProperties.getProperties().get("content"));
        log.debug(String.format("Sending email with invoice: %s", invoice));
        emailSender.send(message);
    }
}
