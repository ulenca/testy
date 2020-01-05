package pl.coderstrust.modelMongo.mapper;

import java.util.Collection;
import org.mapstruct.Mapping;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.modelMongo.CompanyMongo;
import pl.coderstrust.modelMongo.InvoiceEntryMongo;
import pl.coderstrust.modelMongo.InvoiceMongo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import pl.coderstrust.model.Invoice;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface InvoiceMapper {

    InvoiceMapper INVOICE_MAPPER_INSTANCE = Mappers.getMapper(InvoiceMapper.class);

    @Mapping(target = "withNumber", source = "number")
    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withIssuedDate", source = "issuedDate")
    @Mapping(target = "withDueDate", source = "dueDate")
    @Mapping(target = "withSeller", source = "seller")
    @Mapping(target = "withBuyer", source = "buyer")
    @Mapping(target = "withEntries", source = "entries")
    InvoiceMongo invoiceToInvoiceMongo(Invoice invoice);

    @Mapping(target = "withNumber", source = "number")
    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withIssuedDate", source = "issuedDate")
    @Mapping(target = "withDueDate", source = "dueDate")
    @Mapping(target = "withSeller", source = "seller")
    @Mapping(target = "withBuyer", source = "buyer")
    @Mapping(target = "withEntries", source = "entries")
    Invoice invoiceMongoToInvoice(InvoiceMongo invoiceMongo);

    Collection<Invoice> invoicesMongoToInvoices(Collection<InvoiceMongo> invoices);

    @Mapping(target = "withName", source = "name")
    @Mapping(target = "withAddress", source = "address")
    @Mapping(target = "withTaxId", source = "taxId")
    @Mapping(target = "withAccountNumber", source = "accountNumber")
    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withPhoneNumber", source = "phoneNumber")
    @Mapping(target = "withEmail", source = "email")
    CompanyMongo companyToCompanyMongo (Company company);

    @Mapping(target = "withName", source = "name")
    @Mapping(target = "withAddress", source = "address")
    @Mapping(target = "withTaxId", source = "taxId")
    @Mapping(target = "withAccountNumber", source = "accountNumber")
    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withPhoneNumber", source = "phoneNumber")
    @Mapping(target = "withEmail", source = "email")
    Company companyMongoToCompany(CompanyMongo companyMongo);

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withDescription", source = "description")
    @Mapping(target = "withQuantity", source = "quantity")
    @Mapping(target = "withPrice", source = "price")
    @Mapping(target = "withNetValue", source = "netValue")
    @Mapping(target = "withGrossValue", source = "grossValue")
    @Mapping(target = "withVatRate", source = "vatRate")
    InvoiceEntryMongo invoiceEntryToInvoiceEntryMongo (InvoiceEntry invoiceEntry);

    @Mapping(target = "withId", source = "id")
    @Mapping(target = "withDescription", source = "description")
    @Mapping(target = "withQuantity", source = "quantity")
    @Mapping(target = "withPrice", source = "price")
    @Mapping(target = "withNetValue", source = "netValue")
    @Mapping(target = "withGrossValue", source = "grossValue")
    @Mapping(target = "withVatRate", source = "vatRate")
    InvoiceEntry invoiceEntryMongoToInvoiceEntry (InvoiceEntryMongo invoiceEntryMongo);
}
