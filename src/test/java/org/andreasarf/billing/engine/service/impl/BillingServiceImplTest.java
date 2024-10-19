package org.andreasarf.billing.engine.service.impl;

import org.andreasarf.billing.engine.dto.BillingPaymentDto;
import org.andreasarf.billing.engine.dto.OutstandingBillingDto;
import org.andreasarf.billing.engine.model.Billing;
import org.andreasarf.billing.engine.model.BillingCycle;
import org.andreasarf.billing.engine.model.Product;
import org.andreasarf.billing.engine.model.ProductTransaction;
import org.andreasarf.billing.engine.repository.BillingRepository;
import org.andreasarf.billing.engine.service.BillingCycleService;
import org.andreasarf.billing.engine.service.ProductTransactionService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(BillingServiceImpl.class)
class BillingServiceImplTest {

    private static final Long TRX_ID = Random.from(RandomGenerator.getDefault()).nextLong();

    @Autowired
    private BillingServiceImpl service;
    @MockBean
    private BillingRepository billingRepository;
    @MockBean
    private BillingCycleService billingCycleService;
    @MockBean
    private ProductTransactionService productTransactionService;

    @Nested
    class CreateBillingTest {

        @Test
        void givenEmptyTransaction_whenCreateBillings_thenShouldNotCreate() {
            // arrange
            final var latestCycle = BillingCycle.builder()
                    .id(1L)
                    .name("Billing Cycle #1")
                    .timestamp(ZonedDateTime.now())
                    .build();
            when(billingCycleService.findLatest()).thenReturn(Optional.of(latestCycle));
            when(productTransactionService.findNotBilled(latestCycle.getId())).thenReturn(List.of());

            // act
            service.createBillings();

            // assert
            verify(billingRepository, never()) .save(any());
        }

        @Test
        void givenTransactions_whenCreateBillings_thenShouldCreate() {
            // arrange
            final var latestCycle = BillingCycle.builder()
                    .id(1L)
                    .name("Billing Cycle #1")
                    .timestamp(ZonedDateTime.now())
                    .build();
            final var productTransaction1 = ProductTransaction.builder()
                    .id(1L)
                    .product(Product.builder()
                            .amount(BigDecimal.valueOf(100))
                            .interestRate(10.0)
                            .period(10)
                            .build())
                    .build();
            final var productTransaction2 = ProductTransaction.builder()
                    .id(2L)
                    .product(Product.builder()
                            .amount(BigDecimal.valueOf(200))
                            .interestRate(20.0)
                            .period(20)
                            .build())
                    .build();
            final var billing1 = Billing.builder()
                    .billingCycle(latestCycle)
                    .productTransaction(productTransaction1)
                    .amount(new BigDecimal("11.00"))
                    .build();
            final var billing2 = Billing.builder()
                    .billingCycle(latestCycle)
                    .productTransaction(productTransaction2)
                    .amount(new BigDecimal("12.00"))
                    .build();

            when(billingCycleService.findLatest()).thenReturn(Optional.of(latestCycle));
            when(productTransactionService.findNotBilled(latestCycle.getId()))
                    .thenReturn(List.of(productTransaction1, productTransaction2));

            // act
            service.createBillings();

            // assert
            verify(billingRepository) .save(billing1);
            verify(billingRepository) .save(billing2);
        }
    }

    @Nested
    class PayBillingTest {

        @Test
        void givenDifferentAmount_whenPayBilling_thenShouldReject() {
            // arrange
            final var paymentDto = new BillingPaymentDto(TRX_ID, BigDecimal.valueOf(100));
            final var billing1 = Billing.builder()
                    .id(1L)
                    .amount(BigDecimal.valueOf(100))
                    .build();
            final var billing2 = Billing.builder()
                    .id(2L)
                    .amount(BigDecimal.valueOf(200))
                    .build();

            when(billingRepository.findUnpaidBillingsByTransaction(TRX_ID))
                    .thenReturn(List.of(billing1, billing2));

            // act
            final var res = service.payBilling(paymentDto);

            // assert
            assertFalse(res);
            verify(billingRepository, never()).saveAll(anyList());
        }

        @Test
        void givenExactAmount_whenPayBilling_thenShouldApprove() {
            // arrange
            final var paymentDto = new BillingPaymentDto(TRX_ID, BigDecimal.valueOf(300));
            final var billing1 = Billing.builder()
                    .id(1L)
                    .amount(BigDecimal.valueOf(100))
                    .build();
            final var billing2 = Billing.builder()
                    .id(2L)
                    .amount(BigDecimal.valueOf(200))
                    .build();

            when(billingRepository.findUnpaidBillingsByTransaction(TRX_ID))
                    .thenReturn(List.of(billing1, billing2));
            when(billingRepository.isBillingFinished(TRX_ID)).thenReturn(false);

            // act
            final var res = service.payBilling(paymentDto);

            // assert
            assertTrue(res);
            verify(billingRepository).saveAll(anyList());
            assertNotNull(billing1.getPaidAt());
            assertNotNull(billing2.getPaidAt());
            verify(productTransactionService, never()).finish(TRX_ID);
        }

        @Test
        void givenFinished_whenPayBilling_thenShouldCloseLoan() {
            // arrange
            final var paymentDto = new BillingPaymentDto(TRX_ID, BigDecimal.valueOf(300));
            final var billing1 = Billing.builder()
                    .id(1L)
                    .amount(BigDecimal.valueOf(100))
                    .build();
            final var billing2 = Billing.builder()
                    .id(2L)
                    .amount(BigDecimal.valueOf(200))
                    .build();

            when(billingRepository.findUnpaidBillingsByTransaction(TRX_ID))
                    .thenReturn(List.of(billing1, billing2));
            when(billingRepository.isBillingFinished(TRX_ID)).thenReturn(true);

            // act
            final var res = service.payBilling(paymentDto);

            // assert
            assertTrue(res);
            verify(billingRepository).saveAll(anyList());
            assertNotNull(billing1.getPaidAt());
            assertNotNull(billing2.getPaidAt());
            verify(productTransactionService, timeout(1000)).finish(TRX_ID);
        }
    }

    @Nested
    class FindOutstandingBillingsTest {

        @Test
        void givenEmptyBillings_whenFindOutstandingBillings_thenShouldReturnEmptyList() {
            // arrange
            when(billingRepository.findUnpaidBillingsByTransaction(TRX_ID)).thenReturn(List.of());

            // act
            final var res = service.findOutstandingBillings(TRX_ID);

            // assert
            assertEquals(new OutstandingBillingDto(BigDecimal.ZERO, List.of()), res);
        }

        @Test
        void givenBillings_whenFindOutstandingBillings_thenShouldReturnOutstandingBillings() {
            // arrange
            final var billing1 = Billing.builder()
                    .id(1L)
                    .amount(BigDecimal.valueOf(100))
                    .build();
            final var billing2 = Billing.builder()
                    .id(2L)
                    .amount(BigDecimal.valueOf(200))
                    .build();
            when(billingRepository.findUnpaidBillingsByTransaction(TRX_ID)).thenReturn(List.of(billing1, billing2));

            // act
            final var res = service.findOutstandingBillings(TRX_ID);

            // assert
            assertEquals(new OutstandingBillingDto(BigDecimal.valueOf(300), List.of(billing1, billing2)), res);
        }
    }
}
