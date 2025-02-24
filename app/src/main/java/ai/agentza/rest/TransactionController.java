package ai.agentza.rest;

import ai.agentza.model.AgentRepository;
import ai.agentza.model.WalletRepository;
import ai.agentza.model.payees.AgentPayee;
import ai.agentza.model.payees.PayeeRepository;
import ai.agentza.model.transactions.Deposit;
import ai.agentza.model.transactions.Transaction;
import ai.agentza.model.transactions.TransactionRepository;
import ai.agentza.model.transactions.Withdrawal;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/v1")
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final AgentRepository agentRepository;
    private final PayeeRepository payeeRepository;

    public TransactionController(TransactionRepository transactionRepository, WalletRepository walletRepository, AgentRepository agentRepository, PayeeRepository payeeRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.agentRepository = agentRepository;
        this.payeeRepository = payeeRepository;
    }

    @GetMapping("/wallets/{walletId}/transactions")
    @Parameters(@Parameter(in = ParameterIn.HEADER, name = "X-API-KEY", schema = @Schema(type = "string")))
    public List<TransactionResponse> listTransactions(
            @PathVariable String walletId,
            Principal principal
    ) {
        var wallet = walletRepository.findById(walletId).orElseThrow();
        if (!wallet.getAgentId().equals(principal.getName())) {
            // TODO implement this as a filter in the repository
            throw new RuntimeException("Unauthorized");
        }

        return StreamSupport.stream(transactionRepository.findByWalletId(walletId).spliterator(), false).map(transaction -> {
            System.out.println("Transaction: " + transaction);
            if (transaction instanceof Deposit deposit) {
                return new TransactionResponse(
                        deposit.getTransactionId(),
                        deposit.getTimestamp(),
                        deposit.getWalletId(),
                        deposit.getAmount(),
                        deposit.getCurrency(),
                        deposit.getStatus(),
                        "DEPOSIT",
                        deposit.getPayerDescription()
                );
            } else if (transaction instanceof Withdrawal withdrawal) {
                return new TransactionResponse(
                        withdrawal.getTransactionId(),
                        withdrawal.getTimestamp(),
                        withdrawal.getWalletId(),
                        withdrawal.getAmount(),
                        withdrawal.getCurrency(),
                        withdrawal.getStatus(),
                        "WITHDRAWAL",
                        withdrawal.getPayeeId()
                );
            } else {
                throw new RuntimeException("Unsupported transaction type");
            }
        }).toList();
    }

    @PostMapping("/wallets/{walletId}/transactions")
    @Parameters(@Parameter(in = ParameterIn.HEADER, name = "X-API-KEY", schema = @Schema(type = "string")))
    public TransactionResponse createTransaction(
            @PathVariable String walletId,
            @RequestBody TransactionRequest request,
            Principal principal
    ) {

        if(request.amount() <= 0){
            throw new RuntimeException("Invalid amount");
        }

        var payer = agentRepository.findById(principal.getName()).orElseThrow();

        var wallet = walletRepository.findById(walletId).orElseThrow();
        if (!wallet.getAgentId().equals(principal.getName())) {
            // TODO implement this as a filter in the repository
            throw new RuntimeException("Unauthorized");
        }

        var payee = payeeRepository.findById(request.payeeId()).orElseThrow();
        if (!payee.getPayerAgentId().equals(principal.getName())) {
            // TODO implement this as a filter in the repository
            throw new RuntimeException("Unauthorized");
        }

        if (payee instanceof AgentPayee agentPayee) {
            var payeeWallet = walletRepository.findById(
                    agentPayee.getWalletId() != null ? agentPayee.getWalletId() : agentRepository.findById(agentPayee.getAgentId()).orElseThrow().getDefaultWalletId()
            ).orElseThrow();

            if (!payeeWallet.getCurrency().equals(wallet.getCurrency())) {
                throw new RuntimeException("Currency mismatch");
            }

            if (wallet.getBalance() < request.amount()) {
                throw new RuntimeException("Insufficient funds");
            }

            payeeWallet.setBalance(payeeWallet.getBalance() + request.amount());
            walletRepository.save(payeeWallet);
            transactionRepository.save(new Deposit(
                    LocalDateTime.now(),
                    payeeWallet.getWalletId(),
                    request.amount(),
                    payeeWallet.getCurrency(),
                    Transaction.TransactionStatus.COMPLETED,
                    payer.getName()
            ));

            wallet.setBalance(wallet.getBalance() - request.amount());
            walletRepository.save(wallet);
            var withdrawal = transactionRepository.save(new Withdrawal(
                    LocalDateTime.now(),
                    wallet.getWalletId(),
                    -request.amount(),
                    wallet.getCurrency(),
                    Transaction.TransactionStatus.COMPLETED,
                    payee.getPayeeId()
            ));

            return new TransactionResponse(
                    withdrawal.getTransactionId(),
                    withdrawal.getTimestamp(),
                    withdrawal.getWalletId(),
                    withdrawal.getAmount(),
                    withdrawal.getCurrency(),
                    withdrawal.getStatus(),
                    "WITHDRAWAL",
                    withdrawal.getPayeeId()
            );
        } else {
            throw new RuntimeException("Unsupported payee type");
        }
    }

    public record TransactionRequest (String payeeId, Double amount){}
    public record TransactionResponse (String transactionId, LocalDateTime timestamp, String walletId, Double amount, String currency, Transaction.TransactionStatus status, String type, String payeeId){}
}
