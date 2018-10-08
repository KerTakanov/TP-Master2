import java.util.Random;

public class Dice {
    private Random random = new Random();

    public int next(int d) {
        return random.nextInt(d);
    }
}
