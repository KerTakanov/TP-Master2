package messages;

import lombok.Data;

@Data
public abstract class Message {
    protected Object payload;
    protected Integer stamp;
    protected Integer dest;
    protected Integer sender;

    public Message(Object payload, Integer stamp, Integer to, Integer sender) {
        this.payload = payload;
        this.stamp = stamp;
        this.dest = to;
        this.sender = sender;
    }
}
