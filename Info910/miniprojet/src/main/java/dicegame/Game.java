package dicegame;

import lombok.Data;
import messages.SynchronizeMessage;

import java.util.ArrayList;
import java.util.List;

@Data
public class Game {
    private List<Player> players = new ArrayList<>();

    public Game(int nbPlayers) {
        for (int i = 0; i < nbPlayers; ++i) {
            players.add(new Player());
        }
    }

    public void launch() {
        players.forEach(Player::ready);
        players.get(0).getProcess().getCom().broadcast(new SynchronizeMessage());
        players.forEach(Player::launchDice);
    }
}
