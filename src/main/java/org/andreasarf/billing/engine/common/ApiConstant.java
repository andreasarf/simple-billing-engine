package org.andreasarf.billing.engine.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ApiConstant {

    public static final String V1_ROOT = "/api/v1/billing";
    public static final String V1_ROOT_INTERNAL = "/api/v1/internal/billing";

    // customer
    public static final String CUSTOMER = "/customer";
    public static final String CUSTOMERS = "/customers";

    // product (loan)
    public static final String PRODUCT = "/product";
    public static final String PRODUCTS = "/products";

    // product transaction (loan taken)
    public static final String TRANSACTION = "/transaction";
    public static final String TRANSACTIONS = "/transactions";
    public static final String TRANSACTION_NPL = TRANSACTION + "/npl";

    // billing cycle
    public static final String BILLING_CYCLE = "/billing-cycle";
    public static final String BILLING_CYCLES = "/billing-cycles";

    // billing
    public static final String BILLINGS = "/all";
    public static final String BILLING_PAYMENT = "/payment";
    public static final String BILLING_OUTSTANDING = "/outstanding";
}
