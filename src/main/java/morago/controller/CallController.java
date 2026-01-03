package morago.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import morago.dto.call.CallActionRequest;
import morago.dto.call.CallCreateRequest;
import morago.dto.call.CallResponse;
import morago.security.CustomUserDetails;
import morago.service.CallService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/calls")
public class CallController {
    private final CallService callService;

//    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping
    public ResponseEntity<CallResponse> create(@RequestBody CallCreateRequest req, @AuthenticationPrincipal CustomUserDetails principal){
        Long clientId = principal.getId();

        log.info("Client ID : {}", clientId);
        return ResponseEntity.ok(
                CallResponse.from(
                        callService.create(
                                clientId,
                                req.calleeId(),
                                req.topicId())));
    }

//    @PreAuthorize("hasRole('INTERPRETER')")
    @PostMapping("/{id}/accept")
    public ResponseEntity<CallResponse> accept(
            @PathVariable Long id,
            @RequestBody CallActionRequest req){
        return ResponseEntity.ok(CallResponse.from(callService.accept(id, req.interpreterId())));
    }

//    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/reject")
    public ResponseEntity<CallResponse> reject(
            @PathVariable Long id,
            @RequestBody CallActionRequest req){
        return ResponseEntity.ok(CallResponse.from(callService.reject(id, req.interpreterId())));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<CallResponse> start(@PathVariable  Long id){
        return ResponseEntity.ok(CallResponse.from(callService.start(id)));
    }

    @PostMapping("/{id}/end")
    public ResponseEntity<CallResponse> end(@PathVariable  Long id){
        return ResponseEntity.ok(CallResponse.from(callService.end(id)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<CallResponse> cancel(
            @PathVariable  Long id,
            @RequestBody CallActionRequest req){
        return ResponseEntity.ok(CallResponse.from(callService.cancel(id, req.interpreterId())));
    }
}
