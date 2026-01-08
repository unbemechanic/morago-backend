package morago.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import morago.customExceptions.UserNotFoundException;
import morago.customExceptions.call.InvalidCallTopicException;
import morago.customExceptions.interpreter.ProfileExistsException;
import morago.customExceptions.language.InvalidLanguageException;
import morago.dto.admin.StatusUpdateDto;
import morago.dto.interpreter.request.InterpreterProfileRequest;
import morago.model.CallTopic;
import morago.model.User;
import morago.model.interpreter.InterpreterProfile;
import morago.model.interpreter.Language;
import morago.repository.InterpreterProfileRepository;
import morago.repository.LanguageRepository;
import morago.repository.UserRepository;
import morago.repository.call.CallTopicRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class InterpreterService {
    private final InterpreterProfileRepository interpreterProfileRepository;
    private final UserRepository userRepository;
    private final CallTopicRepository callTopicRepository;
    private final LanguageRepository languageRepository;
    private final NotificationService notificationService;

    public void updateIsActive(StatusUpdateDto status, Long userId){
        InterpreterProfile profile = interpreterProfileRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        profile.setIsActive(status.getStatus());
        interpreterProfileRepository.save(profile);
    }

    @Transactional
    public void createProfile(InterpreterProfileRequest profile, Long userId){
        User user = userRepository.findUserById(userId).orElseThrow(UserNotFoundException::new);

        user.setFirstName(profile.getFirstName());
        user.setLastName(profile.getLastName());


        Set<CallTopic> topics = new HashSet<>(
                callTopicRepository.findAllById(profile.getCallTopicIds())
        );

        if(topics.size() != profile.getCallTopicIds().size()){
            throw new InvalidCallTopicException();
        }

        Set<Language> languageIds = new HashSet<>(
                languageRepository.findAllById(profile.getLanguageIds())
        );

        if (languageIds.size() != profile.getLanguageIds().size()) {
            throw new InvalidLanguageException();
        }

        if(interpreterProfileRepository.existsByUserId(userId)){
            throw new ProfileExistsException("Interpreter profile already exists");
        }

        InterpreterProfile interpreterProfile = InterpreterProfile.builder()
                .user(user)
                .callTopics(topics)
                .hourlyRate(profile.getHourlyRate())
                .languages(languageIds)
                .level(profile.getLevel())
                .build();
        interpreterProfileRepository.save(interpreterProfile);
        notificationService.notifyAdminUserProfileFinish(user);
    }

    @Transactional
    public void updateProfile(Long userId, InterpreterProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        InterpreterProfile profile =
                interpreterProfileRepository.findByUserId(userId)
                        .orElseThrow(UserNotFoundException::new);

        if(request.getFirstName() != null){
            user.setFirstName(request.getFirstName());
        }
        if(request.getLastName() != null){
            user.setLastName(request.getLastName());
        }

        profile.update(
                request.getLevel(),
                request.getHourlyRate(),
                request.getCallTopicIds() == null ? null :
                        new HashSet<>(callTopicRepository.findAllById(request.getCallTopicIds())),
                request.getLanguageIds() == null ? null :
                        new HashSet<>(languageRepository.findAllById(request.getLanguageIds()))
        );
    }
}
