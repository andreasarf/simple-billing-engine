package org.andreasarf.billing.engine.service.impl;

import lombok.RequiredArgsConstructor;
import org.andreasarf.billing.engine.dto.NonPerformingDto;
import org.andreasarf.billing.engine.dto.ProductTransactionDto;
import org.andreasarf.billing.engine.model.ProductTransaction;
import org.andreasarf.billing.engine.repository.BillingRepository;
import org.andreasarf.billing.engine.repository.ProductTransactionRepository;
import org.andreasarf.billing.engine.service.CustomerService;
import org.andreasarf.billing.engine.service.ProductService;
import org.andreasarf.billing.engine.service.ProductTransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductTransactionServiceImpl implements ProductTransactionService {

    private final ProductTransactionRepository repository;
    private final CustomerService customerService;
    private final ProductService productService;
    private final BillingRepository billingRepository;

    @Transactional
    @Override
    public ProductTransaction create(ProductTransactionDto dto) {
        final var customer = customerService.getById(dto.getCustomerId());
        final var product = productService.getById(dto.getProductId());
        final var productTransaction = ProductTransaction.builder()
                .customer(customer)
                .product(product)
                .status(ProductTransaction.Status.RUNNING)
                .takenAt(ZonedDateTime.now())
                .build();
        return repository.save(productTransaction);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductTransaction> getByCustomerId(Long id) {
        return repository.findByCustomerId(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductTransaction> findNotBilled(Long latestId) {
        return repository.findNotBilled(latestId);
    }

    @Transactional
    @Override
    public ProductTransaction finish(Long id) {
        final var productTransaction = repository.findById(id).orElseThrow();
        productTransaction.setStatus(ProductTransaction.Status.DONE);
        return repository.save(productTransaction);
    }

    @Transactional(readOnly = true)
    @Override
    public NonPerformingDto checkNonPerformerByCustomer(Long customerId) {
        final var customer = customerService.getById(customerId);
        final var productTransactions = repository.findByCustomerIdAndStatus(customerId, ProductTransaction.Status.RUNNING);
        final var result = NonPerformingDto.builder()
                .customerId(customer.getId())
                .count(0)
                .loans(new ArrayList<>())
                .build();

        for (final var productTransaction : productTransactions) {
            final var npl = billingRepository.findMissingPaymentByProductTransactionId(productTransaction.getId());

            if (!npl.isEmpty()) {
                final var loan = NonPerformingDto.Loan.builder()
                        .name(productTransaction.getProduct().getName())
                        .takenAt(productTransaction.getTakenAt())
                        .count(npl.size())
                        .missingBillings(npl)
                        .build();
                result.getLoans().add(loan);
            }
        }

        result.setCount(result.getLoans().size());

        return result;
    }
}
