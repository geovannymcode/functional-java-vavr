package com.geovannycode.payments.domain;

import java.math.BigDecimal;

public record PaymentRequest(
        String customerId,
        String sourceAccount,
        String destinationAccount,
        BigDecimal amount,
        String concept
) {
}
