package org.andreasarf.billing.engine.repository;

import org.andreasarf.billing.engine.model.Billing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BillingRepository extends JpaRepository<Billing, Long> {

    List<Billing> findAllByProductTransactionId(Long id);

    @Query("SELECT b FROM Billing b " +
            "WHERE b.productTransaction.id = ?1 " +
            "AND b.paidAt IS NULL")
    List<Billing> findUnpaidBillingsByTransaction(Long id);

    // NOTE: non-performing loan is when there are at least two missing billings
    // Exclude the latest billing cycle
    @Query("SELECT b FROM Billing b " +
            "WHERE b.productTransaction.id = ?1 " +
            "AND b.billingCycle.id != (SELECT bc.id FROM BillingCycle bc ORDER BY bc.id DESC LIMIT 1) " +
            "AND b.paidAt IS NULL")
    List<Billing> findMissingPaymentByProductTransactionId(Long id);

    @Query("SELECT CASE WHEN COUNT(b) = b.productTransaction.product.period THEN TRUE ELSE FALSE END " +
            "FROM Billing b " +
            "WHERE b.productTransaction.id = ?1 " +
            "AND b.paidAt IS NOT NULL")
    boolean isBillingFinished(Long trxId);
}
