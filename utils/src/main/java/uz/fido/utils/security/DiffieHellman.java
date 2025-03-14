package uz.fido.utils.security;

import android.content.Context;

import java.math.BigInteger;
import java.security.SecureRandom;

import uz.fido.utils.utility.user.PaperExtKt;

public class DiffieHellman {

    int bitLength = 512;
    BigInteger a, g, p;

    BigInteger biA, biB;
    BigInteger biK;
    String biKString;

    private static DiffieHellman diffieHellman = null;

    public static DiffieHellman getDiffieHellman() {
        if (diffieHellman == null) {
            diffieHellman = new DiffieHellman();
        }
        return diffieHellman;
    }

    public static void clearDiffieHellman() {
        diffieHellman = null;
    }

    private DiffieHellman() {
        SecureRandom randomGenerator = new SecureRandom();
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
        biKString = biK.toString();
    }

    public void setKeyBSwapKey(String B, String additionalText, Context context) {
        biB = new BigInteger(B);
        biK = biB.modPow(a, p);
        biKString = biK.toString();
        biKString += additionalText;
        PaperExtKt.saveToPaper(context, "KEY_K", biKString);
    }

    public String getKeyK() {
        return biKString;
    }
}
