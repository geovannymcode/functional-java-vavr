package com.geovannycode.payments.api.controller;

import com.geovannycode.payments.app.PaymentService;
import com.geovannycode.payments.domain.DomainError;
import com.geovannycode.payments.domain.PaymentRequest;
import io.vavr.control.Either;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<?> processPayment(@RequestBody PaymentRequest request) {
        Either<DomainError, String> result = paymentService.process(request);
        
        return result.fold(
                error -> ResponseEntity.badRequest().body(error),
                txId -> ResponseEntity.ok().body(new PaymentResponse(txId))
        );
    }

    record PaymentResponse(String transactionId) {}
}
