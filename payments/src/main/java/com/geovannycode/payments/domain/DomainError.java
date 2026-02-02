package com.geovannycode.payments.domain;

public sealed interface DomainError permits ValidationError, BusinessError, InfraError {
    String message();
}
