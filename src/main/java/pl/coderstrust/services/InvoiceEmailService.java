package pl.coderstrust.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.coderstrust.model.Invoice;

@Service
public class InvoiceEmailService implements EmailService {

    public JavaMailSender emailSender;

    @Autowired
    private Environment env;

    @Autowired
    public InvoiceEmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Async
    public void sendSimpleMessage(Invoice invoice) {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null!");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(env.getProperty("spring.mail.properties.to"));
        message.setFrom(env.getProperty("spring.mail.properties.from"));
        message.setSubject(env.getProperty("spring.mail.properties.subject"));
        message.setText("");
        emailSender.send(message);
    }
}
