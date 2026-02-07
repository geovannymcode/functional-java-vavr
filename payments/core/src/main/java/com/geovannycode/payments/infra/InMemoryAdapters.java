package com.geovannycode.payments.infra;

import com.geovannycode.payments.app.PaymentService;
import com.geovannycode.payments.domain.PaymentRequest;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class InMemoryAdapters {

    public static PaymentService.BalancePort balancePort() {
        Map<String, BigDecimal> balances = Map.of(
                "ACC-001", new BigDecimal("150.00"),
                "ACC-002", new BigDecimal("20.00")
        );
        return account -> balances.getOrDefault(account, BigDecimal.ZERO);
    }

    public static PaymentService.TransferPort transferPort() {
        return (PaymentRequest req) -> "TX-" + UUID.randomUUID();
    }
}
