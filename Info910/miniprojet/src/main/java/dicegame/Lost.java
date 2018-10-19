package dicegame;

import messages.Message;

public class Lost extends Message {
    public Lost(Integer stamp, Integer sender) {
        super(null, stamp, null, sender);
    }
}
