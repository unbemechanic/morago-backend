package morago.customExceptions.call;

public class CallTopicExistsException extends RuntimeException {
    public CallTopicExistsException(String topicName){
        super("Topic with name " + topicName + " exists!");
    }
}
