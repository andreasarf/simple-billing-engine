package org.andreasarf.billing.engine.dto;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link org.andreasarf.billing.engine.model.ProductTransaction}
 */
@Value
public class ProductTransactionDto implements Serializable {
    Long customerId;
    Long productId;
}
