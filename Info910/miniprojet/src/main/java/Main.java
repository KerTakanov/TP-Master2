import dicegame.Game;

public class Main {
    public static void main(String[] args) {
        Game game = new Game(10);
        game.launch();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
