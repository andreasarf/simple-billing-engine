package org.andreasarf.billing.engine.repository;

import org.andreasarf.billing.engine.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
