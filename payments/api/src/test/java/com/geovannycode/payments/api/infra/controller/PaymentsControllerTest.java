package com.geovannycode.payments.api.infra.controller;

import com.geovannycode.payments.api.domain.PaymentRequestDto;
import com.geovannycode.payments.api.domain.PaymentResponseDto;
import io.vavr.control.Option;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("PaymentsController - Tests de integración")
class PaymentsControllerTest {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    @DisplayName("POST /api/payments debe retornar 200 OK cuando el pago es aprobado")
    void shouldReturn200WhenPaymentIsApproved() {
        // Given
        var request = new PaymentRequestDto(
                "CUST-001",
                "ACC-001",
                "ACC-XYZ",
                new BigDecimal("100.00"),
                Option.of("Pago de servicio")
        );

        // When
        PaymentResponseDto response = restClient.post()
                .uri("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(PaymentResponseDto.class);

        // Then
        assertNotNull(response);
        assertTrue(response.result().isRight());
        response.result().peek(approved -> {
            assertNotNull(approved.transactionId());
            assertTrue(approved.transactionId().startsWith("TX-"));
        });
    }

    @Test
    @DisplayName("POST /api/payments debe retornar 400 Bad Request cuando amount es negativo")
    void shouldReturn400WhenAmountIsNegative() {
        // Given
        var request = new PaymentRequestDto(
                "CUST-001",
                "ACC-001",
                "ACC-XYZ",
                new BigDecimal("-10.00"),
                Option.of("Pago inválido")
        );

        // When/Then
        try {
            restClient.post()
                    .uri("/api/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(PaymentResponseDto.class);
            fail("Debería haber lanzado excepción");
        } catch (Exception e) {
            // Esperamos un error 400
            assertTrue(e.getMessage().contains("400") || e.getMessage().contains("Bad Request"));
        }
    }

    @Test
    @DisplayName("POST /api/payments debe retornar 400 Bad Request cuando amount es cero")
    void shouldReturn400WhenAmountIsZero() {
        // Given
        var request = new PaymentRequestDto(
                "CUST-001",
                "ACC-001",
                "ACC-XYZ",
                BigDecimal.ZERO,
                Option.of("Pago cero")
        );

        // When/Then
        try {
            restClient.post()
                    .uri("/api/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(PaymentResponseDto.class);
            fail("Debería haber lanzado excepción");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("400") || e.getMessage().contains("Bad Request"));
        }
    }

    @Test
    @DisplayName("POST /api/payments debe retornar 422 cuando sourceAccount es ACC-002")
    void shouldReturn422WhenInsufficientBalance() {
        // Given
        var request = new PaymentRequestDto(
                "CUST-001",
                "ACC-002",
                "ACC-XYZ",
                new BigDecimal("100.00"),
                Option.of("Pago con saldo insuficiente")
        );

        // When/Then
        try {
            restClient.post()
                    .uri("/api/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(PaymentResponseDto.class);
            fail("Debería haber lanzado excepción");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("422") || e.getMessage().contains("Unprocessable"));
        }
    }

    @Test
    @DisplayName("POST /api/payments debe manejar request sin concepto")
    void shouldHandleRequestWithoutConcept() {
        // Given
        var request = new PaymentRequestDto(
                "CUST-001",
                "ACC-001",
                "ACC-XYZ",
                new BigDecimal("50.00"),
                Option.none()
        );

        // When
        PaymentResponseDto response = restClient.post()
                .uri("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(PaymentResponseDto.class);

        // Then
        assertNotNull(response);
        assertTrue(response.result().isRight());
    }
}
