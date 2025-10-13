package com.eplatform.b2b.supplier.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "supplier_contacts")
public class SupplierContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "contact_type", nullable = false)
    private ContactType contactType;

    @Column(name = "is_primary")
    private boolean primary = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Version
    private Long version;

    protected SupplierContact() {}

    public SupplierContact(Supplier supplier, String name, String position,
                          String email, String phone, ContactType contactType) {
        this.supplier = supplier;
        this.name = name;
        this.position = position;
        this.email = email;
        this.phone = phone;
        this.contactType = contactType;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public Supplier getSupplier() { return supplier; }
    public String getName() { return name; }
    public String getPosition() { return position; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public ContactType getContactType() { return contactType; }
    public boolean isPrimary() { return primary; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Long getVersion() { return version; }

    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
    public void setName(String name) { this.name = name; }
    public void setPosition(String position) { this.position = position; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setContactType(ContactType contactType) { this.contactType = contactType; }
    public void setPrimary(boolean primary) { this.primary = primary; }
}
