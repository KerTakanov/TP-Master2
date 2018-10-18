package messages;

import lombok.Data;

public class TargetMessage extends Message {
    public TargetMessage(Object payload, int stamp, Integer to, Integer sender) {
        super(payload, stamp, to, sender);
    }
}
