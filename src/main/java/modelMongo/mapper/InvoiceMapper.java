package modelMongo.mapper;

import java.util.Collection;
import modelMongo.InvoiceMongo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.coderstrust.model.Invoice;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface InvoiceMapper {

    InvoiceMapper INVOICE_MAPPER_INSTANCE = Mappers.getMapper(InvoiceMapper.class);

    //@Mapping(target = "withNumber", source = "number")
    //@Mapping(target = "withId", numberFormat = "0L")
    InvoiceMongo invoiceToInvoiceMongo(Invoice invoice);

    Invoice invoiceMongoToInvoice(InvoiceMongo invoiceMongo);

    Collection<InvoiceMongo> invoiceToInvoiceMongo(Collection<Invoice> invoices);
}
