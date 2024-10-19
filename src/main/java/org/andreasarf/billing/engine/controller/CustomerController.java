package org.andreasarf.billing.engine.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.andreasarf.billing.engine.common.ApiConstant;
import org.andreasarf.billing.engine.dto.BaseResponse;
import org.andreasarf.billing.engine.dto.CustomerDto;
import org.andreasarf.billing.engine.model.Customer;
import org.andreasarf.billing.engine.service.CustomerService;
import org.andreasarf.billing.engine.util.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstant.V1_ROOT)
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping(ApiConstant.CUSTOMER)
    public ResponseEntity<BaseResponse<Customer>> createCustomer(@RequestBody CustomerDto dto) {
        log.info("Creating customer");
        final var customer = customerService.create(dto);
        return ResponseUtil.ok(customer);
    }

    @GetMapping(ApiConstant.CUSTOMER + "/{id}")
    public ResponseEntity<BaseResponse<Customer>> getCustomer(@PathVariable Long id) {
        log.info("Getting customer");
        final var customer = customerService.getById(id);
        return ResponseUtil.ok(customer);
    }

    @GetMapping(ApiConstant.CUSTOMERS)
    public ResponseEntity<BaseResponse<List<Customer>>> getAllCustomers() {
        log.info("Getting all customers");
        final var customers = customerService.getAll();
        return ResponseUtil.ok(customers);
    }
}
