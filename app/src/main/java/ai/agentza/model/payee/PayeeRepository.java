package ai.agentza.model.payee;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PayeeRepository extends CrudRepository<Payee, String> {
    List<Payee> findByPayerAgentId(String payerAgentId);
}
