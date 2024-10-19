package org.andreasarf.billing.engine.repository;

import org.andreasarf.billing.engine.model.ProductTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductTransactionRepository extends JpaRepository<ProductTransaction, Long> {

    List<ProductTransaction> findByCustomerId(Long id);

    List<ProductTransaction> findByCustomerIdAndStatus(Long id, ProductTransaction.Status status);

    @Query("SELECT pt FROM ProductTransaction pt " +
            "WHERE pt.status = 'RUNNING'" +
            "AND pt.id NOT IN (" +
                "SELECT b.productTransaction.id FROM Billing b " +
                "WHERE b.billingCycle.id = ?1)")
    List<ProductTransaction> findNotBilled(Long latestId);

}
