package org.andreasarf.billing.engine.dto;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link org.andreasarf.billing.engine.model.Customer}
 */
@Value
public class CustomerDto implements Serializable {
    String name;
    String email;
}
