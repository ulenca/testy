package pl.coderstrust.datamodel;

import java.math.BigDecimal;
import java.util.Objects;

public class InvoiceEntry {

    private String id;
    private String description;
    private int quantity;
    private BigDecimal price;
    private BigDecimal netValue;
    private BigDecimal grossValue;
    private Vat vatRate;

    public InvoiceEntry(String id, String description, int quantity, BigDecimal price, BigDecimal netValue, BigDecimal grossValue, Vat vatRate) {
        this.id = id;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.netValue = netValue;
        this.grossValue = grossValue;
        this.vatRate = vatRate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getNetValue() {
        return netValue;
    }

    public void setNetValue(BigDecimal netValue) {
        this.netValue = netValue;
    }

    public BigDecimal getGrossValue() {
        return grossValue;
    }

    public void setGrossValue(BigDecimal grossValue) {
        this.grossValue = grossValue;
    }

    public Vat getVatRate() {
        return vatRate;
    }

    public void setVatRate(Vat vatRate) {
        this.vatRate = vatRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvoiceEntry that = (InvoiceEntry) o;
        return quantity == that.quantity &&
                Objects.equals(id, that.id) &&
                Objects.equals(description, that.description) &&
                Objects.equals(price, that.price) &&
                Objects.equals(netValue, that.netValue) &&
                Objects.equals(grossValue, that.grossValue) &&
                vatRate == that.vatRate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, quantity, price, netValue, grossValue, vatRate);
    }

    @Override
    public String toString() {
        return "InvoiceEntry{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", netValue=" + netValue +
                ", grossValue=" + grossValue +
                ", vatRate=" + vatRate +
                '}';
    }
}
