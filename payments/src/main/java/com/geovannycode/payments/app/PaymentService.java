package com.geovannycode.payments.app;

import com.geovannycode.payments.domain.BusinessError;
import com.geovannycode.payments.domain.DomainError;
import com.geovannycode.payments.domain.InfraError;
import com.geovannycode.payments.domain.PaymentRequest;
import com.geovannycode.payments.domain.ValidationError;
import io.vavr.control.Either;
import io.vavr.control.Try;

import java.math.BigDecimal;

public class PaymentService {

    private final PaymentValidator validator = new PaymentValidator();
    private final BalancePort balancePort;
    private final TransferPort transferPort;

    public PaymentService(BalancePort balancePort, TransferPort transferPort) {
        this.balancePort = balancePort;
        this.transferPort = transferPort;
    }

    public Either<DomainError, String> process(PaymentRequest req) {
        // 1) Validación acumulando errores
        var validated = validator.validate(req);
        if (validated.isInvalid()) {
            var msg = validated.getError().map(ValidationError::message).mkString("; ");
            return Either.left(new ValidationError(msg));
        }

        // 2) Reglas de negocio + IO controlado
        return Try.of(() -> balancePort.getAvailable(req.sourceAccount()))
                .toEither()
                .mapLeft(ex -> (DomainError) new InfraError("Fallo consultando saldo: " + ex.getMessage()))
                .flatMap(balance -> hasFunds(balance, req.amount()))
                .flatMap(ok -> Try.of(() -> transferPort.transfer(req))
                        .toEither()
                        .mapLeft(ex -> (DomainError) new InfraError("Fallo en transferencia: " + ex.getMessage())));
    }

    private Either<DomainError, Boolean> hasFunds(BigDecimal balance, BigDecimal amount) {
        if (balance.compareTo(amount) >= 0) return Either.right(true);
        return Either.left(new BusinessError("Saldo insuficiente"));
    }

    public interface BalancePort {
        BigDecimal getAvailable(String account);
    }

    public interface TransferPort {
        String transfer(PaymentRequest req); // retorna id de transacción
    }
}
