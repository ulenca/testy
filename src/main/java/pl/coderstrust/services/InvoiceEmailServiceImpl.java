package pl.coderstrust.services;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.coderstrust.model.Invoice;

@Service
public class InvoiceEmailServiceImpl implements InvoiceEmailService {

    private JavaMailSender emailSender;
    private MailProperties mailProperties;
    private InvoicePdfService invoicePdfService;

    @Autowired
    public InvoiceEmailServiceImpl(JavaMailSender emailSender, MailProperties mailProperties, InvoicePdfService invoicePdfService) {
        this.emailSender = emailSender;
        this.mailProperties = mailProperties;
        this.invoicePdfService = invoicePdfService;
    }

    @Async
    public void sendEmailWithInvoice(Invoice invoice) {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null!");
        }
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(mailProperties.getProperties().get("to"));
            helper.setFrom(mailProperties.getUsername());
            helper.setSubject(mailProperties.getProperties().get("subject"));
            helper.setText(mailProperties.getProperties().get("content"));
            helper.addAttachment(String.format("%s.pdf", invoice.getNumber()), new ByteArrayResource(invoicePdfService.createPdf(invoice)));
            emailSender.send(message);
        } catch (MessagingException | ServiceOperationException e) {
            e.printStackTrace();
        }
    }
}
