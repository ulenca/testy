package pl.coderstrust.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@ApiModel(value = "Invoice")
@JsonDeserialize(builder = Invoice.InvoiceBuilder.class)
@Entity
public final class Invoice {

    @ApiModelProperty(value = "The unique identifier of the invoice", position = -1, dataType = "Long")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;
    @ApiModelProperty(value = "Invoice number", required = true, example = "180213-002")
    private final String number;
    @ApiModelProperty(value = "Invoice issued data", required = true, example = "12/12/2019")
    private final LocalDate issuedDate;
    @ApiModelProperty(value = "Invoice due data", required = true, example = "11/12/2019")
    private final LocalDate dueDate;
    @ApiModelProperty(value = "Seller data")

    @ManyToOne(cascade = CascadeType.ALL)
    private final Company seller;
    @ApiModelProperty(value = "Buyer data")

    @ManyToOne(cascade = CascadeType.ALL)
    private final Company buyer;
    @ApiModelProperty(value = "Invoice entries")

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private final List<InvoiceEntry> entries;

    private Invoice() {
        id = null;
        seller = null;
        buyer = null;
        entries = null;
        number = null;
        issuedDate = null;
        dueDate = null;
    }

    private Invoice(Invoice.InvoiceBuilder builder) {
        id = builder.id;
        number = builder.number;
        issuedDate = builder.issuedDate;
        dueDate = builder.dueDate;
        seller = builder.seller;
        buyer = builder.buyer;
        entries = builder.entries;
    }

    public static Invoice.InvoiceBuilder builder() {
        return new Invoice.InvoiceBuilder();
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

    public Company getSeller() {
        return seller;
    }

    public Company getBuyer() {
        return buyer;
    }

    public List<InvoiceEntry> getEntries() {
        return entries == null ? new ArrayList<>() : new ArrayList<>(entries);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Invoice)) {
            return false;
        }
        Invoice invoice = (Invoice) o;
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
        return "Invoice{"
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
        private Long id;
        private String number;
        private LocalDate issuedDate;
        private LocalDate dueDate;
        private Company seller;
        private Company buyer;
        private List<InvoiceEntry> entries;

        public Invoice.InvoiceBuilder withInvoice(Invoice invoice) {
            this.id = invoice.id;
            this.number = invoice.number;
            this.issuedDate = invoice.issuedDate;
            this.dueDate = invoice.dueDate;
            this.seller = invoice.seller;
            this.buyer = invoice.buyer;
            this.entries = invoice.entries;
            return this;
        }

        public Invoice.InvoiceBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public Invoice.InvoiceBuilder withNumber(String number) {
            this.number = number;
            return this;
        }

        public Invoice.InvoiceBuilder withIssuedDate(LocalDate issuedDate) {
            this.issuedDate = issuedDate;
            return this;
        }

        public Invoice.InvoiceBuilder withDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public Invoice.InvoiceBuilder withSeller(Company seller) {
            this.seller = seller;
            return this;
        }

        public Invoice.InvoiceBuilder withBuyer(Company buyer) {
            this.buyer = buyer;
            return this;
        }

        public Invoice.InvoiceBuilder withEntries(List<InvoiceEntry> entries) {
            this.entries = entries;
            return this;
        }

        public Invoice build() {
            return new Invoice(this);
        }
    }
}
