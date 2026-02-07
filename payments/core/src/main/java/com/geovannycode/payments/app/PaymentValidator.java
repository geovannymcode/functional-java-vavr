package com.geovannycode.payments.app;

import com.geovannycode.payments.domain.PaymentRequest;
import com.geovannycode.payments.domain.ValidationError;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;

import java.math.BigDecimal;

import static io.vavr.control.Validation.invalid;
import static io.vavr.control.Validation.valid;

public class PaymentValidator {

    public Validation<Seq<ValidationError>, PaymentRequest> validate(PaymentRequest r) {
        return Validation.combine(
                        nonBlank(r.customerId(), "customerId"),
                        nonBlank(r.sourceAccount(), "sourceAccount"),
                        nonBlank(r.destinationAccount(), "destinationAccount"),
                        positive(r.amount()),
                        maxConcept(r.concept(), 120)
                )
                .ap((c, s, d, a, concept) -> r);
    }

    private Validation<ValidationError, String> nonBlank(String v, String field) {
        return (v != null && !v.isBlank())
                ? valid(v)
                : invalid(new ValidationError(field + " es requerido"));
    }

    private Validation<ValidationError, BigDecimal> positive(BigDecimal amount) {
        return (amount != null && amount.compareTo(BigDecimal.ZERO) > 0)
                ? valid(amount)
                : invalid(new ValidationError("amount debe ser > 0"));
    }

    private Validation<ValidationError, String> maxConcept(String concept, int max) {
        if (concept == null) return valid(""); // opcional
        return concept.length() <= max
                ? valid(concept)
                : invalid(new ValidationError("concept máximo " + max + " caracteres"));
    }
}
