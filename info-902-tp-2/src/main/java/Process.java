import com.google.common.base.Charsets;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.google.common.io.CharSink;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import messages.*;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.File;
import java.io.IOException;

public class Process implements Runnable, Messager, Broadcaster {
    public static final int NB_PLAYERS = 10;

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
    private int nbReady = 0;

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
        Message message = new Message(payload, stamp++, null, id);
        if (bus != null) {
            bus.postEvent(message);
        }
    }

    // Declaration de la methode de callback invoquee lorsqu'un message de type Bidule transite sur le bus
    @Subscribe
    @AllowConcurrentEvents
    public void receive(Message message) {
        if (message.getDest() != null && id != message.getDest() && message.getSender() != id) {
            return;
        }

        stamp = 1 + Math.max(message.getStamp(), stamp);

        if (message.getPayload() instanceof Token) {
            onToken();
        } else if (message.getPayload() instanceof Synchronize) {
            onSynchronize();
        } else if (message.getPayload() instanceof Lost) {
            onLost();
        } else if (message.getPayload() instanceof Stop) {
            stop();
        } else if (message.getPayload() instanceof Ready) {
            onReady();
        } else {
            onDice((Integer) message.getPayload());
        }
    }

    private void onLost() {
        nbLost++;

        if (nbLost == NB_PLAYERS - 1 && highest) {
            System.out.println(String.format("\n{%d} is the winner !", id));
            request();

            for (int i = 0; i < 10; ++i) {
                System.out.print("=");
            }

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

    private void onReady() {
        nbReady += 1;
    }

    private void onDice(int score) {
        if (this.score < score && highest) {
            highest = false;
            broadcast(new Lost());
        }
    }

    public void onSynchronize() {
        nbLost = 0;
        score = 0;
        highest = true;
        played = false;

        broadcast(new Ready());
        onReady();
    }

    public void onToken() {
        hasToken = true;
    }

    public void request() {
        while (!hasToken) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
                /* ignored */
            }
        }
    }

    public void release() {
        if (hasToken) {
            hasToken = false;
            send(new Token(), (id + 1) % nbProcess);
        }
    }

    @Override
    public void send(Object payload, int to) {
        if (to == id) {
            return;
        }

        Message message = new Message(payload, ++stamp, to, id);
        bus.postEvent(message);
    }

    public void run() {
        while (nbProcess < NB_PLAYERS) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                /* ignored */
            }
        }
        broadcast(new Ready());

        while (this.alive) {
            try {
                Thread.sleep(20);
                if (nbProcess >= NB_PLAYERS && !played && nbReady >= NB_PLAYERS) {
                    score = dice.next(200);
                    played = true;
                    nbReady = 0;
                    broadcast(score);
                }
                release();
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
        System.out.println(String.format("{%d} stopping...", id));
        release();
        this.alive = false;
    }
}
