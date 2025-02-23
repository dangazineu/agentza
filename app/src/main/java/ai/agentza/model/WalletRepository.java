package ai.agentza.model;

import ai.agentza.model.payee.Payee;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WalletRepository extends CrudRepository<Wallet, String> {
    List<Wallet> findByAgentId(String agentId);
}
