package org.example.domain;

import java.util.Objects;

public class CompanyUser extends User {
    private String companyName;
    private String taxId;

    public CompanyUser(String email, String displayName, String companyName, String taxId) {
        super(email, displayName);
        Objects.requireNonNull(companyName, "companyName cannot be null");
        Objects.requireNonNull(taxId, "taxId cannot be null");
        this.companyName = companyName;
        this.taxId = taxId;
    }

    public CompanyUser(String email, String companyName, String taxId) {
        super(email);
        Objects.requireNonNull(companyName, "companyName cannot be null");
        Objects.requireNonNull(taxId, "taxId cannot be null");
        this.companyName = companyName;
        this.taxId = taxId;
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

    @Override
    public String toString() {
        return "CompanyUser{" +
                "companyName='" + companyName + '\'' +
                ", taxId='" + taxId + '\'' +
                '}';
    }
}
