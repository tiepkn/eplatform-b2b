package com.eplatform.b2b.supplier.repo;

import com.eplatform.b2b.supplier.domain.Supplier;
import com.eplatform.b2b.supplier.domain.SupplierContract;
import com.eplatform.b2b.supplier.domain.ContractStatus;
import com.eplatform.b2b.supplier.domain.ContractType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierContractRepository extends JpaRepository<SupplierContract, Long> {

    List<SupplierContract> findBySupplier(Supplier supplier);

    List<SupplierContract> findBySupplierId(Long supplierId);

    Optional<SupplierContract> findByContractNumber(String contractNumber);

    List<SupplierContract> findByStatus(ContractStatus status);

    List<SupplierContract> findByContractType(ContractType contractType);

    List<SupplierContract> findBySupplierIdAndStatus(Long supplierId, ContractStatus status);

    @Query("SELECT c FROM SupplierContract c WHERE c.status = 'ACTIVE' AND c.validFrom <= :now AND c.validTo >= :now")
    List<SupplierContract> findActiveContracts(@Param("now") LocalDateTime now);

    @Query("SELECT c FROM SupplierContract c WHERE c.supplier.id = :supplierId AND c.status = 'ACTIVE' AND c.validFrom <= :now AND c.validTo >= :now")
    List<SupplierContract> findActiveContractsBySupplier(@Param("supplierId") Long supplierId, @Param("now") LocalDateTime now);

    @Query("SELECT c FROM SupplierContract c WHERE c.validTo < :now AND c.status = 'ACTIVE'")
    List<SupplierContract> findExpiredContracts(@Param("now") LocalDateTime now);

    @Query("SELECT c FROM SupplierContract c WHERE c.validFrom > :now")
    List<SupplierContract> findUpcomingContracts(@Param("now") LocalDateTime now);

    boolean existsByContractNumber(String contractNumber);
}
