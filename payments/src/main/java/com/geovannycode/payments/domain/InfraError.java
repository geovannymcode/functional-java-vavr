package com.geovannycode.payments.domain;

public record InfraError(String message) implements DomainError {}