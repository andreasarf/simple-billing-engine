package org.andreasarf.billing.engine.service.impl;

import org.andreasarf.billing.engine.common.enums.CreditRating;
import org.andreasarf.billing.engine.dto.NonPerformingDto;
import org.andreasarf.billing.engine.model.Billing;
import org.andreasarf.billing.engine.model.BillingCycle;
import org.andreasarf.billing.engine.model.Customer;
import org.andreasarf.billing.engine.model.Product;
import org.andreasarf.billing.engine.model.ProductTransaction;
import org.andreasarf.billing.engine.repository.BillingRepository;
import org.andreasarf.billing.engine.repository.ProductTransactionRepository;
import org.andreasarf.billing.engine.service.CustomerService;
import org.andreasarf.billing.engine.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(ProductTransactionServiceImpl.class)
class ProductTransactionServiceImplTest {

    private static final Long CUSTOMER_ID = Random.from(RandomGenerator.getDefault()).nextLong();

    @Autowired
    private ProductTransactionServiceImpl service;
    @MockBean
    private ProductTransactionRepository repository;
    @MockBean
    private CustomerService customerService;
    @MockBean
    private ProductService productService;
    @MockBean
    private BillingRepository billingRepository;

    @BeforeEach
    void setUp() {
        when(customerService.getById(CUSTOMER_ID))
                .thenReturn(getCustomer());
    }

    @Nested
    class CheckNonPerformerByCustomerTest {

        @Test
        void givenEmptyNpl_whenCheckNonPerformerByCustomer_thenShouldReturnPerforming() {
            // arrange
            final var product1 = Product.builder()
                    .id(1L)
                    .name("Loan A")
                    .build();
            final var product2 = Product.builder()
                    .id(4L)
                    .name("Loan X")
                    .build();
            final var productTransaction1 = ProductTransaction.builder()
                    .id(2L)
                    .customer(getCustomer())
                    .product(product1)
                    .status(ProductTransaction.Status.RUNNING)
                    .takenAt(ZonedDateTime.now().minusDays(30))
                    .build();
            final var productTransaction2 = ProductTransaction.builder()
                    .id(3L)
                    .customer(getCustomer())
                    .product(product2)
                    .status(ProductTransaction.Status.RUNNING)
                    .takenAt(ZonedDateTime.now().minusDays(14))
                    .build();
            final var expected = NonPerformingDto.builder()
                    .customerId(CUSTOMER_ID)
                    .count(0)
                    .loans(List.of())
                    .build();

            when(repository.findByCustomerIdAndStatus(CUSTOMER_ID, ProductTransaction.Status.RUNNING))
                    .thenReturn(List.of(productTransaction1, productTransaction2));
            when(billingRepository.findMissingPaymentByProductTransactionId(2L))
                    .thenReturn(List.of());
            when(billingRepository.findMissingPaymentByProductTransactionId(3L))
                    .thenReturn(List.of());

            // act
            final var res = service.checkNonPerformerByCustomer(CUSTOMER_ID);

            // assert
            assertEquals(expected, res);
            assertEquals(CreditRating.PERFORMING, res.getStatus());
        }

        @Test
        void givenNonEmptyNpl_whenCheckNonPerformerByCustomer_thenShouldReturnNonPerforming() {
            // arrange
            final var product1 = Product.builder()
                    .id(1L)
                    .name("Loan A")
                    .build();
            final var product2 = Product.builder()
                    .id(4L)
                    .name("Loan X")
                    .build();
            final var productTransaction1 = ProductTransaction.builder()
                    .id(2L)
                    .customer(getCustomer())
                    .product(product1)
                    .status(ProductTransaction.Status.RUNNING)
                    .takenAt(ZonedDateTime.now().minusDays(30))
                    .build();
            final var productTransaction2 = ProductTransaction.builder()
                    .id(3L)
                    .customer(getCustomer())
                    .product(product2)
                    .status(ProductTransaction.Status.RUNNING)
                    .takenAt(ZonedDateTime.now().minusDays(14))
                    .build();
            final var billing1 = Billing.builder()
                    .billingCycle(BillingCycle.builder()
                            .id(10L)
                            .build())
                    .productTransaction(productTransaction1)
                    .amount(BigDecimal.valueOf(1000))
                    .build();
            final var billing2 = Billing.builder()
                    .billingCycle(BillingCycle.builder()
                            .id(11L)
                            .build())
                    .productTransaction(productTransaction1)
                    .amount(BigDecimal.valueOf(1000))
                    .build();
            final var expected = NonPerformingDto.builder()
                    .customerId(CUSTOMER_ID)
                    .count(1)
                    .loans(List.of(
                            NonPerformingDto.Loan.builder()
                                    .name(product2.getName())
                                    .takenAt(productTransaction2.getTakenAt())
                                    .count(2)
                                    .missingBillings(List.of(billing1, billing2))
                                    .build()
                    ))
                    .build();

            when(repository.findByCustomerIdAndStatus(CUSTOMER_ID, ProductTransaction.Status.RUNNING))
                    .thenReturn(List.of(productTransaction1, productTransaction2));
            when(billingRepository.findMissingPaymentByProductTransactionId(2L))
                    .thenReturn(List.of());
            when(billingRepository.findMissingPaymentByProductTransactionId(3L))
                    .thenReturn(List.of(billing1, billing2));

            // act
            final var res = service.checkNonPerformerByCustomer(CUSTOMER_ID);

            // assert
            assertEquals(expected, res);
            assertEquals(CreditRating.NON_PERFORMING, res.getStatus());
        }

        @Test
        void givenCustomerNotFound_whenCheckNonPerformerByCustomer_thenShouldThrow() {
            // arrange
            when(customerService.getById(CUSTOMER_ID))
                    .thenThrow(new NoSuchElementException());

            // act
            assertThrows(NoSuchElementException.class,
                    () -> service.checkNonPerformerByCustomer(CUSTOMER_ID));

            // assert
        }

    }

    private static Customer getCustomer() {
        return Customer.builder()
                .id(CUSTOMER_ID)
                .build();
    }
}
