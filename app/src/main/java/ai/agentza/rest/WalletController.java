package ai.agentza.rest;

import ai.agentza.model.Wallet;
import ai.agentza.model.WalletRepository;
import ai.agentza.model.payee.Payee;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {

    private final WalletRepository walletRepository;
    public WalletController(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @GetMapping("/")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of wallets", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Wallet.class))}),
            @ApiResponse(responseCode = "403", description = "Not authorized"),
    })
    @Parameters(@Parameter(in = ParameterIn.HEADER, name = "X-API-KEY", schema = @Schema(type = "string")))
    public Iterable<Wallet> listWallets(Principal principal) {
        return walletRepository.findByAgentId(principal.getName());
    }

    @PostMapping("/")
    @Parameters(@Parameter(in = ParameterIn.HEADER, name = "X-API-KEY", schema = @Schema(type = "string")))
    public Wallet createWallet(
            @RequestBody String description,
            Principal principal
    ) {
        return walletRepository.save(
                new Wallet(
                        UUID.randomUUID().toString(),
                        principal.getName(),
                        description,
                        0.0
                )
        );
    }
}
