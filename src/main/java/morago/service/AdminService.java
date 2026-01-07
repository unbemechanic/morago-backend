package morago.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import morago.customExceptions.PhoneNumberAlreadyExistsException;
import morago.customExceptions.UserNotFoundException;
import morago.customExceptions.call.CallTopicExistsException;
import morago.customExceptions.call.CallTopicNotFoundException;
import morago.customExceptions.call.InvalidCallTopicException;
import morago.customExceptions.call.NoFieldsToUpdateException;
import morago.customExceptions.interpreter.NoInterpreterFoundException;
import morago.customExceptions.language.InvalidLanguageException;
import morago.customExceptions.language.LanguageExistsException;
import morago.customExceptions.language.NoLanguageFoundException;
import morago.customExceptions.password.WeakPasswordException;
import morago.customExceptions.role.RoleNotFoundException;
import morago.dto.admin.*;
import morago.dto.admin.client.ClientProfileDto;
import morago.dto.admin.client.CreateClientRequestDto;
import morago.dto.admin.interpreter.AdminIPResponseDto;
import morago.dto.admin.interpreter.AdminInterpreterProfileRequestDto;
import morago.dto.admin.interpreter.SingleInterpreterProfileDto;
import morago.dto.call.topic.CallTopicDto;
import morago.dto.language.LanguageRequestDto;
import morago.dto.notification.NotificationDto;
import morago.mapper.CallMapper;
import morago.mapper.UserMapper;
import morago.mapper.WithdrawalMapper;
import morago.mapper.WithdrawalRequestMapper;
import morago.model.Call;
import morago.model.CallTopic;
import morago.model.Role;
import morago.model.User;
import morago.model.client.ClientProfile;
import morago.model.interpreter.InterpreterProfile;
import morago.model.interpreter.InterpreterWithdrawalRequest;
import morago.model.interpreter.Language;
import morago.model.interpreter.Withdrawal;
import morago.repository.*;
import morago.repository.call.CallRepository;
import morago.repository.call.CallTopicRepository;
import morago.utils.PasswordValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final CallTopicRepository callTopicRepository;
    private final LanguageRepository languageRepository;
    private final RoleRepository roleRepository;
    private final InterpreterProfileRepository interpreterProfileRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CallRepository callRepository;
    private final WithdrawalRepository withdrawalRepository;
    private final InterpreterWithdrawalRequestRepository interpreterWithdrawalRequestRepository;
    private final CallMapper callMapper;
    private final WithdrawalRequestMapper withdrawalRequestMapper;
    private final WithdrawalMapper withdrawalMapper;
    private final DepositRepository depositRepository;
    private final ClientRepository clientRepository;
    private final NotificationRepository notificationRepository;

    // Get all users
    public Page<UserResponseDto> findAllUsers(Pageable pageable) {
        Page<User> allUsers = userRepository.findAll(pageable);
        if (allUsers.isEmpty()) {
            throw new UserNotFoundException();
        }
        return allUsers.map(userMapper::toUserResponseDto);
    }

    public User findUserById(Long id) {
        return userRepository.findUserById(id).orElseThrow(UserNotFoundException::new);
    }

    public void createNewInterpreterProfile(AdminInterpreterProfileRequestDto dto) {
        if(userRepository.existsByPhoneNumber(dto.getPhoneNumber())){
            throw new PhoneNumberAlreadyExistsException();
        }

        if(!PasswordValidator.isValid(dto.getPassword())){
            throw new WeakPasswordException();
        }

        // User creation
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setIsVerified(true);

        // Assign role
        Role interpreterRole = roleRepository.findRoleByName("INTERPRETER").orElseThrow(RoleNotFoundException::new);

        user.setRole(interpreterRole);
        user = userRepository.save(user);

        // Call topics
        Set<CallTopic> callTopics = new HashSet<>(
              callTopicRepository.findAllById(dto.getCallTopicIds())
        );

        if (callTopics.size() != dto.getCallTopicIds().size()) {
            throw new InvalidCallTopicException();
        }


        // Language assignment
        Set<Language> languages = new HashSet<>(
                languageRepository.findAllById(dto.getLanguageIds())
        );

        if (languages.size() != dto.getLanguageIds().size()) {
            throw new InvalidLanguageException();
        }

        // Create Interpreter profile
        InterpreterProfile interpreter = new InterpreterProfile();
        interpreter.setUser(user);
        interpreter.setLanguages(languages);
        interpreter.setCallTopics(callTopics);
        interpreter.setLevel(dto.getLevel());
        interpreter.setHourlyRate(dto.getHourlyRate());
        interpreter.setIsActive(true);

        interpreterProfileRepository.save(interpreter);
    }

    public Page<AdminIPResponseDto> findAllInterpreterProfiles(Pageable pageable) {
        Page<InterpreterProfile> profiles = interpreterProfileRepository.findAll(pageable);
        return profiles.map(this::buildAdminInterpreterDto);
    }

    public SingleInterpreterProfileDto getInterpreterProfileById(Long id) {
        User user = userRepository.findUserById(id).orElseThrow(UserNotFoundException::new);
        InterpreterProfile interpreterProfile = interpreterProfileRepository.findById(id).orElseThrow(NoInterpreterFoundException::new);

        SingleInterpreterProfileDto dto = new SingleInterpreterProfileDto();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());

        dto.setLevel(interpreterProfile.getLevel());
        dto.setHourlyRate(interpreterProfile.getHourlyRate());
        dto.setIsVerified(interpreterProfile.getIsActive());
        dto.setCallTopicIds(interpreterProfile.getCallTopics()
                .stream().map(CallTopic::getTopicId).collect(Collectors.toSet()));
        dto.setLanguageIds(interpreterProfile.getLanguages().stream().map(Language::getId).collect(Collectors.toSet()));

        return dto;
    }

    @Transactional
    public void deleteById(Long id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        userRepository.delete(user);
    }

    // Client Profile
    public void createClientProfile(CreateClientRequestDto client){
        if(userRepository.existsByPhoneNumber(client.getPhoneNumber())){
            throw new PhoneNumberAlreadyExistsException();
        }
        if(!PasswordValidator.isValid(client.getPassword())){
            throw new WeakPasswordException();
        }
        User user = new User();
        user.setFirstName(client.getFirstName());
        user.setLastName(client.getLastName());
        user.setPhoneNumber(client.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(client.getPassword()));

        Role clientRole = roleRepository.findRoleByName("CLIENT").orElseThrow(RoleNotFoundException::new);

        user.setRole(clientRole);
        user = userRepository.save(user);

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setUser(user);
        clientProfile.setIsActive(true);

        clientRepository.save(clientProfile);
    }

    public Page<ClientProfileDto> findAllClientProfiles(Pageable pageable) {
        return clientRepository.findAll(pageable).map(ClientProfileDto::from);
    }

    // Call topics
    public Optional<CallTopic> findById(Long id) {
        if (!callTopicRepository.existsById(id)) {
            throw new CallTopicNotFoundException(id);
        }
        return callTopicRepository.findByTopicId(id);
    }
    public Optional<CallTopic> findByName(String name) {
        if (!callTopicRepository.existsByTopicName(name)) {
            throw new CallTopicNotFoundException(name);
        }
        return callTopicRepository.findByTopicName(name);
    }

    public void createNewTopic(CallTopicDto dto){
        if (callTopicRepository.existsByTopicName(dto.getName())){
            throw new CallTopicExistsException(dto.getName());
        }

        CallTopic topic = new CallTopic();
        topic.setTopicName(dto.getName());
        topic.setIsActive(dto.getIsActive());

        callTopicRepository.save(topic);
    }

    public Page<CallTopic> getAllTopics(Pageable pageable) {
        Page<CallTopic> page = callTopicRepository.findAll(pageable);
        if (page.isEmpty()){
            throw new CallTopicNotFoundException();
        }
        return page;
    }

    public void updateTopic(Long id, String newName, Boolean isActive){

        boolean hasName = newName != null && !newName.isBlank();
        boolean hasActive = isActive != null;

        if (!hasName && !hasActive) {
            throw new NoFieldsToUpdateException();
        }

        CallTopic topic = callTopicRepository.findById(id)
                .orElseThrow(() -> new CallTopicNotFoundException(id));

        if (hasName) {
            boolean exists = callTopicRepository
                    .existsByTopicNameIgnoreCaseAndTopicIdNot(newName.trim(), id);

            if (exists) {
                throw new CallTopicExistsException(newName);
            }

            topic.setTopicName(newName.trim());
        }

        if (hasActive) {
            topic.setIsActive(isActive);
        }
        callTopicRepository.save(topic);
    }

    public  void deleteTopicById(Long id){
        if (!callTopicRepository.existsById(id)) {
            throw new CallTopicNotFoundException(id);
        }
        callTopicRepository.deleteById(id);
    }

    public void createNewLanguage(LanguageRequestDto language) {
        if(languageRepository.existsByNameIgnoreCase(language.getName())){
            throw new LanguageExistsException();
        }

        Language newLanguage = new Language();
        newLanguage.setName(language.getName());

        languageRepository.save(newLanguage);
    }

    public Page<Language> findAllLanguages(Pageable pageable){
        Page<Language> page = languageRepository.findAll(pageable);
        if (page.isEmpty()){
            throw new NoLanguageFoundException();
        }

        return page;
    }

    public void updateLanguage(Long id, String newName){
        Language language = languageRepository.findById(id).orElseThrow(NoLanguageFoundException::new);
        language.setName(newName);
        languageRepository.save(language);
    }

    public void deleteLanguage(Long id){
        if (!languageRepository.existsById(id)) {
            throw new NoLanguageFoundException();
        }
        languageRepository.deleteById(id);
    }

    // Notification
    public List<NotificationDto> getAdminNotifications(){

        return notificationRepository.findByUser_Roles_Name("ADMIN")
                .stream()
                .map(NotificationDto::from)
                .toList();

    }

    // Aggregation method
    private AdminIPResponseDto buildAdminInterpreterDto(InterpreterProfile profile){
        User user = profile.getUser();
        List<Call> calls = callRepository.findAllByInterpreterProfileId(profile.getId());


        List<InterpreterWithdrawalRequest> withdrawalRequests =
                interpreterWithdrawalRequestRepository.findAllByInterpreterProfileId(profile.getId());

        List<Withdrawal> withdrawals = withdrawalRepository.findAllByInterpreterProfileId(profile.getId());

        return AdminIPResponseDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .isVerified(user.getIsVerified())
                .level(profile.getLevel())
                .calls(callMapper.toDtoList(calls))
                .withdrawalRequest(withdrawalRequestMapper.toDtoList(withdrawalRequests))
                .withdraw(withdrawalMapper.toDtoList(withdrawals))
                .build();
    }
}
