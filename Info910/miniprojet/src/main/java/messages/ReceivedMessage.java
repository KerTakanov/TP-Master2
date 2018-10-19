package messages;

public class ReceivedMessage extends Message {
    public ReceivedMessage(Integer to, Integer sender) {
        super(null, null, to, sender);
    }
}
