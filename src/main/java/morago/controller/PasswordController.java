package morago.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import morago.dto.password.request.PasswordResetConfirmRequest;
import morago.dto.password.request.PasswordResetRequest;
import morago.dto.password.request.PasswordResetVerityRequest;
import morago.service.PasswordResetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/password")
public class PasswordController {
    private final PasswordResetService passwordResetService;

    @Operation(
            summary = "Password reset request",
            description = "Users can request reset-password with phone number",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "No content",
                            content = @Content(schema = @Schema(implementation = PasswordResetRequest.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "User with requested number not found"),

            }
    )
    @PostMapping("/reset/request")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void request(@RequestBody PasswordResetRequest dto) {
        log.info("Password reset requested for {}", dto);
        passwordResetService.startReset(dto.phone());
    }

    @Operation(
            summary = "Password reset verify",
            description = "Users can verify reset-password with phone number and OTP code. Then user receives reset token",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ok",
                            content = @Content(schema = @Schema(implementation = PasswordResetVerityRequest.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "AUTH_INVALID_RESET_CODE"),
                    @ApiResponse(responseCode = "404", description = "No user found is phone number is incorrect")
            }
    )
    @PostMapping("/reset/verify")
    public Map<String, String> verify(@Valid @RequestBody PasswordResetVerityRequest dto) {
        String token = passwordResetService.verifyCode(dto.phoneNumber(), dto.code());
        log.info("Password reset verity requested for {}", dto);
        return Map.of("token", token);
    }

    @Operation(
            summary = "Password reset confirm",
            description = "Confirms new passwords with token new password and confirm new password",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "No content",
                            content = @Content(schema = @Schema(implementation = PasswordResetConfirmRequest.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Wrong Reset Password Token"
                            + "Passwords do not match" + "Password is required"),
            }
    )
    @PostMapping("/reset/confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void confirm(@Valid @RequestBody PasswordResetConfirmRequest dto) {
        log.info("Password reset confirm for {} completed successfully", dto);
        passwordResetService.confirmReset(dto.token(), dto.newPassword(), dto.newConfirmPassword());
    }
}
