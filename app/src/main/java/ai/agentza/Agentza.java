package ai.agentza;

import ai.agentza.model.payee.AgentPayee;
import ai.agentza.model.payee.PayeeRepository;
import ai.agentza.model.payee.USACHPayee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
public class Agentza {

	public static void main(String[] args) {
		SpringApplication.run(Agentza.class, args);
	}

}