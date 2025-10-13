package com.eplatform.b2b.supplier.repo;

import com.eplatform.b2b.supplier.domain.Supplier;
import com.eplatform.b2b.supplier.domain.SupplierContact;
import com.eplatform.b2b.supplier.domain.ContactType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierContactRepository extends JpaRepository<SupplierContact, Long> {

    List<SupplierContact> findBySupplier(Supplier supplier);

    List<SupplierContact> findBySupplierId(Long supplierId);

    Optional<SupplierContact> findBySupplierAndPrimary(Supplier supplier, boolean primary);

    List<SupplierContact> findByContactType(ContactType contactType);

    List<SupplierContact> findBySupplierIdAndContactType(Long supplierId, ContactType contactType);

    Optional<SupplierContact> findByEmail(String email);

    boolean existsByEmailAndSupplierId(String email, Long supplierId);
}
