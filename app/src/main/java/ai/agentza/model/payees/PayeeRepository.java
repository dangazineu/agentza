package ai.agentza.model.payees;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PayeeRepository extends CrudRepository<Payee, String> {
    List<Payee> findByPayerAgentId(String payerAgentId);
}
