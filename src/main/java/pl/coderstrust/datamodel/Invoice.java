package pl.coderstrust.datamodel;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Invoice {

    private String id;
    private String number;
    private LocalDate issuedDate;
    private LocalDate dueDate;
    private Company seller;
    private Company buyer;
    private List<InvoiceEntry> invoiceEntries;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDate issuedDate) {
        this.issuedDate = issuedDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Company getSeller() {
        return seller;
    }

    public void setSeller(Company seller) {
        this.seller = seller;
    }

    public Company getBuyer() {
        return buyer;
    }

    public void setBuyer(Company buyer) {
        this.buyer = buyer;
    }

    public List<InvoiceEntry> getInvoiceEntries() {
        return invoiceEntries;
    }

    public void setInvoiceEntries(List<InvoiceEntry> invoiceEntries) {
        this.invoiceEntries = invoiceEntries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invoice invoice = (Invoice) o;
        return Objects.equals(id, invoice.id) &&
                Objects.equals(number, invoice.number) &&
                Objects.equals(issuedDate, invoice.issuedDate) &&
                Objects.equals(dueDate, invoice.dueDate) &&
                Objects.equals(seller, invoice.seller) &&
                Objects.equals(buyer, invoice.buyer) &&
                Objects.equals(invoiceEntries, invoice.invoiceEntries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, issuedDate, dueDate, seller, buyer, invoiceEntries);
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id='" + id + '\'' +
                ", number='" + number + '\'' +
                ", issuedDate=" + issuedDate +
                ", dueDate=" + dueDate +
                ", seller=" + seller +
                ", buyer=" + buyer +
                ", invoiceEntries=" + invoiceEntries +
                '}';
    }
}
