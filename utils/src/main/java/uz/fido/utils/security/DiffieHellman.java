package uz.fido.utils.security;

import java.math.BigInteger;
import java.util.Random;

import uz.fido.utils.log.Logger;

public class DiffieHellman {

    int bitLength = 512;
    BigInteger a, g, p;

    BigInteger biA, biB;
    BigInteger biK;

    private static DiffieHellman diffieHellman = null;

    public static DiffieHellman getDiffieHellman() {
        if (diffieHellman == null) {
            diffieHellman = new DiffieHellman();
        }
        return diffieHellman;
    }

    private DiffieHellman() {
        Random randomGenerator = new Random();
        a = new BigInteger(bitLength, randomGenerator);
        g = new BigInteger(bitLength, randomGenerator);
        p = new BigInteger(bitLength, randomGenerator);
        biA = g.modPow(a, p);
    }

    public String get_g() {
        return g.toString();
    }

    public String get_p() {
        return p.toString();
    }

    public String getKeyA() {
        return biA.toString();
    }

    public void SetKeyB(String B) {
        biB = new BigInteger(B);
        biK = biB.modPow(a, p);
        Logger.writeLog("bik________________________" + biK);
    }

    public String getKeyK() {
        Logger.writeLog("get_bik________________________" + biK);
        return biK.toString();
    }
}
