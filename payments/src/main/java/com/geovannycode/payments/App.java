package com.geovannycode.payments;

import com.geovannycode.payments.app.PaymentService;
import com.geovannycode.payments.domain.PaymentRequest;
import com.geovannycode.payments.infra.InMemoryAdapters;

import java.math.BigDecimal;

public class App {
    public static void main(String[] args) {
        var service = new PaymentService(
                InMemoryAdapters.balancePort(),
                InMemoryAdapters.transferPort()
        );

        var req = new PaymentRequest(
                "CUST-9",
                "ACC-001",
                "ACC-XYZ",
                new BigDecimal("80.00"),
                "Pago de seguro"
        );

        var result = service.process(req);

        /*System.out.println(
                result.fold(
                        err -> "ERROR: " + err.message(),
                        txId -> "OK: " + txId
                )
        );*/
    }
}
