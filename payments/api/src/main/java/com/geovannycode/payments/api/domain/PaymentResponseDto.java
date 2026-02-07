package com.geovannycode.payments.api.domain;

import io.vavr.control.Either;

public record PaymentResponseDto(
        Either<ErrorDto, ApprovedDto> result
) {}
