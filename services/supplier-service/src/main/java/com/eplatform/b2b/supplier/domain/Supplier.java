package com.eplatform.b2b.supplier.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "suppliers", indexes = {
    @Index(name = "idx_suppliers_status", columnList = "status"),
    @Index(name = "idx_suppliers_company_name", columnList = "company_name"),
    @Index(name = "idx_suppliers_tax_code", columnList = "tax_code", unique = true)
})
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "tax_code", nullable = false, unique = true)
    private String taxCode;

    @Column(name = "business_license")
    private String businessLicense;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplierStatus status = SupplierStatus.PENDING;

    @Column(name = "contact_person", nullable = false)
    private String contactPerson;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column
    private String address;

    @Column(name = "website")
    private String website;

    @Column(name = "industry")
    private String industry;

    @Column(name = "company_size")
    @Enumerated(EnumType.STRING)
    private CompanySize companySize;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    @Column(name = "approved_date")
    private LocalDateTime approvedDate;

    @Column(name = "notes", length = 1000)
    private String notes;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SupplierContact> contacts = new ArrayList<>();

    @Version
    private Long version;

    protected Supplier() {}

    public Supplier(String companyName, String taxCode, String businessLicense,
                   String contactPerson, String email, String phone, String address) {
        this.companyName = companyName;
        this.taxCode = taxCode;
        this.businessLicense = businessLicense;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.registrationDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        if (status == SupplierStatus.APPROVED && approvedDate == null) {
            approvedDate = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getCompanyName() { return companyName; }
    public String getTaxCode() { return taxCode; }
    public String getBusinessLicense() { return businessLicense; }
    public SupplierStatus getStatus() { return status; }
    public String getContactPerson() { return contactPerson; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getWebsite() { return website; }
    public String getIndustry() { return industry; }
    public CompanySize getCompanySize() { return companySize; }
    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public LocalDateTime getApprovedDate() { return approvedDate; }
    public String getNotes() { return notes; }
    public List<SupplierContact> getContacts() { return contacts; }
    public Long getVersion() { return version; }

    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setBusinessLicense(String businessLicense) { this.businessLicense = businessLicense; }
    public void setStatus(SupplierStatus status) { this.status = status; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setWebsite(String website) { this.website = website; }
    public void setIndustry(String industry) { this.industry = industry; }
    public void setCompanySize(CompanySize companySize) { this.companySize = companySize; }
    public void setNotes(String notes) { this.notes = notes; }

    public void addContact(SupplierContact contact) {
        contacts.add(contact);
        contact.setSupplier(this);
    }

    public void removeContact(SupplierContact contact) {
        contacts.remove(contact);
        contact.setSupplier(null);
    }
}
