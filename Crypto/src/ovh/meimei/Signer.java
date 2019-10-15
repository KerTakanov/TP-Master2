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

public class Signer {
    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        System.out.println("Entrer le message à signer: ");
        Scanner in = new Scanner(System.in);

        String name = args[0];
        String message = in.nextLine();
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(message.getBytes("UTF-8"));
        String hash = DatatypeConverter
                .printHexBinary(digest);

        String clef = "";
        BigInteger cle;
        BigInteger b;

        try {
            clef = Files.readAllLines(Paths.get(name + ".priv")).get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        cle = new BigInteger(clef.split(" ")[0]);
        b = new BigInteger(clef.split(" ")[1]);

        BigInteger sign = Chiffre.chiffrer(cle, b, hash);
        System.out.println(String.format("hash: %s", hash));

        try {
            Files.write(Paths.get(String.format("./%s.sign", name)), sign.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
