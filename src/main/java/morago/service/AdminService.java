package morago.service;

import lombok.RequiredArgsConstructor;
import morago.customExceptions.UserNotFoundException;
import morago.customExceptions.call.CallTopicExistsException;
import morago.customExceptions.call.CallTopicNotFoundException;
import morago.customExceptions.call.NoCallTopicFoundException;
import morago.dto.call.topic.CallTopicDto;
import morago.model.CallTopic;
import morago.model.User;
import morago.repository.UserRepository;
import morago.repository.call.CallTopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final CallTopicRepository callTopicRepository;

    // Get all users
    public List<User> findAllUsers() {
        if (userRepository.findAll().isEmpty()) {
            throw new UserNotFoundException();
        }
        return userRepository.findAll();
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

    public List<CallTopic> getAllTopics(){
        if (callTopicRepository.findAll().isEmpty()) {
            throw new NoCallTopicFoundException();
        }
        return callTopicRepository.findAll();
    }
}
