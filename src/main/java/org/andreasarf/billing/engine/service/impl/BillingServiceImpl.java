package org.andreasarf.billing.engine.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.andreasarf.billing.engine.dto.BillingPaymentDto;
import org.andreasarf.billing.engine.dto.OutstandingBillingDto;
import org.andreasarf.billing.engine.model.Billing;
import org.andreasarf.billing.engine.model.Product;
import org.andreasarf.billing.engine.repository.BillingRepository;
import org.andreasarf.billing.engine.service.BillingCycleService;
import org.andreasarf.billing.engine.service.BillingService;
import org.andreasarf.billing.engine.service.ProductTransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingServiceImpl implements BillingService {

    private final BillingRepository billingRepository;
    private final BillingCycleService billingCycleService;
    private final ProductTransactionService productTransactionService;

    @Transactional
    @Override
    public void createBillings() {
        final var latestCycle = billingCycleService.findLatest().orElseThrow();
        final var productTransactions = productTransactionService.findNotBilled(latestCycle.getId());

        log.info("There are {} product transactions to be billed for {}",
                productTransactions.size(), latestCycle.getName());

        for (final var productTransaction : productTransactions) {
            final var billing = Billing.builder()
                    .productTransaction(productTransaction)
                    .billingCycle(latestCycle)
                    .amount(getAmountToPaid(productTransaction.getProduct()))
                    .build();
            billingRepository.save(billing);
        }
    }

    @Transactional
    @Override
    public boolean payBilling(BillingPaymentDto dto) {
        final var outstanding = findOutstandingBillings(dto.getProductTransactionId());

        // NOTE: paid amount should be equal to the total outstanding amount
        if (outstanding.getTotalAmount().compareTo(dto.getAmount()) != 0) {
            log.warn("Paid amount {} is not equal to the total outstanding amount {}",
                    dto.getAmount(), outstanding.getTotalAmount());
            return false;
        }

        for (final var billing : outstanding.getBillings()) {
            billing.setPaidAt(ZonedDateTime.now());
        }

        billingRepository.saveAll(outstanding.getBillings());

        Thread.ofVirtual().start(() ->
                checkAndUpdateTransactionStatus(dto.getProductTransactionId()));

        return true;
    }

    private void checkAndUpdateTransactionStatus(Long productTransactionId) {
        final var isDone = billingRepository.isBillingFinished(productTransactionId);
        if (isDone) {
            productTransactionService.finish(productTransactionId);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Billing> getAllBillings(Long productTransactionId) {
        return billingRepository.findAllByProductTransactionId(productTransactionId);
    }

    @Transactional(readOnly = true)
    @Override
    public OutstandingBillingDto findOutstandingBillings(Long productTransactionId) {
        final var billings = billingRepository.findUnpaidBillingsByTransaction(productTransactionId);
        final var totalAmount = billings.stream()
                .map(Billing::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new OutstandingBillingDto(totalAmount, billings);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Billing> getMissingPaymentsByTransaction(Long transactionId) {
        return billingRepository.findMissingPaymentByProductTransactionId(transactionId);
    }

    private static BigDecimal getAmountToPaid(Product product) {
        return product.getAmount()
                .add(product.getAmount().multiply(BigDecimal.valueOf(product.getInterestRate() / 100.0)))
                .divide(BigDecimal.valueOf(product.getPeriod()), 2, RoundingMode.UP);
    }
}
