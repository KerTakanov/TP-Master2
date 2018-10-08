import messages.Message;

public interface Broadcaster {
    void broadcast(Object payload);
    void receive(Message message);
}
