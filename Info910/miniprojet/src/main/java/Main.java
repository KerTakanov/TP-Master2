import processes.Process;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        List<Process> processes = new ArrayList<>();

        for (int i = 0; i < 6; ++i) {
            processes.add(new Process());
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        processes.get(0).getCom().synchronize();

        Thread.sleep(1000);
        processes.get(0).getCom().send("pd", 1);

        Thread.sleep(1000);
        System.out.println(processes.get(1).getLetterBox().messages);
    }
}
