package messages;

public class SyncBroadcastMessage extends Message {
    public SyncBroadcastMessage(Object payload, int stamp, Integer sender) {
        super(payload, stamp, null, sender);
    }
}
