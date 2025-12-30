package morago.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import morago.customExceptions.UserNotFoundException;
import morago.customExceptions.call.CallTopicNotFoundException;
import morago.model.Call;
import morago.model.CallTopic;
import morago.model.client.ClientProfile;
import morago.model.interpreter.InterpreterProfile;
import morago.repository.ClientRepository;
import morago.repository.InterpreterProfileRepository;
import morago.repository.call.CallRepository;
import morago.repository.call.CallTopicRepository;
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

    public Call create(Long clientProfileId, Long interpreterProfileId, Long callTopicId) {

        ClientProfile client = clientRepository.findById(clientProfileId).orElseThrow(UserNotFoundException::new);
        InterpreterProfile interpreter = interpreterProfileRepository.findById(interpreterProfileId).orElseThrow(UserNotFoundException::new);
        CallTopic topic = callTopicRepository.findByTopicId(callTopicId).orElseThrow(CallTopicNotFoundException::new);

        Call call = new Call(client, interpreter, topic);

        log.info(
                "Call created clientProfileId={} interpreterProfileId={} topic={}",
                clientProfileId,
                interpreterProfileId,
                topic
        );
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
        return call;
    }

    @Transactional
    public Call accept(Long callId, Long actorId){
        Call call = get(callId);
        call.accept(actorId);
        log.info("Call accepted callId={} actorId={}", callId, actorId);
        return call;
    }

    @Transactional
    public Call reject(Long callId, Long actorId){
        Call call = get(callId);
        call.reject(actorId);
        log.info("Call rejected callId={} actorId={}", callId, actorId);
        return call;
    }

    @Transactional
    public Call cancel(Long callId, Long actorId){
        Call call = get(callId);
        call.cancel(actorId);
        log.info("Call cancelled callId={} actorId={}", callId, actorId);
        return call;
    }

    private Call get(Long id) {
        return callRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Call not found"));
    }
}
