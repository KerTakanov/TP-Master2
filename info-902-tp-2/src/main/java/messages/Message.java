package messages;

import lombok.Data;

@Data
public class Message {
    private Object payload;
    private int stamp;
    private Integer dest;

    public Message(Object payload, int stamp, Integer to) {
        this.payload = payload;
        this.stamp = stamp;
        dest = to;
    }
}
