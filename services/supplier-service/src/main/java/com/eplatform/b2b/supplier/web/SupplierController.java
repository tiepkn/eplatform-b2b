package com.eplatform.b2b.supplier.web;

import com.eplatform.b2b.supplier.domain.*;
import com.eplatform.b2b.supplier.service.SupplierService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    // Supplier CRUD Operations
    @PostMapping
    public ResponseEntity<Supplier> createSupplier(@Valid @RequestBody CreateSupplierRequest request) {
        Supplier supplier = supplierService.createSupplier(
                request.companyName(),
                request.taxCode(),
                request.businessLicense(),
                request.contactPerson(),
                request.email(),
                request.phone(),
                request.address()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(supplier);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long id) {
        return supplierService.getSupplierById(id)
                .map(supplier -> ResponseEntity.ok(supplier))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tax-code/{taxCode}")
    public ResponseEntity<Supplier> getSupplierByTaxCode(@PathVariable String taxCode) {
        return supplierService.getSupplierByTaxCode(taxCode)
                .map(supplier -> ResponseEntity.ok(supplier))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Supplier>> getSuppliersByStatus(@PathVariable SupplierStatus status) {
        List<Supplier> suppliers = supplierService.getSuppliersByStatus(status);
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Supplier>> searchSuppliers(@RequestParam String q) {
        List<Supplier> suppliers = supplierService.searchSuppliers(q);
        return ResponseEntity.ok(suppliers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Supplier> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSupplierRequest request) {
        Supplier supplier = supplierService.updateSupplier(
                id,
                request.companyName(),
                request.businessLicense(),
                request.contactPerson(),
                request.email(),
                request.phone(),
                request.address(),
                request.website(),
                request.industry(),
                request.companySize(),
                request.notes()
        );
        return ResponseEntity.ok(supplier);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Supplier> approveSupplier(@PathVariable Long id) {
        Supplier supplier = supplierService.approveSupplier(id);
        return ResponseEntity.ok(supplier);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Supplier> rejectSupplier(@PathVariable Long id, @RequestBody String reason) {
        Supplier supplier = supplierService.rejectSupplier(id, reason);
        return ResponseEntity.ok(supplier);
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<Supplier> activateSupplier(@PathVariable Long id) {
        Supplier supplier = supplierService.activateSupplier(id);
        return ResponseEntity.ok(supplier);
    }

    @PostMapping("/{id}/suspend")
    public ResponseEntity<Supplier> suspendSupplier(@PathVariable Long id, @RequestBody String reason) {
        Supplier supplier = supplierService.suspendSupplier(id, reason);
        return ResponseEntity.ok(supplier);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }

    // Contact Management
    @PostMapping("/{supplierId}/contacts")
    public ResponseEntity<SupplierContact> addContact(
            @PathVariable Long supplierId,
            @Valid @RequestBody CreateContactRequest request) {
        SupplierContact contact = supplierService.addContact(
                supplierId,
                request.name(),
                request.position(),
                request.email(),
                request.phone(),
                request.contactType()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(contact);
    }

    @GetMapping("/{supplierId}/contacts")
    public ResponseEntity<List<SupplierContact>> getSupplierContacts(@PathVariable Long supplierId) {
        List<SupplierContact> contacts = supplierService.getSupplierContacts(supplierId);
        return ResponseEntity.ok(contacts);
    }

    @DeleteMapping("/contacts/{contactId}")
    public ResponseEntity<Void> removeContact(@PathVariable Long contactId) {
        supplierService.removeContact(contactId);
        return ResponseEntity.noContent().build();
    }

    // Contract Management
    @PostMapping("/{supplierId}/contracts")
    public ResponseEntity<SupplierContract> createContract(
            @PathVariable Long supplierId,
            @Valid @RequestBody CreateContractRequest request) {
        SupplierContract contract = supplierService.createContract(
                supplierId,
                request.contractNumber(),
                request.contractType(),
                request.validFrom(),
                request.validTo()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(contract);
    }

    @GetMapping("/{supplierId}/contracts")
    public ResponseEntity<List<SupplierContract>> getSupplierContracts(@PathVariable Long supplierId) {
        List<SupplierContract> contracts = supplierService.getSupplierContracts(supplierId);
        return ResponseEntity.ok(contracts);
    }

    @GetMapping("/contracts/active")
    public ResponseEntity<List<SupplierContract>> getActiveContracts() {
        List<SupplierContract> contracts = supplierService.getActiveContracts();
        return ResponseEntity.ok(contracts);
    }

    // Analytics
    @GetMapping("/analytics/status-count")
    public ResponseEntity<SupplierStatusCount> getSupplierStatusCount() {
        long pending = supplierService.getSupplierCountByStatus(SupplierStatus.PENDING);
        long approved = supplierService.getSupplierCountByStatus(SupplierStatus.APPROVED);
        long active = supplierService.getSupplierCountByStatus(SupplierStatus.ACTIVE);
        long total = supplierService.getTotalSuppliers();

        SupplierStatusCount count = new SupplierStatusCount(pending, approved, active, total);
        return ResponseEntity.ok(count);
    }

    // DTOs
    public record CreateSupplierRequest(
            @NotBlank String companyName,
            @NotBlank String taxCode,
            String businessLicense,
            @NotBlank String contactPerson,
            @NotBlank String email,
            @NotBlank String phone,
            String address
    ) {}

    public record UpdateSupplierRequest(
            String companyName,
            String businessLicense,
            String contactPerson,
            String email,
            String phone,
            String address,
            String website,
            String industry,
            CompanySize companySize,
            String notes
    ) {}

    public record CreateContactRequest(
            @NotBlank String name,
            @NotBlank String position,
            @NotBlank String email,
            @NotBlank String phone,
            @NotNull ContactType contactType
    ) {}

    public record CreateContractRequest(
            @NotBlank String contractNumber,
            @NotNull ContractType contractType,
            @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime validFrom,
            @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime validTo
    ) {}

    public record SupplierStatusCount(
            long pending,
            long approved,
            long active,
            long total
    ) {}
}
