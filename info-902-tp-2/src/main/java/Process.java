import com.google.common.base.Charsets;
import com.google.common.eventbus.Subscribe;
import com.google.common.io.CharSink;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import messages.Lost;
import messages.Message;
import messages.Synchronize;
import messages.Token;

import java.io.File;
import java.io.IOException;

public class Process implements Runnable, Messager, Broadcaster {
    private Thread thread;
    private EventBusService bus;
    private boolean alive;
    private boolean dead;
    private static int nbProcess = 0;
    private int id = Process.nbProcess++;
    private int stamp = 0;
    private boolean hasToken;
    private Dice dice = new Dice();
    private boolean highest = true;
    private int score;
    private int nbLost = 0;
    private boolean played = false;

    public Process(String name) {
        this.bus = EventBusService.getInstance();
        this.bus.registerSubscriber(this); // Auto enregistrement sur le bus afin que les methodes "@Subscribe" soient invoquees automatiquement.

        this.thread = new Thread(this);
        this.thread.setName(name);
        this.alive = true;
        this.dead = false;

        if (id == 0) {
            hasToken = true;
        }

        this.thread.start();
    }

    @Override
    public void broadcast(Object payload) {
        Message message = new Message(payload, stamp++, null);
        bus.postEvent(message);
    }

    // Declaration de la methode de callback invoquee lorsqu'un message de type Bidule transite sur le bus
    @Subscribe
    public void receive(Message message) {
        if (message.getDest() != null && id != message.getDest()) {
            return;
        }

        stamp = 1 + Math.max(message.getStamp(), stamp);

        if (message.getPayload() instanceof Token) {
            onToken();
        } else if (message.getPayload() instanceof Synchronize) {
            onSynchronize();
        } else if (message.getPayload() instanceof Lost) {
            onLost();
        } else {
            onDice((Integer) message.getPayload());
        }
    }

    private void onLost() {
        nbLost++;

        if (nbLost == nbProcess - 1 && highest) {
            request();

            for (int i = 0; i < 10; ++i) {
                System.out.print("=");
            }
            System.out.println(String.format("\n{%d} is the winner !", id));

            File file = new File("winners");
            CharSink chs = Files.asCharSink(
                    file, Charsets.UTF_8, FileWriteMode.APPEND);
            try {
                chs.write(id + "\n");
            } catch (IOException ignore) { /* ignored */}

            release();

            broadcast(new Synchronize());
            onSynchronize();
        }
    }

    private void onDice(int score) {
        if (this.score < score && highest) {
            highest = false;
            broadcast(new Lost());
        }
    }

    public void onSynchronize() {
        // io
        highest = true;
        nbLost = 0;
        score = 0;
        played = false;
    }

    public void onToken() {
        hasToken = true;
    }

    public void request() {
        while (!hasToken && !dead) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
                /* ignored */
            }
        }
    }

    public void release() {
        hasToken = false;
        send(new Token(), (id + 1) % nbProcess);
    }

    @Override
    public void send(Object payload, int to) {
        if (to == id) {
            return;
        }

        Message message = new Message(payload, ++stamp, to);
        bus.postEvent(message);
    }

    public void run() {
        while (this.alive) {
            try {
                Thread.sleep(20);
                if (nbProcess >= 10 && !played) {
                    score = dice.next(200);
                    played = true;
                    broadcast(score);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // liberation du bus
        this.bus.unRegisterSubscriber(this);
        this.bus = null;
        this.dead = true;
    }

    public void waitStoped() {
        while (!this.dead) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        release();
        this.alive = false;
    }
}
