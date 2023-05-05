package uz.fido.utils.security;

import static uz.fido.utils.security.AeSimpleSHA1.SHA2;

import android.util.Base64;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.RequestBody;
import okio.Buffer;

public class CryptoUtil {

    public static final int pswdIterations = 10;
    public static final int keySize = 128;
    public static final String cypherInstance = "AES/CBC/PKCS5Padding";
    public static final String secretKeyInstance = "PBKDF2WithHmacSHA1";
    public static final String AESSalt = "qwerty";
    public static final String initializationVector = "8119745113154120";


    public static String encrypt(String data, String plainText) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(getRaw(plainText), "AES");
        Cipher cipher = Cipher.getInstance(cypherInstance);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(initializationVector.getBytes()));
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.encodeToString(encrypted, Base64.DEFAULT).replaceAll("\\r\\n|\\r|\\n", "");
    }

    public static String decrypt(String data, String plainText) throws Exception {
        byte[] encryptedBytes = Base64.decode(data.replaceAll("\\r\\n|\\r|\\n", ""), Base64.DEFAULT);
        SecretKeySpec skeySpec = new SecretKeySpec(getRaw(plainText), "AES");
        Cipher cipher = Cipher.getInstance(cypherInstance);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(initializationVector.getBytes()));
        byte[] decrypted = cipher.doFinal(encryptedBytes);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    public static String encryptWithoutSalt(String data, String plainText) throws Exception {
        String sha2 = SHA2(plainText);
        SecretKeySpec skeySpec = new SecretKeySpec(sha2.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(cypherInstance);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(initializationVector.getBytes()));
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.encodeToString(encrypted, Base64.DEFAULT).replaceAll("\\r\\n|\\r|\\n", "");
    }

    private static byte[] getRaw(String plainText) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(secretKeyInstance);
            KeySpec spec = new PBEKeySpec(plainText.toCharArray(), AESSalt.getBytes(), pswdIterations, keySize);
            return factory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static String requestBodyToString(RequestBody requestBody) throws IOException {
        Buffer buffer = new Buffer();
        try {
            requestBody.writeTo(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.readUtf8();
    }


}
