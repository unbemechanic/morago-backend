package morago.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import morago.customExceptions.UserNotFoundException;
import morago.customExceptions.call.CallNotFoundException;
import morago.customExceptions.call.SecurityWebRTCException;
import morago.dto.call.CallActionRequest;
import morago.dto.webrtc.WebRTCSignalingMessage;
import morago.enums.TokenEnum;
import morago.jwt.JWTService;
import morago.model.Call;
import morago.model.interpreter.InterpreterProfile;
import morago.repository.InterpreterProfileRepository;
import morago.repository.call.CallRepository;
import morago.service.CallService;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CallSignalingController {
    private final JWTService jWTService;
    private final InterpreterProfileRepository interpreterProfileRepository;
    private final CallService callService;
    private final SimpMessagingTemplate messagingTemplate;
    private final CallRepository callRepository;

    @MessageMapping("/webrtc.offer")
    @Transactional
    public void offer(@Payload WebRTCSignalingMessage message, Principal principal) {


        Call call = callRepository.findById(message.getCallId()).orElseThrow(CallNotFoundException::new);

        Long targetUserId = call.getInterpreterProfile().getUser().getId();
        Long clientId = call.getClientProfile().getUser().getId();
        Long senderId = Long.valueOf(principal.getName());

        if (!senderId.equals(clientId)) {
            throw new SecurityWebRTCException("Only client can send OFFER");
        }

        messagingTemplate.convertAndSendToUser(
                targetUserId.toString(),
                "/queue/webrtc-signals",
                message);
        log.info("Client {} sent OFFER --> interpreter {}", senderId, targetUserId);
    }

    @MessageMapping("/webrtc.answer")
    @Transactional
    public void answer(@Payload WebRTCSignalingMessage message, Principal principal) {
        Call call = callRepository.findById(message.getCallId()).orElseThrow(CallNotFoundException::new);

        Long targetUserId = call.getClientProfile().getUser().getId();
        Long senderId = Long.valueOf(principal.getName());
        if (!senderId.equals(call.getInterpreterProfile().getUser().getId())) {
            throw new SecurityWebRTCException("Only interpreter can create ANSWER");
        }

        messagingTemplate.convertAndSendToUser(
                targetUserId.toString(),
                "/queue/webrtc-signals",
                message
        );
        log.info("Interpreter {} created Answer for call {} to client {}",  principal.getName(), message.getCallId(), targetUserId);
    }

    @MessageMapping("/webrtc.ice")
    @Transactional
    public void ice(@Payload WebRTCSignalingMessage message, Principal principal) {
        callService.validateParticipants(message.getCallId(), principal);

        Call call = callRepository.findById(message.getCallId()).orElseThrow(CallNotFoundException::new);

        Long senderId = Long.valueOf(principal.getName());



        Long targetUserId = senderId.equals(call.getClientProfile().getUser().getId())
                ? call.getInterpreterProfile().getUser().getId() : call.getClientProfile().getUser().getId();
        messagingTemplate.convertAndSendToUser(
                targetUserId.toString(),
                "/queue/webrtc-signals",
                message
        );
    }
    @MessageMapping("/call.accept")
    public void accept(
            @Header("Authorization") String authorization,
            @Payload CallActionRequest req){
        String token = authorization.substring(7);
        Long userId = jWTService.extractUserId(token, TokenEnum.ACCESS);
        InterpreterProfile interpreter = interpreterProfileRepository.findByUserId(userId).orElseThrow(UserNotFoundException::new);
        callService.accept(req.callId(),  interpreter.getId());
        callService.start(req.callId());
    }

    @MessageMapping("/call.reject")
    public void reject(
            @Header("Authorization") String authorization,
            @Payload @Valid CallActionRequest req){
        String token = authorization.substring(7);
        Long interpreterId = jWTService.extractUserId(token, TokenEnum.ACCESS);
        callService.reject(req.callId(),  interpreterId);
    }

    @MessageMapping("/call.end")
    public void end(@Payload Long id, Principal principal) {
        callService.validateParticipants(id, principal);
        log.info("Call {} ended, principle={}", id, principal.getName());
        callService.end(id);
    }

    @MessageMapping("/call.cancel")
    public void cancel(
            @Header("Authorization")  String authorization,
            @Payload CallActionRequest req){
        String token = authorization.substring(7);
        Long clientId = jWTService.extractUserId(token, TokenEnum.ACCESS);
        callService.cancel(req.callId(), clientId);
    }
}
