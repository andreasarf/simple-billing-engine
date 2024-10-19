package org.andreasarf.billing.engine.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.andreasarf.billing.engine.common.ApiConstant;
import org.andreasarf.billing.engine.common.enums.ErrorCode;
import org.andreasarf.billing.engine.dto.BaseResponse;
import org.andreasarf.billing.engine.dto.BillingPaymentDto;
import org.andreasarf.billing.engine.dto.OutstandingBillingDto;
import org.andreasarf.billing.engine.model.Billing;
import org.andreasarf.billing.engine.service.BillingService;
import org.andreasarf.billing.engine.util.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstant.V1_ROOT)
public class BillingController {

    private final BillingService billingService;

    @PostMapping
    public ResponseEntity<BaseResponse<Void>> createBillings() {
        log.info("Creating billings");
        billingService.createBillings();
        return ResponseUtil.ok();
    }

    @GetMapping(ApiConstant.BILLINGS)
    public ResponseEntity<BaseResponse<List<Billing>>> getAllBillings(@RequestParam Long trxId) {
        log.info("Getting all billings");
        final var billings = billingService.getAllBillings(trxId);
        return ResponseUtil.ok(billings);
    }


    @PostMapping(ApiConstant.BILLING_PAYMENT)
    public ResponseEntity<BaseResponse<Void>> payBilling(@RequestBody BillingPaymentDto dto) {
        log.info("Paying billing");
        final var paid = billingService.payBilling(dto);

        if (paid) {
            return ResponseUtil.ok();
        } else {
            return ResponseUtil.error(ErrorCode.PAID_AMOUNT_NOT_MATCH);
        }
    }

    @GetMapping(ApiConstant.BILLING_OUTSTANDING)
    public ResponseEntity<BaseResponse<OutstandingBillingDto>> getOutstandingBillings(
            @RequestParam Long trxId) {
        log.info("Getting outstanding billings");
        final var outstanding = billingService.findOutstandingBillings(trxId);
        return ResponseUtil.ok(outstanding);
    }
}
