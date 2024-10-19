package org.andreasarf.billing.engine.service.impl;

import lombok.RequiredArgsConstructor;
import org.andreasarf.billing.engine.model.BillingCycle;
import org.andreasarf.billing.engine.repository.BillingCycleRepository;
import org.andreasarf.billing.engine.service.BillingCycleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BillingCycleServiceImpl implements BillingCycleService {

    private final BillingCycleRepository repository;

    @Transactional
    @Override
    public BillingCycle create() {
        final long nextName = findLatest()
                .map(BillingCycle::getId)
                .orElse(0L) + 1;
        final var billingCycle = BillingCycle.builder()
                .name("Billing Cycle #" + nextName)
                .timestamp(ZonedDateTime.now())
                .build();
        return repository.save(billingCycle);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<BillingCycle> findLatest() {
        return repository.findFirstByOrderByIdDesc();
    }
}
