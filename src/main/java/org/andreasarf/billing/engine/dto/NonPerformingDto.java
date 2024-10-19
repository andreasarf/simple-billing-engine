package org.andreasarf.billing.engine.dto;

import lombok.Builder;
import lombok.Data;
import org.andreasarf.billing.engine.common.enums.CreditRating;
import org.andreasarf.billing.engine.model.Billing;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class NonPerformingDto implements Serializable {

    private Long customerId;
    private Integer count;
    private List<Loan> loans;

    public CreditRating getStatus() {
        return loans.isEmpty() ? CreditRating.PERFORMING : CreditRating.NON_PERFORMING;
    }

    @Data
    @Builder
    public static class Loan implements Serializable {

        private String name;
        private ZonedDateTime takenAt;
        private Integer count;
        private List<Billing> missingBillings;
    }
}
