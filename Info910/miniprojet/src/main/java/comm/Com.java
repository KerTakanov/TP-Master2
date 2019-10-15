package comm;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import messages.*;
import processes.EventBusService;
import processes.Process;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public class Com {
    private Process process;
    private EventBusService bus;
    private Token token;
    private AtomicBoolean syncing = new AtomicBoolean(false);
    private CountDownLatch latch = new CountDownLatch(2);
    private CyclicBarrier barrier = new CyclicBarrier(2);

    public Com(Process process) {
        this.process = process;
        this.bus = EventBusService.getInstance();
        this.bus.registerSubscriber(this);

        if (process.getId() == 0) {
            token = new Token(0);
        }
    }

    public boolean hasToken() {
        return token != null;
    }

    public void synchronize() {
        bus.postEvent(new SynchronizeMessage());
    }

    private void synchronizeInternal() {
        syncing.set(true);

        System.out.println(String.format("Process %d syncing", process.getId()));
        bus.postEvent(new SynchronizeMessage());

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(String.format("Process %d done syncing", process.getId()));

        syncing.set(false);
    }

    public void broadcastSync(Object payload) {
        SyncBroadcastMessage message = new SyncBroadcastMessage(payload, process.clock(), process.getId());
        if (bus != null) {
            bus.postEvent(message);
        }
    }

    public void broadcast(Message message) {
        bus.postEvent(message);
    }

    public void broadcast(Object payload) {
        BroadcastMessage message = new BroadcastMessage(payload, process.clock(), process.getId());
        if (bus != null) {
            bus.postEvent(message);
        }
    }

    public void send(Object payload, int to) {
        if (to == process.getId()) {
            return;
        }

        Message message = new TargetMessage(payload, process.clock(), to, process.getId());
        bus.postEvent(message);
    }

    public void sendSync(Object payload, int to) {
        if (to == process.getId()) {
            return;
        }

        Message message = new SyncTargetMessage(payload, process.clock(), to, process.getId());
        bus.postEvent(message);
        try {
            barrier.await();

            send(new ReceivedMessage(message.getDest(), process.getId()), to);

            barrier.reset();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    public void requestSC() {
        System.out.println("Waiting token");
        while (!hasToken()) {
            try {
                Thread.sleep(1); // on attend un peu pour ne pas surcharger le CPU
            } catch (InterruptedException ignored) {
                /* ignored */
            }
        }
        System.out.println("Got token");
    }

    public void releaseSC() {
        if (token == null)
            return;

        bus.postEvent(new Token((process.getId() + 1) % Process.processes.size()));
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onHeartbit(HeartbitMessage message) {
        process.heartbit(message);
    }

    @Subscribe
    public void onRecvSync(SyncTargetMessage message) {
        if (message.getDest() != process.getId()) {
            return;
        }

        process.clock(message.getStamp());
        process.getLetterBox().post(message);
        bus.postEvent(new ReceivedMessage(message.getSender(), process.getId()));
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onSendSync(ReceivedMessage message) throws BrokenBarrierException, InterruptedException {
        if (message.getDest() != process.getId()) {
            return;
        }

        barrier.await();
    }

    @Subscribe
    public void onBroadcastSync(SyncBroadcastMessage message) {
        if (message.getSender() != process.getId()) {
            process.clock(message.getStamp());
            process.getLetterBox().post(message);
        }

        latch = new CountDownLatch(Process.processes.size() - 1);
        synchronizeInternal();
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onSynchronize(SynchronizeMessage message) {
        if (syncing.get()) {
            latch.countDown();
            return;
        }

        latch = new CountDownLatch(Process.processes.size());
        synchronizeInternal();
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onToken(Token token) {
        if (token.getDest() == process.getId()) {
            this.token = token;
        }
    }

    @Subscribe
    public void onBroadcast(BroadcastMessage message) {
        if (message.getSender() != process.getId()) {
            process.clock(message.getStamp());
            process.getLetterBox().post(message);
        }
    }

    @Subscribe
    public void onTargetMessage(TargetMessage message) {
        if (message.getDest() != process.getId()) {
            return;
        }

        process.clock(message.getStamp());
        process.getLetterBox().post(message);
    }

    public void stop() {
        this.bus.unRegisterSubscriber(this);
        this.bus = null;
    }
}
