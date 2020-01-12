package pl.coderstrust.mongomodel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigDecimal;
import java.util.Objects;
import org.springframework.data.annotation.PersistenceConstructor;

@JsonDeserialize(builder = pl.coderstrust.model.InvoiceEntry.InvoiceEntryBuilder.class)
public final class InvoiceEntryMongo {

    private final Long id;
    private final String description;
    private final long quantity;
    private final BigDecimal price;
    private final BigDecimal netValue;
    private final BigDecimal grossValue;
    private final VatMongo vatRate;

    @PersistenceConstructor
    private InvoiceEntryMongo(Long id, String description, long quantity, BigDecimal price, BigDecimal netValue, BigDecimal grossValue, VatMongo vatRate) {
        this.id = id;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.netValue = netValue;
        this.grossValue = grossValue;
        this.vatRate = vatRate;
    }

    private InvoiceEntryMongo() {
        id = null;
        description = null;
        quantity = 0;
        price = null;
        netValue = null;
        grossValue = null;
        vatRate = null;
    }

    private InvoiceEntryMongo(InvoiceEntryBuilder builder) {
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

    public VatMongo getVatRate() {
        return vatRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InvoiceEntryMongo)) {
            return false;
        }
        InvoiceEntryMongo entry = (InvoiceEntryMongo) o;
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
        return "InvoiceEntryMongo{"
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
        private VatMongo vatRate;

        public InvoiceEntryBuilder withInvoiceEntryMongo(InvoiceEntryMongo invoiceEntry) {
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

        public InvoiceEntryBuilder withVatRate(VatMongo vatRate) {
            this.vatRate = vatRate;
            return this;
        }

        public InvoiceEntryMongo build() {
            return new InvoiceEntryMongo(this);
        }
    }
}
