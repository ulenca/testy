package pl.coderstrust.services;

public interface EmailService {
    void sendSimpleMessage(String to, String from, String subject, String text);
}
