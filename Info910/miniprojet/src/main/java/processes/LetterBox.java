package processes;

import lombok.Data;
import messages.Message;

import java.util.ArrayList;
import java.util.List;

@Data
public class LetterBox {
    public List<Message> messages = new ArrayList<>();

    public void post(Message message) {
        synchronized (messages) {
            messages.add(message);
        }
    }

    public Message get() {
        if (messages.size() > 0)
            return messages.remove(0);
        else
            return null;
    }
}
