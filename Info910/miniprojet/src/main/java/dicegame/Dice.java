package dicegame;

import java.util.Random;

public class Dice {
    private Random random = new Random();

    public int next(int max) {
        return 1 + random.nextInt(max);
    }
}
