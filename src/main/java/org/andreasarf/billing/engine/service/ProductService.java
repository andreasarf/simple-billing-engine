package org.andreasarf.billing.engine.service;

import org.andreasarf.billing.engine.dto.ProductDto;
import org.andreasarf.billing.engine.model.Product;

import java.util.List;

public interface ProductService {

    Product create(ProductDto dto);

    Product getById(Long id);

    List<Product> getAll();
}
