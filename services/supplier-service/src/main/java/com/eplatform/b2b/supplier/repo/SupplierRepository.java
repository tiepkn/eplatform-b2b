package com.eplatform.b2b.supplier.repo;

import com.eplatform.b2b.supplier.domain.Supplier;
import com.eplatform.b2b.supplier.domain.SupplierStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findByTaxCode(String taxCode);

    Optional<Supplier> findByEmail(String email);

    List<Supplier> findByStatus(SupplierStatus status);

    List<Supplier> findByCompanyNameContainingIgnoreCase(String companyName);

    @Query("SELECT s FROM Supplier s WHERE s.companyName LIKE %:keyword% OR s.contactPerson LIKE %:keyword%")
    List<Supplier> findByCompanyNameOrContactPersonContaining(@Param("keyword") String keyword);

    @Query("SELECT s FROM Supplier s WHERE s.status = :status AND s.registrationDate >= :fromDate")
    List<Supplier> findByStatusAndRegistrationDateAfter(@Param("status") SupplierStatus status, @Param("fromDate") LocalDateTime fromDate);

    @Query("SELECT COUNT(s) FROM Supplier s WHERE s.status = :status")
    long countByStatus(@Param("status") SupplierStatus status);

    boolean existsByTaxCode(String taxCode);

    boolean existsByEmail(String email);
}
