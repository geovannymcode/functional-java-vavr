package com.geovannycode.payments.api.app;

import com.geovannycode.payments.api.domain.ApprovedDto;
import com.geovannycode.payments.api.domain.ErrorDto;
import com.geovannycode.payments.api.domain.PaymentRequestDto;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PaymentService - Tests unitarios")
class PaymentServiceTest {

    private PaymentService service;

    @BeforeEach
    void setUp() {
        service = new PaymentService();
    }

    @Test
    @DisplayName("Debe aprobar pago cuando todos los datos son válidos")
    void shouldApprovePaymentWhenValid() {
        // Given
        var request = new PaymentRequestDto(
                "CUST-001",
                "ACC-001",
                "ACC-XYZ",
                new BigDecimal("100.00"),
                Option.of("Pago de servicio")
        );

        // When
        Either<ErrorDto, ApprovedDto> result = service.process(request);

        // Then
        assertTrue(result.isRight(), "El resultado debe ser Right (éxito)");
        result.peek(approved -> {
            assertNotNull(approved.transactionId());
            assertTrue(approved.transactionId().startsWith("TX-"));
        });
    }

    @Test
    @DisplayName("Debe rechazar pago cuando amount es null")
    void shouldRejectPaymentWhenAmountIsNull() {
        // Given
        var request = new PaymentRequestDto(
                "CUST-001",
                "ACC-001",
                "ACC-XYZ",
                null,
                Option.of("Pago inválido")
        );

        // When
        Either<ErrorDto, ApprovedDto> result = service.process(request);

        // Then
        assertTrue(result.isLeft(), "El resultado debe ser Left (error)");
        result.peekLeft(error -> {
            assertEquals("VALIDATION", error.code());
            assertEquals("amount debe ser > 0", error.message());
        });
    }

    @Test
    @DisplayName("Debe rechazar pago cuando amount es cero")
    void shouldRejectPaymentWhenAmountIsZero() {
        // Given
        var request = new PaymentRequestDto(
                "CUST-001",
                "ACC-001",
                "ACC-XYZ",
                BigDecimal.ZERO,
                Option.of("Pago cero")
        );

        // When
        Either<ErrorDto, ApprovedDto> result = service.process(request);

        // Then
        assertTrue(result.isLeft());
        result.peekLeft(error -> {
            assertEquals("VALIDATION", error.code());
            assertEquals("amount debe ser > 0", error.message());
        });
    }

    @Test
    @DisplayName("Debe rechazar pago cuando amount es negativo")
    void shouldRejectPaymentWhenAmountIsNegative() {
        // Given
        var request = new PaymentRequestDto(
                "CUST-001",
                "ACC-001",
                "ACC-XYZ",
                new BigDecimal("-50.00"),
                Option.of("Pago negativo")
        );

        // When
        Either<ErrorDto, ApprovedDto> result = service.process(request);

        // Then
        assertTrue(result.isLeft());
        result.peekLeft(error -> {
            assertEquals("VALIDATION", error.code());
        });
    }

    @Test
    @DisplayName("Debe rechazar pago cuando sourceAccount es ACC-002 (saldo insuficiente)")
    void shouldRejectPaymentWhenInsufficientBalance() {
        // Given
        var request = new PaymentRequestDto(
                "CUST-001",
                "ACC-002",
                "ACC-XYZ",
                new BigDecimal("100.00"),
                Option.of("Pago con saldo insuficiente")
        );

        // When
        Either<ErrorDto, ApprovedDto> result = service.process(request);

        // Then
        assertTrue(result.isLeft());
        result.peekLeft(error -> {
            assertEquals("BUSINESS", error.code());
            assertEquals("Saldo insuficiente", error.message());
        });
    }

    @Test
    @DisplayName("Debe aprobar pago sin concepto (Option.none)")
    void shouldApprovePaymentWithoutConcept() {
        // Given
        var request = new PaymentRequestDto(
                "CUST-001",
                "ACC-001",
                "ACC-XYZ",
                new BigDecimal("50.00"),
                Option.none()
        );

        // When
        Either<ErrorDto, ApprovedDto> result = service.process(request);

        // Then
        assertTrue(result.isRight());
    }

    @Test
    @DisplayName("Debe usar fold para transformar Either en String")
    void shouldUseFoldToTransformResult() {
        // Given
        var request = new PaymentRequestDto(
                "CUST-001",
                "ACC-001",
                "ACC-XYZ",
                new BigDecimal("100.00"),
                Option.of("Test fold")
        );

        // When
        Either<ErrorDto, ApprovedDto> result = service.process(request);
        String message = result.fold(
                error -> "ERROR: " + error.code() + " - " + error.message(),
                approved -> "SUCCESS: " + approved.transactionId()
        );

        // Then
        assertTrue(message.startsWith("SUCCESS: TX-"));
    }
}
