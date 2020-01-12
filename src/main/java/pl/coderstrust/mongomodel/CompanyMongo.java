package pl.coderstrust.mongomodel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Objects;
import org.springframework.data.annotation.PersistenceConstructor;

@JsonDeserialize(builder = CompanyMongo.CompanyBuilder.class)
public final class CompanyMongo {

    private final Long id;
    private final String name;
    private final String address;
    private final String taxId;
    private final String accountNumber;
    private final String phoneNumber;
    private final String email;

    @PersistenceConstructor
    private CompanyMongo(Long id, String name, String address, String taxId, String accountNumber, String phoneNumber, String email) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.taxId = taxId;
        this.accountNumber = accountNumber;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    private CompanyMongo() {
        id = null;
        name = null;
        address = null;
        taxId = null;
        accountNumber = null;
        phoneNumber = null;
        email = null;
    }

    private CompanyMongo(CompanyBuilder builder) {
        id = builder.id;
        name = builder.name;
        address = builder.address;
        taxId = builder.taxId;
        accountNumber = builder.accountNumber;
        phoneNumber = builder.phoneNumber;
        email = builder.email;
    }

    public static CompanyBuilder builder() {
        return new CompanyBuilder();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getTaxId() {
        return taxId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompanyMongo)) {
            return false;
        }
        CompanyMongo company = (CompanyMongo) o;
        return id.equals(company.id)
                && name.equals(company.name)
                && address.equals(company.address)
                && taxId.equals(company.taxId)
                && accountNumber.equals(company.accountNumber)
                && phoneNumber.equals(company.phoneNumber)
                && email.equals(company.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address, taxId, accountNumber, phoneNumber, email);
    }

    @Override
    public String toString() {
        return "CompanyMongo{"
                + "id='" + id + '\''
                + ", name='" + name + '\''
                + ", address='" + address + '\''
                + ", taxId='" + taxId + '\''
                + ", accountNumber='" + accountNumber + '\''
                + ", phoneNumber='" + phoneNumber + '\''
                + ", email='" + email + '\''
                + '}';
    }

    public static final class CompanyBuilder {
        private Long id;
        private String name;
        private String address;
        private String taxId;
        private String accountNumber;
        private String phoneNumber;
        private String email;

        public CompanyBuilder withCompanyMongo(CompanyMongo company) {
            this.id = company.id;
            this.name = company.name;
            this.address = company.address;
            this.taxId = company.taxId;
            this.accountNumber = company.accountNumber;
            this.phoneNumber = company.phoneNumber;
            this.email = company.email;
            return this;
        }

        public CompanyBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public CompanyBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public CompanyBuilder withAddress(String address) {
            this.address = address;
            return this;
        }

        public CompanyBuilder withTaxId(String taxId) {
            this.taxId = taxId;
            return this;
        }

        public CompanyBuilder withAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public CompanyBuilder withPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public CompanyBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public CompanyMongo build() {
            return new CompanyMongo(this);
        }
    }
}
