package com.geovannycode.payments.api.domain;

import io.vavr.control.Either;

public record PaymentResponseDto(
        Either<com.geovannycode.payments.api.domain.ErrorDto, com.geovannycode.payments.api.domain.ApprovedDto> result
) {}
