package com.geovannycode.payments.api.dto;

import io.vavr.control.Option;

import java.math.BigDecimal;

public record PaymentRequestDto(
        String customerId,
        String sourceAccount,
        String destinationAccount,
        BigDecimal amount,
        Option<String> concept
) {}
