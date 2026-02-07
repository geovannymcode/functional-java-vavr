package com.geovannycode.payments.api.dto;

import io.vavr.control.Either;

public record PaymentResponseDto(
        Either<ErrorDto, ApprovedDto> result
) {}
