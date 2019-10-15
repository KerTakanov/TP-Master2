package ovh.meimei;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Dechiffre {
    public static String dechiffrer(BigInteger cle, BigInteger b, BigInteger chiffre) {
        return new String(chiffre.modPow(cle, b).toByteArray());
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String message = scanner.nextLine();
        String filename = args[0];
        String clef = "";
        BigInteger cle;
        BigInteger b;

        try {
            clef = Files.readAllLines(Paths.get(filename + ".priv")).get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        cle = new BigInteger(clef.split(" ")[0]);
        b = new BigInteger(clef.split(" ")[1]);

        System.out.println(dechiffrer(cle, b, new BigInteger(message)));
    }
}
