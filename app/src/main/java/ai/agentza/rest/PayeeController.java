package ai.agentza.rest;

import ai.agentza.model.Agent;
import ai.agentza.model.AgentRepository;
import ai.agentza.model.payees.AgentPayee;
import ai.agentza.model.payees.Payee;
import ai.agentza.model.payees.PayeeRepository;
import ai.agentza.model.payees.USACHPayee;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/payees")
public class PayeeController {

    private final PayeeRepository payeeRepository;
    private final AgentRepository agentRepository;


    public PayeeController(PayeeRepository payeeRepository, AgentRepository agentRepository) {
        this.payeeRepository = payeeRepository;
        this.agentRepository = agentRepository;
    }

    @GetMapping("/")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of Payees", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Payee.List.class))}),
        @ApiResponse(responseCode = "403", description = "Not authorized"),
    })
    @Parameters(@Parameter(in = ParameterIn.HEADER, name = "X-API-KEY", schema = @Schema(type = "string")))
    public Payee.List listPayees(Principal principal) {
        return new Payee.List(payeeRepository.findByPayerAgentId(principal.getName()));
    }

    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of Payees", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Payee.class))}),
            @ApiResponse(responseCode = "403", description = "Not authorized"),
    })
    @Parameters(@Parameter(in = ParameterIn.HEADER, name = "X-API-KEY", schema = @Schema(type = "string")))
    public Optional<Payee> getPayee(@PathVariable String id) {
        return payeeRepository.findById(id);
    }

    @Operation(summary = "Create a new Payee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Payee created successfully", content = {@Content(mediaType = "application/json", schema = @Schema(oneOf = {AgentPayee.class, USACHPayee.class}))}),
        @ApiResponse(responseCode = "400", description = "Invalid input provided"),
        @ApiResponse(responseCode = "403", description = "Not authorized"),
    })
    @PostMapping("/")
    @Parameters(@Parameter(in = ParameterIn.HEADER, name = "X-API-KEY", schema = @Schema(type = "string")))
    public Payee createPayee(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payee to create", required = true, content = @Content(mediaType = "application/json",schema = @Schema(implementation = Payee.class)))
            @RequestBody Payee payee,
            Principal principal
    ) {
        if(payee instanceof AgentPayee agentPayee) {
            Optional<Agent> agent = agentRepository.findById(agentPayee.getAgentId());
            if(agent.isEmpty()) {
                throw new AgentNotFoundException(agentPayee.getAgentId());
            }
        }
        payee.setPayerAgentId(principal.getName());
        return payeeRepository.save(payee);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class AgentNotFoundException extends RuntimeException {
        public AgentNotFoundException(String agentId) {
            super("Agent with id " + agentId + " not found");
        }
    }
}
