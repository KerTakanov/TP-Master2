package dicegame;

import com.google.common.base.Charsets;
import com.google.common.eventbus.Subscribe;
import com.google.common.io.CharSink;
import com.google.common.io.FileWriteMode;
import lombok.Data;
import messages.SynchronizeMessage;
import processes.EventBusService;
import processes.Process;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Data
public class Player {
    private Process process = new Process();
    private Dice dice = new Dice();
    private EventBusService bus;

    private int score;

    private Map<Integer, Boolean> playersHighestStatus;

    public Player() {
        this.bus = EventBusService.getInstance();
        this.bus.registerSubscriber(this);

        process.init();
    }

    public void ready() {
        playersHighestStatus = new HashMap<>();
        for (Process oprocess : Process.processes) {
            playersHighestStatus.put(oprocess.getId(), true);
        }
    }

    public void launchDice() {
        System.out.println(String.format("%d launching dice", process.getId()));

        score = dice.next(200);
        process.getCom().broadcast(new Score(score, process.clock(), process.getId()));
    }

    @Subscribe
    public void onScore(Score score) {
        if (score.getSender() == process.getId()) {
            return;
        }

        if (this.score < (int) score.getPayload()) {
            playersHighestStatus.put(process.getId(), false);
            process.getCom().broadcast(new Lost(process.clock(), process.getId()));
        } else if (this.score == (int) score.getPayload()) {
            launchDice();
        }
    }

    @Subscribe
    public void onLost(Lost lost) {
        if (lost.getSender() == process.getId()) {
            return;
        }

        playersHighestStatus.put(lost.getSender(), false);

        if (playersHighestStatus.get(process.getId())) {
            for (Process oprocess : Process.processes) {
                if (oprocess.getId() != process.getId() && playersHighestStatus.get(oprocess.getId())) {
                    return;
                }
            }

            System.out.println("Got a winner");

            process.getCom().requestSC();
            // tous les autres ont perdu, sauf ce process là, donc il a gagné
            File file = new File("winners");
            CharSink chs = com.google.common.io.Files.asCharSink(
                    file, Charsets.UTF_8, FileWriteMode.APPEND);
            try {
                chs.write(process.getId() + "\n");
            } catch (IOException ignore) { /* ignored */}

            System.out.println(String.format("%d a gagné avec un score de %d !", process.getId(), score));
            playersHighestStatus.put(process.getId(), false);
            process.getCom().releaseSC();
        }
    }
}
