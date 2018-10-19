package processes;

import comm.Com;
import lombok.Data;
import messages.HeartbitMessage;
import messages.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Data
public class Process implements Lamport {
    public static List<Process> processes = new ArrayList<>();
    private int id;

    private boolean alive = true;
    private Com com;
    private int clock;
    private Semaphore semaphore = new Semaphore(1);

    private LetterBox letterBox = new LetterBox();
    private LetterBox heartbits = new LetterBox();

    public Process() {

    }

    public void init() {
        com = new Com(this);
        synchronized (processes) {
            this.id = processes.size();
            processes.add(this);
        }

        Runnable runnable = new Runnable() {
            public void run() {
                com.releaseSC();
                com.broadcast(new HeartbitMessage(id));
            }
        };

        ScheduledExecutorService service = Executors
                .newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 0, 150, TimeUnit.MILLISECONDS);

        Runnable runnableHeartbit = new Runnable() {
            @Override
            public void run() {
                synchronized (processes) {
                    for (Process process : processes) {
                        process.setAlive(false);

                        for (Message message : heartbits.messages) {
                            if (process.id == message.getSender()) {
                                process.setAlive(true);
                            }
                        }
                    }

                    heartbits.messages.clear();

                    com.requestSC();
                    // find first disponible ID and assign it to self if process id < this is

                    for (Process process : processes) {
                        if (alive && !process.isAlive() && process.getId() < id) {
                            id = process.getId();
                        }
                    }

                    processes = processes.stream().filter(process -> alive).collect(Collectors.toList());
                }

                com.releaseSC();
            }
        };

        ScheduledExecutorService serviceHeartbit = Executors
                .newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 0, 150 * 3, TimeUnit.MILLISECONDS);
    }

    public void heartbit(HeartbitMessage message) {
        heartbits.post(message);
    }

    @Override
    public int clock() {
        try {
            semaphore.acquire();
            clock++;
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return clock;
    }

    @Override
    public int clock(int stamp) {
        clock = 1 + (Math.max(clock, stamp));
        return clock;
    }
}
