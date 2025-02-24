package ai.agentza.model.payees;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value=AgentPayee.class, name = "AGENT"),
        @JsonSubTypes.Type(value=USACHPayee.class, name = "US_ACH")
})
public abstract class Payee {

    @Id @UuidGenerator private String payeeId;

    private String payerAgentId;

    protected Payee(){}

    protected Payee(String payerAgentId){ this.payerAgentId = payerAgentId; }

    public String getPayeeId() { return payeeId; }

    public String getPayerAgentId() { return payerAgentId; }

    @Override public int hashCode() { return payeeId.hashCode(); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Payee payee = (Payee) obj;
        return payeeId.equals(payee.payeeId);
    }

    public void setPayerAgentId(String payerAgentId) {
        this.payerAgentId = payerAgentId;
    }

    // This is a work around the fact that returning a List<Payee> from a REST controller will erase the root type
    // information, resulting in a missing "type" field in the JSON response.
    // Reference: https://groups.google.com/g/jackson-user/c/dY4iFZyeAX8
    public static class List extends ArrayList<Payee> {

        public List(Iterable<Payee> payees) {
            super();
            payees.forEach(this::add);
        }
    }
}
