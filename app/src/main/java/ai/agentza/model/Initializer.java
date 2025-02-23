package ai.agentza.model;

import ai.agentza.model.payee.AgentPayee;
import ai.agentza.model.payee.PayeeRepository;
import ai.agentza.model.payee.USACHPayee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

// This class is temporary, only used to pre-populate the database with some data for querying.
@Component
public class Initializer {

    private final PayeeRepository payeeRepository;
    private final WalletRepository walletRepository;
    private final AgentRepository agentRepository;
    private final ApiKeyRepository apiKeyRepository;

    public Initializer(PayeeRepository payeeRepository, WalletRepository walletRepository, AgentRepository agentRepository, ApiKeyRepository apiKeyRepository) {
        this.payeeRepository = payeeRepository;
        this.walletRepository = walletRepository;
        this.agentRepository = agentRepository;
        this.apiKeyRepository = apiKeyRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void populatePayees() {
        List<String> agentIds = List.of("foo", "bar", "baz", "qux", "quux", "corge", "grault", "garply", "waldo", "fred", "plugh", "xyzzy", "thud");

        for (String agentId : agentIds) {
            Agent agent = agentRepository.save(new Agent(agentId, "Agent " + agentId.toUpperCase(), null));
            Wallet wallet = walletRepository.save(new Wallet(agentId + "-default-wallet", agentId, "Default wallet for " + agent.getName(), 1000.0));

            agent.setDefaultWalletId(wallet.getWalletId());
            agentRepository.save(agent);

            apiKeyRepository.save(new ApiKey("agent-" + agentId + "-key", agentId));
        }


        payeeRepository.save(new AgentPayee("foo", "bar"));
        payeeRepository.save(new USACHPayee("foo", "12345", "54321"));
    }
}
