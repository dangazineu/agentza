package ai.agentza.model;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WalletRepository extends CrudRepository<Wallet, String> {
    List<Wallet> findByAgentId(String agentId);
}
