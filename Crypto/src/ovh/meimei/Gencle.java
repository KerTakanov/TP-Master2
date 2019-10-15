package ovh.meimei;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Gencle {
    public static List<BigInteger> genPQ(int t) {
        BigInteger p = Utils.genPremier(t/2, t/2 + 16, 5);
        BigInteger q = Utils.genPremier(t/2, t/2 + 16, 5);

        return Arrays.asList(p, q);
    }

    public static List<BigInteger> genKeys(String keyStoreName, int bits) {
        List<BigInteger> pq = genPQ(bits);
        BigInteger p = pq.get(0), q = pq.get(1);

        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        BigInteger modulus    = p.multiply(q);
        BigInteger publicKey  = new BigInteger("65537");     // common value in practice = 2^16 + 1
        BigInteger privateKey = publicKey.modInverse(phi);

        try {
            Files.write(Paths.get(String.format("./%s.pub", keyStoreName)), (publicKey.toString() + " " + modulus.toString()).getBytes());
            Files.write(Paths.get(String.format("./%s.priv", keyStoreName)), (privateKey.toString() + " " + modulus.toString()).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Arrays.asList(publicKey, privateKey);
    }
}
