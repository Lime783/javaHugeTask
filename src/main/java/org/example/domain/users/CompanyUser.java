package org.example.domain.users;

import java.util.Objects;

public class CompanyUser extends User {

    private String companyName;
    private String taxId;

    public CompanyUser(String email, String displayName, String companyName, String taxId) {
        super(email, displayName);
        if (companyName.length() > 100 || companyName.length() < 3) {
            throw new IllegalArgumentException("Invalid company name: " + companyName);
        }
        if (taxId.length() != 5) {
            throw new IllegalArgumentException("Invalid tax id: " + taxId);
        }
        this.companyName = Objects.requireNonNull(companyName, "companyName cannot be null");
        this.taxId = Objects.requireNonNull(taxId, "taxId cannot be null");
    }

    public CompanyUser(String email, String companyName, String taxId) {
        this(email, "Anon", companyName, taxId);
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }
}
