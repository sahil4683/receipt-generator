package io.vv.receipt_generator.repository;


import io.vv.receipt_generator.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    Optional<Receipt> findByReceiptNo(String receiptNo);

    Optional<Receipt> findByBillNo(String billNo);

    List<Receipt> findByOwnerNameContainingIgnoreCase(String ownerName);

    List<Receipt> findByTenantNameContainingIgnoreCase(String tenantName);

    List<Receipt> findByReceiptDateBetween(LocalDate startDate, LocalDate endDate);

    List<Receipt> findByApartmentNameContainingIgnoreCase(String apartmentName);

    @Query("SELECT r FROM Receipt r WHERE r.receiptDate = :date ORDER BY r.createdAt DESC")
    List<Receipt> findByReceiptDateOrderByCreatedAtDesc(@Param("date") LocalDate date);

    @Query("SELECT r FROM Receipt r WHERE r.email = :email ORDER BY r.receiptDate DESC")
    List<Receipt> findByEmailOrderByReceiptDateDesc(@Param("email") String email);

    @Query("SELECT COUNT(r) FROM Receipt r WHERE r.receiptDate = :date")
    long countByReceiptDate(@Param("date") LocalDate date);

    @Query("SELECT SUM(r.amount) FROM Receipt r WHERE r.receiptDate BETWEEN :startDate AND :endDate")
    Double getTotalAmountByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    boolean existsByReceiptNo(String receiptNo);

    boolean existsByBillNo(String billNo);
}