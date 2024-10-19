package org.andreasarf.billing.engine.service;

import org.andreasarf.billing.engine.dto.CustomerDto;
import org.andreasarf.billing.engine.model.Customer;

import java.util.List;

public interface CustomerService {

    Customer create(CustomerDto dto);

    Customer getById(Long id);

    List<Customer> getAll();
}
