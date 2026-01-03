package morago.service;

import lombok.RequiredArgsConstructor;
import morago.dto.admin.client.InterpreterResponse;
import morago.dto.admin.interpreter.AdminIPResponseDto;
import morago.model.interpreter.InterpreterProfile;
import morago.repository.InterpreterProfileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final InterpreterProfileRepository interpreterProfileRepository;

    public Page<InterpreterResponse> findAllInterpreterProfiles(Pageable pageable) {
        Page<InterpreterProfile> profiles = interpreterProfileRepository.findAll(pageable);
        return profiles.map(InterpreterResponse::from);
    }
}
