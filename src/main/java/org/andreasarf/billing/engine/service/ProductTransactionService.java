package org.andreasarf.billing.engine.service;

import org.andreasarf.billing.engine.dto.NonPerformingDto;
import org.andreasarf.billing.engine.dto.ProductTransactionDto;
import org.andreasarf.billing.engine.model.ProductTransaction;

import java.util.List;

public interface ProductTransactionService {

    ProductTransaction create(ProductTransactionDto dto);

    List<ProductTransaction> getByCustomerId(Long id);

    List<ProductTransaction> findNotBilled(Long latestId);

    ProductTransaction finish(Long id);

    NonPerformingDto checkNonPerformerByCustomer(Long customerId);
}
