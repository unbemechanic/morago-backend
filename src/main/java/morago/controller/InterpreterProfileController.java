package morago.controller;

import lombok.RequiredArgsConstructor;
import morago.dto.admin.StatusUpdateDto;
import morago.dto.interpreter.request.InterpreterProfileRequest;
import morago.security.CustomUserDetails;
import morago.service.InterpreterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("interpreter-profile")
@RequiredArgsConstructor
@PreAuthorize("hasRole('INTERPRETER')")
public class InterpreterProfileController {

    private final InterpreterService interpreterService;

    @PostMapping("/create/profile")
    public ResponseEntity<Void> createProfile(@RequestBody InterpreterProfileRequest request, @AuthenticationPrincipal CustomUserDetails user){
        Long userId = user.getId();
        interpreterService.createProfile(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/update/profile")
    public ResponseEntity<Void> updateProfile(
            @RequestBody InterpreterProfileRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ){
        Long userId = user.getId();
        interpreterService.updateProfile(userId, request);
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/update/status")
    public ResponseEntity<Void> updateStatus(
            @RequestBody StatusUpdateDto status,
            @AuthenticationPrincipal CustomUserDetails user){
        Long userId = user.getId();
        interpreterService.updateIsActive(status, userId);
        return  ResponseEntity.ok().build();
    }
}
