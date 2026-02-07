package com.geovannycode.payments;

import com.geovannycode.payments.app.PaymentService;
import com.geovannycode.payments.domain.PaymentRequest;
import com.geovannycode.payments.infra.InMemoryAdapters;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PaymentServiceTest {
    @Test
    void should_fail_when_insufficient_funds() {
        var service = new PaymentService(InMemoryAdapters.balancePort(), InMemoryAdapters.transferPort());

        var req = new PaymentRequest("CUST-1", "ACC-002", "ACC-XYZ", new BigDecimal("200.00"), "Pago");
        var result = service.process(req);

        assertTrue(result.isLeft());
        assertTrue(result.getLeft().message().contains("Saldo insuficiente"));
    }
}
