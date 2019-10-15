package ovh.meimei;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Chiffre {
    public static BigInteger chiffrer(BigInteger cle, BigInteger b, String message) {
        return new BigInteger(message.getBytes()).modPow(cle, b);
    }

    public static void main(String[] args) {
        System.out.println("Entrer le message à chiffrer: ");
        Scanner scanner = new Scanner(System.in);
        String message = scanner.nextLine();
        String filename = args[0];
        String clef = "";
        BigInteger cle;
        BigInteger b;

        try {
            clef = Files.readAllLines(Paths.get(filename + ".pub")).get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        cle = new BigInteger(clef.split(" ")[0]);
        b = new BigInteger(clef.split(" ")[1]);

        System.out.println(chiffrer(cle, b, message));
    }
}
