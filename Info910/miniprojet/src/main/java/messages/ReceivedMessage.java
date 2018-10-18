package messages;

public class BroadcastMessage extends Message {
    public BroadcastMessage(Object payload, int stamp, Integer sender) {
        super(payload, stamp, null, sender);
    }
}
