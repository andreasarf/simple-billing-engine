package org.andreasarf.billing.engine.service;

import org.andreasarf.billing.engine.model.BillingCycle;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface BillingCycleService {

    BillingCycle create();

    Optional<BillingCycle> findLatest();
}
