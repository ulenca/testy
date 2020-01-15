package pl.coderstrust.mongomodel.mapper;

import java.util.Collection;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.Vat;
import pl.coderstrust.mongomodel.CompanyMongo;
import pl.coderstrust.mongomodel.InvoiceEntryMongo;
import pl.coderstrust.mongomodel.InvoiceMongo;
import pl.coderstrust.mongomodel.VatMongo;


@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface InvoiceMapper {

    @Mapping(target = "withMongoId", constant = "")
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

    Collection<InvoiceMongo> invoicesToInvoicesMongo(Collection<Invoice> invoices);

    @Mapping(target = "withName", source = "name")
    @Mapping(target = "withAddress", source = "address")
    @Mapping(target = "withTaxId", source = "taxId")
    @Mapping(target = "withAccountNumber", source = "accountNumber")
    @Mapping(target = "withPhoneNumber", source = "phoneNumber")
    @Mapping(target = "withEmail", source = "email")
    CompanyMongo companyToCompanyMongo(Company company);

    @Mapping(target = "withId", constant = "0L")
    @Mapping(target = "withName", source = "name")
    @Mapping(target = "withAddress", source = "address")
    @Mapping(target = "withTaxId", source = "taxId")
    @Mapping(target = "withAccountNumber", source = "accountNumber")
    @Mapping(target = "withPhoneNumber", source = "phoneNumber")
    @Mapping(target = "withEmail", source = "email")
    Company companyMongoToCompany(CompanyMongo companyMongo);

    @Mapping(target = "withDescription", source = "description")
    @Mapping(target = "withQuantity", source = "quantity")
    @Mapping(target = "withPrice", source = "price")
    @Mapping(target = "withNetValue", source = "netValue")
    @Mapping(target = "withGrossValue", source = "grossValue")
    @Mapping(target = "withVatRate", source = "vatRate")
    InvoiceEntryMongo invoiceEntryToInvoiceEntryMongo(InvoiceEntry invoiceEntry);

    @Mapping(target = "withId", constant = "0L")
    @Mapping(target = "withDescription", source = "description")
    @Mapping(target = "withQuantity", source = "quantity")
    @Mapping(target = "withPrice", source = "price")
    @Mapping(target = "withNetValue", source = "netValue")
    @Mapping(target = "withGrossValue", source = "grossValue")
    @Mapping(target = "withVatRate", source = "vatRate")
    InvoiceEntry invoiceEntryMongoToInvoiceEntry(InvoiceEntryMongo invoiceEntryMongo);

    VatMongo vatToVatMongo(Vat vat);

    Vat vatMongoToVat(VatMongo vatMongo);
}
