package pl.coderstrust.database;

public class DatabaseOperationException extends Exception {
    public DatabaseOperationException(String errorMessage) {
        super(errorMessage);
    }
}
