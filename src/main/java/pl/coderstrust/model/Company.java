package pl.coderstrust.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@ApiModel(value = "Company")
@JsonDeserialize(builder = Company.CompanyBuilder.class)
@Entity
public final class Company {

    @ApiModelProperty(value = "The unique identifier of the company.", position = -1, dataType = "Long")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;
    @ApiModelProperty(value = "Company name", required = true, example = "Coca-Cola")
    private final String name;
    @ApiModelProperty(value = "Company address", required = true, example = "Down street 32/1")
    private final String address;
    @ApiModelProperty(value = "Company tax ID", required = true, example = "5687195749")
    private final String taxId;
    @ApiModelProperty(value = "Company account number", required = true, example = "27 1288 2000 0000 0119 4879 1265")
    private final String accountNumber;
    @ApiModelProperty(value = "Company phone number", required = true, example = "123 456 789")
    private final String phoneNumber;
    @ApiModelProperty(value = "Company email address", required = true, example ="someAddress@gmail.com")
    private final String email;

    private Company() {
        id = null;
        name = null;
        address = null;
        taxId = null;
        accountNumber = null;
        phoneNumber = null;
        email = null;
    }

    private Company(CompanyBuilder builder) {
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
        if (!(o instanceof Company)) {
            return false;
        }
        Company company = (Company) o;
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
        return "Company{"
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

        public CompanyBuilder withCompany(Company company) {
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

        public Company build() {
            return new Company(this);
        }
    }
}
