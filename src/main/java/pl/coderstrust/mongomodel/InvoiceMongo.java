package pl.coderstrust.mongomodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@JsonDeserialize(builder = InvoiceMongo.InvoiceBuilder.class)
@Document
public final class InvoiceMongo {

    @Id
    @JsonIgnore
    private String mongoId;

    @Indexed(unique = true)
    private final Long id;

    private final CompanyMongo seller;

    private final CompanyMongo buyer;

    private final List<InvoiceEntryMongo> entries;

    @PersistenceConstructor
    private InvoiceMongo(String mongoId, Long id, String number, LocalDate issuedDate, LocalDate dueDate, CompanyMongo seller, CompanyMongo buyer, ArrayList<InvoiceEntryMongo> entries) {
        this.mongoId = mongoId;
        this.id = id;
        this.number = number;
        this.issuedDate = issuedDate;
        this.dueDate = dueDate;
        this.seller = seller;
        this.buyer = buyer;
        this.entries = entries;
    }

    private final String number;
    private final LocalDate issuedDate;
    private final LocalDate dueDate;

    private InvoiceMongo() {
        id = null;
        seller = null;
        buyer = null;
        entries = null;
        number = null;
        issuedDate = null;
        dueDate = null;
    }

    private InvoiceMongo(InvoiceMongo.InvoiceBuilder builder) {
        mongoId = builder.mongoId;
        id = builder.id;
        number = builder.number;
        issuedDate = builder.issuedDate;
        dueDate = builder.dueDate;
        seller = builder.seller;
        buyer = builder.buyer;
        entries = builder.entries;
    }

    public static InvoiceMongo.InvoiceBuilder builder() {
        return new InvoiceBuilder();
    }

    public String getMongoId() {
        return mongoId;
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public CompanyMongo getSeller() {
        return seller;
    }

    public CompanyMongo getBuyer() {
        return buyer;
    }

    public List<InvoiceEntryMongo> getEntries() {
        return entries == null ? new ArrayList<>() : new ArrayList<>(entries);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InvoiceMongo)) {
            return false;
        }
        InvoiceMongo invoice = (InvoiceMongo) o;
        return id.equals(invoice.id)
                && number.equals(invoice.number)
                && issuedDate.equals(invoice.issuedDate)
                && dueDate.equals(invoice.dueDate)
                && seller.equals(invoice.seller)
                && buyer.equals(invoice.buyer)
                && entries.equals(invoice.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, issuedDate, dueDate, seller, buyer, entries);
    }

    @Override
    public String toString() {
        return "InvoiceMongo{"
                + "id='" + id + '\''
                + ", number='" + number + '\''
                + ", issuedDate=" + issuedDate
                + ", dueDate=" + dueDate
                + ", seller=" + seller
                + ", buyer=" + buyer
                + ", entries=" + entries
                + '}';
    }

    public static final class InvoiceBuilder {
        private String mongoId;
        private Long id;
        private String number;
        private LocalDate issuedDate;
        private LocalDate dueDate;
        private CompanyMongo seller;
        private CompanyMongo buyer;
        private List<InvoiceEntryMongo> entries;

        public InvoiceMongo.InvoiceBuilder withInvoiceMongo(InvoiceMongo invoice) {
            this.mongoId = invoice.mongoId;
            this.id = invoice.id;
            this.number = invoice.number;
            this.issuedDate = invoice.issuedDate;
            this.dueDate = invoice.dueDate;
            this.seller = invoice.seller;
            this.buyer = invoice.buyer;
            this.entries = invoice.entries;
            return this;
        }

        public InvoiceMongo.InvoiceBuilder withMongoId(String mongoId) {
            this.mongoId = mongoId;
            return this;
        }

        public InvoiceMongo.InvoiceBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public InvoiceMongo.InvoiceBuilder withNumber(String number) {
            this.number = number;
            return this;
        }

        public InvoiceMongo.InvoiceBuilder withIssuedDate(LocalDate issuedDate) {
            this.issuedDate = issuedDate;
            return this;
        }

        public InvoiceMongo.InvoiceBuilder withDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public InvoiceMongo.InvoiceBuilder withSeller(CompanyMongo seller) {
            this.seller = seller;
            return this;
        }

        public InvoiceMongo.InvoiceBuilder withBuyer(CompanyMongo buyer) {
            this.buyer = buyer;
            return this;
        }

        public InvoiceMongo.InvoiceBuilder withEntries(List<InvoiceEntryMongo> entries) {
            this.entries = entries;
            return this;
        }

        public InvoiceMongo build() {
            return new InvoiceMongo(this);
        }
    }
}
