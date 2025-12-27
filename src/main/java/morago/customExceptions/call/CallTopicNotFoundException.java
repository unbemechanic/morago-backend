package morago.customExceptions.call;

public class CallTopicNotFoundException extends RuntimeException {
    public CallTopicNotFoundException(Long id) {
        super("No call topic found with id " + id);
    }
    public CallTopicNotFoundException(String topicName) {super("No call topic found with name " + topicName);}
}
