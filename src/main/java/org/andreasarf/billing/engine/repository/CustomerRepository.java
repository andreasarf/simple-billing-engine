package org.andreasarf.billing.engine.repository;

import org.andreasarf.billing.engine.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
