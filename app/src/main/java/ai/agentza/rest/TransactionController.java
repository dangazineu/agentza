package ai.agentza.rest;

import ai.agentza.model.Transaction;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1")
public class TransactionController {
    @GetMapping("/transactions/{id}")
    @Parameters(@Parameter(in = ParameterIn.HEADER, name = "X-API-KEY", schema = @Schema(type = "string")))
    public Transaction getTransaction(@PathVariable(value = "id") String transactionId) {
        return new Transaction(
                transactionId,
                "payerId",
                "payeeId",
                100.0,
                "USD",
                LocalDateTime.now(),
                Transaction.TransactionStatus.PENDING
        );
    }
}
