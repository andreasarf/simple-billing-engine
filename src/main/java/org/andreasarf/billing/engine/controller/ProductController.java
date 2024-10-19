package org.andreasarf.billing.engine.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.andreasarf.billing.engine.common.ApiConstant;
import org.andreasarf.billing.engine.dto.BaseResponse;
import org.andreasarf.billing.engine.dto.ProductDto;
import org.andreasarf.billing.engine.model.Product;
import org.andreasarf.billing.engine.service.ProductService;
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
public class ProductController {

    private final ProductService productService;

    @PostMapping(ApiConstant.PRODUCT)
    public ResponseEntity<BaseResponse<Product>> createProduct(@RequestBody ProductDto dto) {
        log.info("Creating product");
        final var product = productService.create(dto);
        return ResponseUtil.ok(product);
    }

    @GetMapping(ApiConstant.PRODUCT + "/{id}")
    public ResponseEntity<BaseResponse<Product>> getProduct(@PathVariable Long id) {
        log.info("Getting product");
        final var product = productService.getById(id);
        return ResponseUtil.ok(product);
    }

    @GetMapping(ApiConstant.PRODUCTS)
    public ResponseEntity<BaseResponse<List<Product>>> getAllProducts() {
        log.info("Getting all products");
        final var products = productService.getAll();
        return ResponseUtil.ok(products);
    }
}
