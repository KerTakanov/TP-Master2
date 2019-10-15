package ovh.meimei;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class VerifierSignature {
    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        System.out.println("Entrer le nom de la signature à vérifier: ");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        System.out.println("Entrer le message à vérifier: ");
        String message = scanner.nextLine();
        String sign = null;
        String filename = args[0];
        String clef = "";
        BigInteger cle;
        BigInteger b;

        try {
            sign = Files.readAllLines(Paths.get(name + ".sign")).get(0);
            clef = Files.readAllLines(Paths.get(filename + ".pub")).get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        cle = new BigInteger(clef.split(" ")[0]);
        b = new BigInteger(clef.split(" ")[1]);

        String ohash = Dechiffre.dechiffrer(cle, b, new BigInteger(sign));

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(message.getBytes("UTF-8"));
        String hash = DatatypeConverter
                .printHexBinary(digest);

        System.out.println(hash.equals(ohash) ? "Signature valide" : "Signature non valide");
    }
}
