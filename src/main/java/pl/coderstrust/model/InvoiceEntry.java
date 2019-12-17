package pl.coderstrust.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@ApiModel(value = "Invoice entry ")
@JsonDeserialize(builder = InvoiceEntry.InvoiceEntryBuilder.class)
@Entity
public final class InvoiceEntry {

    @ApiModelProperty(position = -1, required = true, example = "1", dataType = "Long")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;
    @ApiModelProperty(value = "Invoice entry description", required = true, example = "Buying some material")
    private final String description;
    @ApiModelProperty(value = "Number of units of the entry", required = true, example = "1")
    private final long quantity;
    @ApiModelProperty(value = "Unit price of the entry", required = true, example = "100")
    private final BigDecimal price;
    @ApiModelProperty(value = "Price without tax", required = true, example = "23")
    private final BigDecimal netValue;
    @ApiModelProperty(value = "Price with tax", required = true, example = "123")
    private final BigDecimal grossValue;
    @ApiModelProperty(value = "Vat rate", required = true, example = "VAT_23")
    private final Vat vatRate;

    private InvoiceEntry() {
        id = null;
        description = null;
        quantity = 0;
        price = null;
        netValue = null;
        grossValue = null;
        vatRate = null;
    }

    private InvoiceEntry(InvoiceEntryBuilder builder) {
        id = builder.id;
        description = builder.description;
        quantity = builder.quantity;
        price = builder.price;
        netValue = builder.netValue;
        grossValue = builder.grossValue;
        vatRate = builder.vatRate;
    }

    public static InvoiceEntryBuilder builder() {
        return new InvoiceEntryBuilder();
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public long getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getNetValue() {
        return netValue;
    }

    public BigDecimal getGrossValue() {
        return grossValue;
    }

    public Vat getVatRate() {
        return vatRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InvoiceEntry)) {
            return false;
        }
        InvoiceEntry entry = (InvoiceEntry) o;
        return id.equals(entry.id)
                && description.equals(entry.description)
                && quantity == entry.quantity
                && price.equals(entry.price)
                && netValue.equals(entry.netValue)
                && grossValue.equals(entry.grossValue)
                && vatRate == entry.vatRate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, quantity, price, netValue, grossValue, vatRate);
    }

    @Override
    public String toString() {
        return "InvoiceEntry{"
                + "id='" + id + '\''
                + ", description='" + description + '\''
                + ", quantity=" + quantity
                + ", price=" + price
                + ", netValue=" + netValue
                + ", grossValue=" + grossValue
                + ", vatRate=" + vatRate
                + '}';
    }

    public static final class InvoiceEntryBuilder {
        private Long id;
        private String description;
        private long quantity;
        private BigDecimal price;
        private BigDecimal netValue;
        private BigDecimal grossValue;
        private Vat vatRate;

        public InvoiceEntryBuilder withInvoiceEntry(InvoiceEntry invoiceEntry) {
            this.id = invoiceEntry.id;
            this.description = invoiceEntry.description;
            this.quantity = invoiceEntry.quantity;
            this.price = invoiceEntry.price;
            this.netValue = invoiceEntry.netValue;
            this.grossValue = invoiceEntry.grossValue;
            this.vatRate = invoiceEntry.vatRate;
            return this;
        }

        public InvoiceEntryBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public InvoiceEntryBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public InvoiceEntryBuilder withQuantity(long quantity) {
            this.quantity = quantity;
            return this;
        }

        public InvoiceEntryBuilder withPrice(BigDecimal price) {
            this.price = price;
            return this;
        }

        public InvoiceEntryBuilder withNetValue(BigDecimal netValue) {
            this.netValue = netValue;
            return this;
        }

        public InvoiceEntryBuilder withGrossValue(BigDecimal grossValue) {
            this.grossValue = grossValue;
            return this;
        }

        public InvoiceEntryBuilder withVatRate(Vat vatRate) {
            this.vatRate = vatRate;
            return this;
        }

        public InvoiceEntry build() {
            return new InvoiceEntry(this);
        }
    }
}
