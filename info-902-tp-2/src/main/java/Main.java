import messages.Stop;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args){

        List<Process> processes = new ArrayList<>();
        for (int i = 0; i < Process.NB_PLAYERS; ++i) {
            processes.add(new Process("P" + i));
        }

        try{
            Thread.sleep(3000);
        }catch(Exception e){
            e.printStackTrace();
        }

        processes.get(0).broadcast(new Stop());
        processes.get(0).stop();

        // wait stop
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
