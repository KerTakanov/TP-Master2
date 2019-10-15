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
    private static int nbProcess = 0;

    private Thread thread;
    private EventBusService bus;
    private Dice dice = new Dice();

    private int id = Process.nbProcess++;
    private int stamp = 0;
    private int score;
    private int nbLost = 0;
    private int nbReady = 0;

    private boolean alive;
    private boolean hasToken;
    private boolean highest = true;
    private boolean played = false;

    public Process(String name) {
        this.bus = EventBusService.getInstance();
        this.bus.registerSubscriber(this); // Auto enregistrement sur le bus afin que les methodes "@Subscribe" soient invoquees automatiquement.

        this.thread = new Thread(this);
        this.thread.setName(name);
        this.alive = true;

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

        // On traite chaque message selon son type
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

    /**
     * Lorsqu'on reçoit un message indiquant qu'un joueur a perdu, on vérifie si on a pas gagné
     */
    private void onLost() {
        nbLost++;

        if (nbLost == NB_PLAYERS - 1 && highest) {
            request();

            System.out.println(String.format("=========================\n{stamp: %d} {%d} is the winner !", stamp, id));

            File file = new File("winners");
            CharSink chs = Files.asCharSink(
                    file, Charsets.UTF_8, FileWriteMode.APPEND);
            try {
                chs.write(id + "\n");
            } catch (IOException ignore) { /* ignored */}

            release();

            // On synchronise tout le monde, ce qui permet de remettre à zéro
            broadcast(new Synchronize());
            onSynchronize();
        }
    }

    private void onReady() {
        nbReady += 1;
    }

    private void onDice(int score) {
        // On considère qu'un processus "perd" lorsqu'il a un score inférieur au score reçu ET si le nombre de perdants
        // est différent du nb de joueurs - 1 (pour ne pas s'inclure)
        if (nbLost != NB_PLAYERS - 1 && this.score < score && highest) {
            System.out.println(String.format("{stamp: %d} {%d} lost", stamp, id));
            highest = false;
            broadcast(new Lost());
        }
    }

    /**
     * Les processus se synchronisent lorsque le vainqueur a été trouvé
     * On en profite donc pour remettre à 0 les attributs pour recommencer une partie
     */
    public void onSynchronize() {
        nbLost = 0;
        score = 0;
        highest = true;
        played = false;

        // On indique aux autres le fait que le processus soit prêt
        broadcast(new Ready());
        onReady(); // techniquement, ce processus aussi est prêt
    }

    public void onToken() {
        hasToken = true;
    }

    /**
     * Bloque le thread actuel jusqu'à recevoir le token
     */
    public void request() {
        while (!hasToken) {
            try {
                Thread.sleep(1); // on attend un peu pour ne pas surcharger le CPU
            } catch (InterruptedException ignored) {
                /* ignored */
            }
        }
    }

    /**
     * Libère le token en le diffusant au prochain
     */
    public void release() {
        if (hasToken) {
            hasToken = false;
            send(new Token(), (id + 1) % nbProcess);
        }
    }

    @Override
    public void send(Object payload, int to) {
        if (to == id || !alive) {
            return;
        }

        Message message = new Message(payload, ++stamp, to, id);
        if (bus != null) {
            bus.postEvent(message);
        }
    }

    public void run() {
        // On attend que tous les process soient démarrés
        while (nbProcess < NB_PLAYERS) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                /* ignored */
            }
        }
        // On signale aux autres le fait que l'on soit prêt
        broadcast(new Ready());

        while (this.alive) {
            try {
                Thread.sleep(20);
                // On ne joue que si tout le monde est prêt et si l'on a pas joué
                if (!played && nbReady >= NB_PLAYERS) {
                    score = dice.next(200);
                    played = true;
                    nbReady = 0;
                    broadcast(score);

                    System.out.println(String.format("{stamp: %d} {%d} scored %d", stamp, id, score));
                }

                release(); // on relâche dans tous les cas périodiquement, au cas où quelqu'un aurait besoin du token
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // liberation du bus
        this.bus.unRegisterSubscriber(this);
        this.bus = null;
    }

    public void stop() {
        System.out.println(String.format("{stamp: %d} {%d} stopping...", stamp, id));
        release(); // on relâche le token
        this.alive = false;
    }
}
