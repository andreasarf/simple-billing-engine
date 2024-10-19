package org.andreasarf.billing.engine.dto;

import lombok.Value;
import org.andreasarf.billing.engine.model.Billing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Value
public class OutstandingBillingDto implements Serializable {

    BigDecimal totalAmount;
    List<Billing> billings;
}
