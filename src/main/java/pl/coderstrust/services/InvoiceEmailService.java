package pl.coderstrust.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class InvoiceEmailService implements EmailService {

    @Autowired
    public JavaMailSender emailSender;

    @Async
    public void sendSimpleMessage(String to, String from, String subject, String text) {
        if (to == null || subject == null || text == null) {
            throw new IllegalArgumentException("Recipient, subject and content cannot be null!");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(from);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}
