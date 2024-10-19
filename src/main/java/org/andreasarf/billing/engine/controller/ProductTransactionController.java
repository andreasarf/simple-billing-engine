package org.andreasarf.billing.engine.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.andreasarf.billing.engine.common.ApiConstant;
import org.andreasarf.billing.engine.dto.BaseResponse;
import org.andreasarf.billing.engine.dto.NonPerformingDto;
import org.andreasarf.billing.engine.dto.ProductTransactionDto;
import org.andreasarf.billing.engine.model.ProductTransaction;
import org.andreasarf.billing.engine.service.ProductTransactionService;
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
public class ProductTransactionController {

    private final ProductTransactionService productTransactionService;

    @PostMapping(ApiConstant.TRANSACTION)
    public ResponseEntity<BaseResponse<ProductTransaction>> createProductTransaction(
            @RequestBody ProductTransactionDto dto) {
        log.info("Creating product transaction");
        final var transaction = productTransactionService.create(dto);
        return ResponseUtil.ok(transaction);
    }

    @GetMapping(ApiConstant.TRANSACTION)
    public ResponseEntity<BaseResponse<List<ProductTransaction>>> getProductTransaction(
            @RequestParam Long customerId) {
        log.info("Getting product transactions by customer id");
        final var transaction = productTransactionService.getByCustomerId(customerId);
        return ResponseUtil.ok(transaction);
    }

    @GetMapping(ApiConstant.TRANSACTION_NPL)
    public ResponseEntity<BaseResponse<NonPerformingDto>> getNonPerformingProductTransaction(
            @RequestParam Long customerId) {
        log.info("Getting non performing product transactions by customer id");
        final var npl = productTransactionService.checkNonPerformerByCustomer(customerId);
        return ResponseUtil.ok(npl);
    }
}
