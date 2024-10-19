package org.andreasarf.billing.engine.service.impl;

import lombok.RequiredArgsConstructor;
import org.andreasarf.billing.engine.dto.ProductDto;
import org.andreasarf.billing.engine.model.Product;
import org.andreasarf.billing.engine.repository.ProductRepository;
import org.andreasarf.billing.engine.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    @Transactional
    @Override
    public Product create(ProductDto dto) {
        final var product = Product.builder()
                .name(dto.getName())
                .amount(dto.getAmount())
                .interestRate(dto.getInterestRate())
                .period(dto.getPeriod())
                .build();
        return repository.save(product);
    }

    @Transactional(readOnly = true)
    @Override
    public Product getById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Product> getAll() {
        return repository.findAll();
    }
}
