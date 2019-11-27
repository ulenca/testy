package pl.coderstrust.services;

import pl.coderstrust.model.Invoice;

public interface EmailService {
    void sendSimpleMessage(Invoice invoice);
}
