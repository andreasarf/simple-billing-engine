package org.andreasarf.billing.engine.dto;

import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link org.andreasarf.billing.engine.model.Product}
 */
@Value
public class ProductDto implements Serializable {

    String name;
    BigDecimal amount;
    Double interestRate;
    Integer period;
}
