package morago.service;

import lombok.RequiredArgsConstructor;
import morago.repository.InterpreterProfileRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterpreterService {
    private final InterpreterProfileRepository interpreterProfileRepository;

    public void updateIsActive(){

    }
}
