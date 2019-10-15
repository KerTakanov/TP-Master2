package ovh.meimei;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Random;

public class Utils {
    public static boolean estProbablementPremier(BigInteger n) {
        if (n.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
            return false;
        }

        Random random = new Random();
        BigInteger k = BigInteger.valueOf(0);
        BigInteger m = n.subtract(BigInteger.ONE);
        BigInteger a;
        do {
            a = new BigInteger(n.bitLength(), random);
        } while (a.compareTo(n) >= 0);

        while (k.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
            m = m.divide(BigInteger.valueOf(2));
            k = k.add(BigInteger.ONE);
        }

        BigInteger b = a.modPow(m, n);

        if (b.mod(n).equals(BigInteger.ONE)) {
            return true;
        }

        for (BigInteger i = BigInteger.ZERO; i.compareTo(k) < 0; i = i.add(BigInteger.ONE)) {
            if (b.mod(n).equals(BigInteger.ONE.negate())) {
                return true;
            }
            b = b.modPow(BigInteger.valueOf(2), n);
        }

        return false;
    }

    public static boolean estProbablementPremierRapide(BigInteger n, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            if (estProbablementPremier(n)) {
                return true;
            }
        }

        return false;
    }

    public static BigInteger genPremier(int minbits, int maxbits, int iterations) {
        Random random = new Random();

        while (true) {
            BigInteger rnd = new BigInteger(minbits + random.nextInt(maxbits - minbits + 1), random);
            if (estProbablementPremierRapide(rnd, iterations)) {
                return rnd;
            }
        }
    }
}
