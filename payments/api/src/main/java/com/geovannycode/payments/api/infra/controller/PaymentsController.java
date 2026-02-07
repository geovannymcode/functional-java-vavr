package com.geovannycode.payments.api.infra.controller;

import com.geovannycode.payments.api.app.PaymentService;
import com.geovannycode.payments.api.domain.ApprovedDto;
import com.geovannycode.payments.api.domain.ErrorDto;
import com.geovannycode.payments.api.domain.PaymentRequestDto;
import com.geovannycode.payments.api.domain.PaymentResponseDto;
import io.vavr.control.Either;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentsController {

    private final PaymentService service;

    public PaymentsController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDto> create(@RequestBody PaymentRequestDto req) {
        Either<ErrorDto, ApprovedDto> result = service.process(req);

        // armamos un DTO que contiene Either (se serializa con vavr-jackson)
        var body = new PaymentResponseDto(result);

        // status según tipo de error
        if (result.isRight()) {
            return ResponseEntity.ok(body);
        }

        var code = result.getLeft().code();
        return switch (code) {
            case "VALIDATION" -> ResponseEntity.badRequest().body(body);
            case "BUSINESS" -> ResponseEntity.status(422).body(body);
            default -> ResponseEntity.internalServerError().body(body);
        };
    }
}
