package ai.agentza.model.transactions;

import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<Transaction, String> {
    Iterable<Transaction> findByWalletId(String walletId);
}

