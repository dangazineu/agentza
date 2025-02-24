package ai.agentza.model.payees;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("US_ACH")
public class USACHPayee extends Payee {
    private String routingNbr;
    private String accountNbr;

    protected USACHPayee() {}
    public USACHPayee(String payerAgentId, String routingNbr, String accountNbr) {
        super(payerAgentId);
        this.routingNbr = routingNbr;
        this.accountNbr = accountNbr;
    }

    public String getRoutingNbr() {
        return routingNbr;
    }

    public String getAccountNbr() {
        return accountNbr;
    }
}
