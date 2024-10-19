package org.andreasarf.billing.engine.service.impl;

import lombok.RequiredArgsConstructor;
import org.andreasarf.billing.engine.dto.CustomerDto;
import org.andreasarf.billing.engine.model.Customer;
import org.andreasarf.billing.engine.repository.CustomerRepository;
import org.andreasarf.billing.engine.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;

    @Transactional
    @Override
    public Customer create(CustomerDto dto) {
        final var customer = Customer.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
        return repository.save(customer);
    }

    @Transactional(readOnly = true)
    @Override
    public Customer getById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Customer> getAll() {
        return repository.findAll();
    }
}
