package org.andreasarf.billing.engine.service;

import org.andreasarf.billing.engine.dto.BillingPaymentDto;
import org.andreasarf.billing.engine.dto.OutstandingBillingDto;
import org.andreasarf.billing.engine.model.Billing;

import java.util.List;

public interface BillingService {

    void createBillings();

    boolean payBilling(BillingPaymentDto dto);

    List<Billing> getAllBillings(Long productTransactionId);

    OutstandingBillingDto findOutstandingBillings(Long productTransactionId);

    List<Billing> getMissingPaymentsByTransaction(Long transactionId);
}
