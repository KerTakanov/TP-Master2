import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args){

        List<Process> processes = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            processes.add(new Process("P" + i));
        }

        try{
            Thread.sleep(10000);
        }catch(Exception e){
            e.printStackTrace();
        }

        processes.forEach(Process::stop);
    }
}
