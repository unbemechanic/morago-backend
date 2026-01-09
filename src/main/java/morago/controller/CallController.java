package morago.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import morago.dto.call.CallCreateRequest;
import morago.dto.call.CallResponse;
import morago.security.CustomUserDetails;
import morago.service.CallService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENT')")
@RequestMapping("/calls")
public class CallController {
    private final CallService callService;

    @PostMapping
    public ResponseEntity<CallResponse> create(@RequestBody CallCreateRequest req, @AuthenticationPrincipal CustomUserDetails principal){
        Long clientId = principal.getId();
        log.info("Create call payload: clientId={} interpreterId={}, topicId={}",
                clientId, req.calleeId(), req.topicId());

        log.info("Client ID : {}", clientId);
        return ResponseEntity.ok(
                CallResponse.from(
                        callService.create(
                                clientId,
                                req.calleeId(),
                                req.topicId())));
    }

}
