package ai.agentza.model;

import java.time.LocalDateTime;

public record Transaction(
        String id,
        String payerId,
        String payeeId,
        Double amount,
        String currency,
        LocalDateTime timestamp,
        TransactionStatus status
) {
    public enum TransactionStatus {
        PENDING,
        COMPLETED,
        FAILED
    }
}


