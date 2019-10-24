package pl.coderstrust.services;

public class InvoiceOperationException extends Exception {
    public InvoiceOperationException() {
        super();
    }

    public InvoiceOperationException(String message) {
        super(message);
    }

    public InvoiceOperationException(Throwable cause) {
        super(cause);
    }

    public InvoiceOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
