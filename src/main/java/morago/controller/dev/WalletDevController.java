package morago.controller.dev;

import lombok.RequiredArgsConstructor;
import morago.service.WalletService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Profile("dev")
@RestController
@RequiredArgsConstructor
@RequestMapping("/dev/wallets")
public class WalletDevController {
    private final WalletService walletService;
    @PostMapping("/{userId}/top-up")
    public ResponseEntity<Void> devTopUp(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount
    ) {
        walletService.topUp(
                userId,
                amount,
                "DEV_TOP_UP"
        );

        return ResponseEntity.ok().build();
    }
}
