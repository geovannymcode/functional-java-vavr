package com.geovannycode.payments.domain;

public record ValidationError(String message) implements DomainError {}
