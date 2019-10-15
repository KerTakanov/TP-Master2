package messages;

import lombok.Data;

@Data
public class Token {
    private int dest;

    public Token(int dest) {
        this.dest = dest;
    }
}
