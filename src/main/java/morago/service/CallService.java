package morago.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import morago.customExceptions.UserNotFoundException;
import morago.customExceptions.call.CallTopicNotFoundException;
import morago.enums.CallState;
import morago.model.Call;
import morago.model.CallTopic;
import morago.model.User;
import morago.model.client.ClientProfile;
import morago.model.interpreter.InterpreterProfile;
import morago.payload.CallEvent;
import morago.repository.ClientRepository;
import morago.repository.InterpreterProfileRepository;
import morago.repository.UserRepository;
import morago.repository.call.CallRepository;
import morago.repository.call.CallTopicRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallService {

    private static final BigDecimal RATE_PER_SECOND = new BigDecimal("0.05");

    private final CallRepository callRepository;
    private final WalletService walletService;
    private final InterpreterProfileRepository interpreterProfileRepository;
    private final ClientRepository  clientRepository;
    private final CallTopicRepository  callTopicRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate  simpMessagingTemplate;

    public Call create(Long clientProfileId, Long interpreterProfileId, Long callTopicId) {

        User user = userRepository.findById(clientProfileId).orElseThrow(UserNotFoundException::new);

        ClientProfile client = clientRepository.findByUserId(user.getId());
        InterpreterProfile interpreter = interpreterProfileRepository.findById(interpreterProfileId).orElseThrow(UserNotFoundException::new);
        CallTopic topic = callTopicRepository.findByTopicId(callTopicId).orElseThrow(CallTopicNotFoundException::new);

        Call call = new Call(client, interpreter, topic);

        log.info(
                "Call created clientProfileId={} interpreterProfileId={} topic={}",
                clientProfileId,
                interpreterProfileId,
                topic
        );

        notifyUser(interpreter.getUser().getId(), new CallEvent("CALL_CREATED", call.getId(), user.getId()));
        return callRepository.save(call);
    }

    @Transactional
    public Call start(Long callId){
        Call call = get(callId);
        call.start();
        log.info(
                "Call started callId={}",
                callId
        );
        notifyBoth(call, new CallEvent("CALL_STARTED", callId, null));
        return call;
    }

    @Transactional
    public Call end(Long callId){
        Call call = get(callId);
        BigDecimal ratePerSecond = call.getInterpreterProfile().getHourlyRatePerSecond();

        call.end(ratePerSecond);

        walletService.charge(
                call.getClientProfile().getUser().getId(),
                call.getTotalPrice(),
                "CALL" + callId
        );
        log.info("Call ended callId={} price={}", callId, call.getTotalPrice());
        notifyBoth(call, new CallEvent("CALL_ENDED", callId, null));
        return call;
    }

    @Transactional
    public Call accept(Long callId, Long interpreterId){
        Call call = get(callId);
        require(call, CallState.RINGING);
        requireInterpreter(call, interpreterId);
        call.accept(interpreterId);
        log.info("Call accepted callId={} actorId={}", callId, interpreterId);
        notifyUser(call.getClientProfile().getId(),
                new CallEvent("CALL_ACCEPTED", callId, interpreterId));
        return call;
    }

    @Transactional
    public Call reject(Long callId, Long actorId){
        Call call = get(callId);
        call.reject(actorId);
        log.info("Call rejected callId={} actorId={}", callId, actorId);

        notifyUser(call.getClientProfile().getUser().getId(), new CallEvent("CALL_REJECTED", callId, actorId));
        return call;
    }

    @Transactional
    public Call cancel(Long callId, Long clientId){
        Call call = get(callId);
        call.cancel(clientId);
        log.info("Call cancelled callId={} actorId={}", callId, clientId);
        notifyUser(call.getClientProfile().getUser().getId(), new CallEvent("CALL_CANCELLED", callId, clientId));
        return call;
    }

    private Call get(Long id) {
        return callRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Call not found"));
    }

    private void require(Call call, CallState callState) {
        if (call.getState() != callState) {
            throw new IllegalArgumentException("Call state not match");
        }
    }

    private void requireInterpreter(Call call, Long interpreterId) {
        if (!call.getInterpreterProfile().getId().equals(interpreterId)) {

        }
    }

    private void notifyUser(Long userId, CallEvent event){
        simpMessagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/calls",
                event
        );
    }

    private void notifyBoth(Call call, CallEvent event){
        Long clientId = call
                .getClientProfile().getUser().getId();

        Long  interpreterId = call
                .getInterpreterProfile().getUser().getId();

        notifyUser(clientId, event);
        notifyUser(interpreterId, event);
    }

}
