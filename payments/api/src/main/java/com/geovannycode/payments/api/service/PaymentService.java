package com.geovannycode.payments.api.service;

import com.geovannycode.payments.api.dto.ApprovedDto;
import com.geovannycode.payments.api.dto.ErrorDto;
import com.geovannycode.payments.api.dto.PaymentRequestDto;
import io.vavr.control.Either;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentService {

    public Either<ErrorDto, ApprovedDto> process(PaymentRequestDto req) {
        if (req.amount() == null || req.amount().compareTo(BigDecimal.ZERO) <= 0) {
            return Either.left(new ErrorDto("VALIDATION", "amount debe ser > 0"));
        }

        // ejemplo “mundo real”: regla simple
        if ("ACC-002".equals(req.sourceAccount())) {
            return Either.left(new ErrorDto("BUSINESS", "Saldo insuficiente"));
        }

        return Either.right(new ApprovedDto("TX-" + UUID.randomUUID()));
    }
}
