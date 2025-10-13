package com.eplatform.b2b.supplier.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "supplier_contracts", indexes = {
    @Index(name = "idx_contracts_supplier", columnList = "supplier_id"),
    @Index(name = "idx_contracts_status", columnList = "status"),
    @Index(name = "idx_contracts_validity", columnList = "valid_from, valid_to")
})
public class SupplierContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "contract_number", nullable = false, unique = true)
    private String contractNumber;

    @Column(name = "contract_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContractType contractType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus status = ContractStatus.DRAFT;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "valid_to", nullable = false)
    private LocalDateTime validTo;

    @Column(name = "payment_terms")
    private String paymentTerms;

    @Column(name = "delivery_terms")
    private String deliveryTerms;

    @Column(name = "minimum_order_value", precision = 15, scale = 2)
    private BigDecimal minimumOrderValue;

    @Column(name = "commission_rate", precision = 5, scale = 2)
    private BigDecimal commissionRate;

    @Column(name = "contract_value", precision = 15, scale = 2)
    private BigDecimal contractValue;

    @Column(name = "currency", length = 3)
    private String currency = "VND";

    @Column(name = "signed_date")
    private LocalDateTime signedDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "contract_file_path")
    private String contractFilePath;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Version
    private Long version;

    protected SupplierContract() {}

    public SupplierContract(Supplier supplier, String contractNumber, ContractType contractType,
                           LocalDateTime validFrom, LocalDateTime validTo) {
        this.supplier = supplier;
        this.contractNumber = contractNumber;
        this.contractType = contractType;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    @PreUpdate
    protected void onUpdate() {
        if (status == ContractStatus.ACTIVE && signedDate == null) {
            signedDate = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public Supplier getSupplier() { return supplier; }
    public String getContractNumber() { return contractNumber; }
    public ContractType getContractType() { return contractType; }
    public ContractStatus getStatus() { return status; }
    public LocalDateTime getValidFrom() { return validFrom; }
    public LocalDateTime getValidTo() { return validTo; }
    public String getPaymentTerms() { return paymentTerms; }
    public String getDeliveryTerms() { return deliveryTerms; }
    public BigDecimal getMinimumOrderValue() { return minimumOrderValue; }
    public BigDecimal getCommissionRate() { return commissionRate; }
    public BigDecimal getContractValue() { return contractValue; }
    public String getCurrency() { return currency; }
    public LocalDateTime getSignedDate() { return signedDate; }
    public String getCreatedBy() { return createdBy; }
    public String getApprovedBy() { return approvedBy; }
    public String getContractFilePath() { return contractFilePath; }
    public String getNotes() { return notes; }
    public Long getVersion() { return version; }

    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
    public void setContractNumber(String contractNumber) { this.contractNumber = contractNumber; }
    public void setContractType(ContractType contractType) { this.contractType = contractType; }
    public void setStatus(ContractStatus status) { this.status = status; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }
    public void setValidTo(LocalDateTime validTo) { this.validTo = validTo; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }
    public void setDeliveryTerms(String deliveryTerms) { this.deliveryTerms = deliveryTerms; }
    public void setMinimumOrderValue(BigDecimal minimumOrderValue) { this.minimumOrderValue = minimumOrderValue; }
    public void setCommissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; }
    public void setContractValue(BigDecimal contractValue) { this.contractValue = contractValue; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    public void setContractFilePath(String contractFilePath) { this.contractFilePath = contractFilePath; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return status == ContractStatus.ACTIVE &&
               now.isAfter(validFrom) && now.isBefore(validTo);
    }
}
