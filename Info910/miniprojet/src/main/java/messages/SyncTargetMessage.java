package messages;

public class SyncTargetMessage extends Message {
    public SyncTargetMessage(Object payload, int stamp, Integer to, Integer sender) {
        super(payload, stamp, to, sender);
    }
}
