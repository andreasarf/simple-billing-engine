package org.andreasarf.billing.engine.repository;

import org.andreasarf.billing.engine.model.BillingCycle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillingCycleRepository extends JpaRepository<BillingCycle, Long> {

    Optional<BillingCycle> findFirstByOrderByIdDesc();
}
