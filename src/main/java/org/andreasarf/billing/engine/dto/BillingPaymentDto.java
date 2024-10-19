package org.andreasarf.billing.engine.dto;

import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link org.andreasarf.billing.engine.model.Billing}
 */
@Value
public class BillingPaymentDto implements Serializable {

    Long productTransactionId;
    BigDecimal amount;
}
