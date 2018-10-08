package messages;

import lombok.Data;

@Data
public class Message {
    private Object payload;
    private int stamp;
    private Integer dest;
    private Integer sender;

    public Message(Object payload, int stamp, Integer to, Integer sender) {
        this.payload = payload;
        this.stamp = stamp;
        dest = to;
        this.sender = sender;
    }
}
