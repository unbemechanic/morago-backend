package morago.controller;

import lombok.RequiredArgsConstructor;
import morago.dto.call.CallActionRequest;
import morago.dto.call.CallCreateRequest;
import morago.dto.call.CallResponse;
import morago.service.CallService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calls")
public class CallController {
    private final CallService callService;

    @PostMapping
    public ResponseEntity<CallResponse> create(@RequestBody CallCreateRequest req){
        return ResponseEntity.ok(
                CallResponse.from(
                        callService.create(
                                req.callerId(),
                                req.calleeId(),
                                req.topicId())));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<CallResponse> accept(
            @PathVariable Long id,
            @RequestBody CallActionRequest req){
        return ResponseEntity.ok(CallResponse.from(callService.accept(id, req.actorId())));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<CallResponse> reject(
            @PathVariable Long id,
            @RequestBody CallActionRequest req){
        return ResponseEntity.ok(CallResponse.from(callService.reject(id, req.actorId())));
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
        return ResponseEntity.ok(CallResponse.from(callService.cancel(id, req.actorId())));
    }
}
