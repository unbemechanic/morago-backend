package morago.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import morago.customExceptions.UserNotFoundException;
import morago.customExceptions.call.CallTopicNotFoundException;
import morago.enums.CallState;
import morago.model.Call;
import morago.model.CallTopic;
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


    private final CallRepository callRepository;
    private final WalletService walletService;
    private final InterpreterProfileRepository interpreterProfileRepository;
    private final ClientRepository  clientRepository;
    private final CallTopicRepository  callTopicRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate  simpMessagingTemplate;

    public Call create(Long clientProfileId, Long interpreterProfileId, Long callTopicId) {

        ClientProfile client = clientRepository.findByUserId(clientProfileId);
        InterpreterProfile interpreter = interpreterProfileRepository.findById(interpreterProfileId).orElseThrow(UserNotFoundException::new);
        CallTopic topic = callTopicRepository.findByTopicId(callTopicId).orElseThrow(CallTopicNotFoundException::new);

        Call call = new Call(client, interpreter, topic);
        call = callRepository.save(call);

        log.info(
                "Call created callId={} clientProfileId={} interpreterProfileId={} topic={}",
                call.getId(),
                clientProfileId,
                interpreterProfileId,
                topic
        );
        //Interpreter receive call notification
        notifyUser(
                interpreter.getUser().getId(),
                new CallEvent(
                        "INCOMING_CALL",
                        call.getId(),
                        interpreterProfileId,
                        clientProfileId,
                        client.getUser().getFirstName(),
                        interpreter.getUser().getLastName()));

        // Client call notification
        notifyUser(
                client.getUser().getId(),
                new CallEvent(
                        "OUTGOING_CALL",
                        call.getId(),
                        interpreterProfileId,
                        clientProfileId,
                        client.getUser().getFirstName(),
                        interpreter.getUser().getLastName()));

        return call;
    }

    @Transactional
    public void start(Long callId){
        Call call = get(callId);

        if (!call.canStart()){
            return;
        }
        call.start();
        log.info(
                "Call started callId={}",
                callId
        );
        Long clientId = call.getClientProfile().getUser().getId();
        Long interpreterId = call.getInterpreterProfile().getUser().getId();
        notifyBoth(call,
                new CallEvent(
                        "CALL_STARTED",
                        callId,
                        interpreterId,
                        clientId,
                        null,
                        null));
    }

    @Transactional
    public void end(Long callId){
        Call call = get(callId);
        BigDecimal ratePerSecond = call.getInterpreterProfile().getHourlyRatePerSecond();

        call.end(ratePerSecond);

        walletService.charge(
                call.getClientProfile().getUser(),
                call.getInterpreterProfile().getUser(),
                call.getTotalPrice(),
                "CALL" + callId
        );
        log.info("Call ended callId={} price={}", callId, call.getTotalPrice());
        notifyBoth(call,
                new CallEvent(
                        "CALL_ENDED",
                        callId,
                        null,
                        null,
                        null,
                        null));
    }

    @Transactional
    public Call accept(Long callId, Long interpreterId){
        Call call = get(callId);
        log.info("Call state={}, required={}", call.getState(), CallState.CREATED);
        require(call, CallState.CREATED);
        requireInterpreter(call, interpreterId);
        call.accept(interpreterId);
        log.info("Call accepted callId={} actorId={}", callId, interpreterId);
        notifyBoth(call,
                new CallEvent(
                        "CALL_ACCEPTED",
                        callId, interpreterId,
                        call.getClientProfile().getUser().getId(),
                        call.getClientProfile().getUser().getFirstName(),
                        call.getInterpreterProfile().getUser().getLastName()));
        return call;
    }

    @Transactional
    public Call reject(Long callId, Long actorId){
        Call call = get(callId);
        Long intId = interpreterProfileRepository.findByUserId(actorId).orElseThrow(UserNotFoundException::new).getId();
        call.reject(intId);
        log.info("Call rejected callId={} actorId={}", callId, actorId);

        notifyBoth(
                call,
                new CallEvent(
                        "CALL_REJECTED",
                        callId,
                        actorId,
                        null,
                        null,
                        null));
        return call;
    }

    @Transactional
    public void cancel(Long callId, Long actorId){
        Call call = get(callId);
        Long clientId = clientRepository.findByUserId(actorId).getId();
        call.cancel(clientId);
        log.info("Call cancelled callId={} actorId={}", callId, clientId);
        notifyBoth(call,
                new CallEvent(
                        "CALL_CANCELLED",
                        callId,
                        null,
                        clientId,
                        null,
                        null));
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

    private void notifyUser(Long username, CallEvent event){
        log.info("Username={} event={}", username, event);
        simpMessagingTemplate.convertAndSendToUser(
                username.toString(),
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
