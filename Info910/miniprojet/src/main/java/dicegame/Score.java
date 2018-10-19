package dicegame;

import lombok.Data;
import messages.Message;

public class Score extends Message {
    public Score(Object value, Integer stamp, Integer sender) {
        super(value, stamp, null, sender);
    }
}
