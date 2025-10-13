package com.eplatform.b2b.supplier.service;

import com.eplatform.b2b.supplier.domain.*;
import com.eplatform.b2b.supplier.repo.SupplierRepository;
import com.eplatform.b2b.supplier.repo.SupplierContactRepository;
import com.eplatform.b2b.supplier.repo.SupplierContractRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierContactRepository contactRepository;
    private final SupplierContractRepository contractRepository;

    public SupplierService(SupplierRepository supplierRepository,
                          SupplierContactRepository contactRepository,
                          SupplierContractRepository contractRepository) {
        this.supplierRepository = supplierRepository;
        this.contactRepository = contactRepository;
        this.contractRepository = contractRepository;
    }

    // Supplier Management
    public Supplier createSupplier(String companyName, String taxCode, String businessLicense,
                                  String contactPerson, String email, String phone, String address) {
        if (supplierRepository.existsByTaxCode(taxCode)) {
            throw new IllegalArgumentException("Supplier with tax code " + taxCode + " already exists");
        }
        if (supplierRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Supplier with email " + email + " already exists");
        }

        Supplier supplier = new Supplier(companyName, taxCode, businessLicense,
                                       contactPerson, email, phone, address);
        return supplierRepository.save(supplier);
    }

    @Transactional(readOnly = true)
    public Optional<Supplier> getSupplierById(Long id) {
        return supplierRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Supplier> getSupplierByTaxCode(String taxCode) {
        return supplierRepository.findByTaxCode(taxCode);
    }

    @Transactional(readOnly = true)
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Supplier> getSuppliersByStatus(SupplierStatus status) {
        return supplierRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Supplier> searchSuppliers(String keyword) {
        return supplierRepository.findByCompanyNameOrContactPersonContaining(keyword);
    }

    public Supplier updateSupplier(Long id, String companyName, String businessLicense,
                                  String contactPerson, String email, String phone, String address,
                                  String website, String industry, CompanySize companySize, String notes) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier with ID " + id + " not found"));

        if (!supplier.getEmail().equals(email) && supplierRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Supplier with email " + email + " already exists");
        }

        supplier.setCompanyName(companyName);
        supplier.setBusinessLicense(businessLicense);
        supplier.setContactPerson(contactPerson);
        supplier.setEmail(email);
        supplier.setPhone(phone);
        supplier.setAddress(address);
        supplier.setWebsite(website);
        supplier.setIndustry(industry);
        supplier.setCompanySize(companySize);
        supplier.setNotes(notes);

        return supplierRepository.save(supplier);
    }

    public Supplier approveSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier with ID " + id + " not found"));

        supplier.setStatus(SupplierStatus.APPROVED);
        return supplierRepository.save(supplier);
    }

    public Supplier rejectSupplier(Long id, String reason) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier with ID " + id + " not found"));

        supplier.setStatus(SupplierStatus.REJECTED);
        supplier.setNotes(reason);
        return supplierRepository.save(supplier);
    }

    public Supplier activateSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier with ID " + id + " not found"));

        supplier.setStatus(SupplierStatus.ACTIVE);
        return supplierRepository.save(supplier);
    }

    public Supplier suspendSupplier(Long id, String reason) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier with ID " + id + " not found"));

        supplier.setStatus(SupplierStatus.SUSPENDED);
        if (reason != null) {
            supplier.setNotes(reason);
        }
        return supplierRepository.save(supplier);
    }

    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new IllegalArgumentException("Supplier with ID " + id + " not found");
        }
        supplierRepository.deleteById(id);
    }

    // Contact Management
    public SupplierContact addContact(Long supplierId, String name, String position,
                                     String email, String phone, ContactType contactType) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier with ID " + supplierId + " not found"));

        if (contactRepository.existsByEmailAndSupplierId(email, supplierId)) {
            throw new IllegalArgumentException("Contact with email " + email + " already exists for this supplier");
        }

        SupplierContact contact = new SupplierContact(supplier, name, position, email, phone, contactType);
        return contactRepository.save(contact);
    }

    @Transactional(readOnly = true)
    public List<SupplierContact> getSupplierContacts(Long supplierId) {
        return contactRepository.findBySupplierId(supplierId);
    }

    public void removeContact(Long contactId) {
        if (!contactRepository.existsById(contactId)) {
            throw new IllegalArgumentException("Contact with ID " + contactId + " not found");
        }
        contactRepository.deleteById(contactId);
    }

    // Contract Management
    public SupplierContract createContract(Long supplierId, String contractNumber, ContractType contractType,
                                          LocalDateTime validFrom, LocalDateTime validTo) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier with ID " + supplierId + " not found"));

        if (contractRepository.existsByContractNumber(contractNumber)) {
            throw new IllegalArgumentException("Contract with number " + contractNumber + " already exists");
        }

        SupplierContract contract = new SupplierContract(supplier, contractNumber, contractType, validFrom, validTo);
        return contractRepository.save(contract);
    }

    @Transactional(readOnly = true)
    public List<SupplierContract> getSupplierContracts(Long supplierId) {
        return contractRepository.findBySupplierId(supplierId);
    }

    @Transactional(readOnly = true)
    public List<SupplierContract> getActiveContracts() {
        return contractRepository.findActiveContracts(LocalDateTime.now());
    }

    // Analytics
    @Transactional(readOnly = true)
    public long getSupplierCountByStatus(SupplierStatus status) {
        return supplierRepository.countByStatus(status);
    }

    @Transactional(readOnly = true)
    public long getTotalSuppliers() {
        return supplierRepository.count();
    }
}
