package org.andreasarf.billing.engine.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.andreasarf.billing.engine.common.ApiConstant;
import org.andreasarf.billing.engine.common.enums.ErrorCode;
import org.andreasarf.billing.engine.dto.BaseResponse;
import org.andreasarf.billing.engine.model.BillingCycle;
import org.andreasarf.billing.engine.service.BillingCycleService;
import org.andreasarf.billing.engine.util.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstant.V1_ROOT)
public class BillingCycleController {

    private final BillingCycleService billingCycleService;

    @PostMapping(ApiConstant.BILLING_CYCLE)
    public ResponseEntity<BaseResponse<BillingCycle>> createLatestBillingCycle() {
        log.info("Creating latest billing cycle");
        final var latest = billingCycleService.create();
        return ResponseUtil.ok(latest);
    }

    @GetMapping(ApiConstant.BILLING_CYCLE)
    public ResponseEntity<BaseResponse<BillingCycle>> getLatestBillingCycle() {
        log.info("Getting latest billing cycles");
        final var billingCycle = billingCycleService.findLatest();

        return billingCycle
                .map(ResponseUtil::ok)
                .orElseGet(() -> ResponseUtil.error(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
