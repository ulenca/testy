package pl.coderstrust.database;

import java.util.Collection;
import java.util.Optional;
import pl.coderstrust.model.Invoice;

public interface Database {

    void save(Invoice invoice);

    Optional<Invoice> getById(Long id);

    Optional<Invoice> getByNumber(Long number);

    Collection<Invoice> getAll();

    void delete(Invoice invoice);

    void deleteAll();

    boolean exists(Long id);

    long count();
}
