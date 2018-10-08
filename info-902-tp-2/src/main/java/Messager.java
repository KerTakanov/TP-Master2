import messages.Message;

public interface Messager {
    void send(Object payload, int to);
    void receive(Message message);
}
