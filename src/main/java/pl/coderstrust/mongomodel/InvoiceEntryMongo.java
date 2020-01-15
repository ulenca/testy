package pl.coderstrust.mongomodel;

import java.math.BigDecimal;
import java.util.Objects;
import org.springframework.data.annotation.PersistenceConstructor;

public final class InvoiceEntryMongo {

    private final String description;
    private final long quantity;
    private final BigDecimal price;
    private final BigDecimal netValue;
    private final BigDecimal grossValue;
    private final VatMongo vatRate;

    @PersistenceConstructor
    private InvoiceEntryMongo(String description, long quantity, BigDecimal price, BigDecimal netValue, BigDecimal grossValue, VatMongo vatRate) {
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.netValue = netValue;
        this.grossValue = grossValue;
        this.vatRate = vatRate;
    }

    private InvoiceEntryMongo() {
        description = null;
        quantity = 0;
        price = null;
        netValue = null;
        grossValue = null;
        vatRate = null;
    }

    private InvoiceEntryMongo(InvoiceEntryBuilder builder) {
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
        return description.equals(entry.description)
                && quantity == entry.quantity
                && price.equals(entry.price)
                && netValue.equals(entry.netValue)
                && grossValue.equals(entry.grossValue)
                && vatRate == entry.vatRate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, quantity, price, netValue, grossValue, vatRate);
    }

    @Override
    public String toString() {
        return "InvoiceEntryMongo{"
                + ", description='" + description + '\''
                + ", quantity=" + quantity
                + ", price=" + price
                + ", netValue=" + netValue
                + ", grossValue=" + grossValue
                + ", vatRate=" + vatRate
                + '}';
    }

    public static final class InvoiceEntryBuilder {
        private String description;
        private long quantity;
        private BigDecimal price;
        private BigDecimal netValue;
        private BigDecimal grossValue;
        private VatMongo vatRate;

        public InvoiceEntryBuilder withInvoiceEntryMongo(InvoiceEntryMongo invoiceEntry) {
            this.description = invoiceEntry.description;
            this.quantity = invoiceEntry.quantity;
            this.price = invoiceEntry.price;
            this.netValue = invoiceEntry.netValue;
            this.grossValue = invoiceEntry.grossValue;
            this.vatRate = invoiceEntry.vatRate;
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
