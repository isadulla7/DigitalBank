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
import uz.fido.utils.app.Keys;

public class CryptoUtil {

    public static final int passwdIterations = 10;
    public static final int keySize = 128;
    public static final String cypherInstance = Keys.INSTANCE.getCipherInstances();
    public static final String secretKeyInstance = Keys.INSTANCE.getSecretKeyInstance();
    public static final String AESSalt = Keys.INSTANCE.getAesSalt();
    public static final String initializationVector = Keys.INSTANCE.getInitializationVector();

    public static String encrypt(String data, String plainText) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(getRaw(plainText), Keys.INSTANCE.getDefaultAlgorithm());
        Cipher cipher = Cipher.getInstance(cypherInstance);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(initializationVector.getBytes()));
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.encodeToString(encrypted, Base64.DEFAULT).replaceAll("\\r\\n|\\r|\\n", "");
    }

    public static String decrypt(String data, String plainText) throws Exception {
        byte[] encryptedBytes = Base64.decode(data.replaceAll("\\r\\n|\\r|\\n", ""), Base64.DEFAULT);
        SecretKeySpec keySpec = new SecretKeySpec(getRaw(plainText), Keys.INSTANCE.getDefaultAlgorithm());
        Cipher cipher = Cipher.getInstance(cypherInstance);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(initializationVector.getBytes()));
        byte[] decrypted = cipher.doFinal(encryptedBytes);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    public static String encryptWithoutSalt(String data, String plainText) throws Exception {
        String sha2 = SHA2(plainText);
        SecretKeySpec keySpec = new SecretKeySpec(sha2.getBytes(), Keys.INSTANCE.getDefaultAlgorithm());
        Cipher cipher = Cipher.getInstance(cypherInstance);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(initializationVector.getBytes()));
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.encodeToString(encrypted, Base64.DEFAULT).replaceAll("\\r\\n|\\r|\\n", "");
    }

    public static String decryptWithoutSalt(String encryptedData, String plainText) throws Exception {
        // Generate SHA2 key based on the plain text (same as encryption process)
        String sha2 = SHA2(plainText);
        SecretKeySpec keySpec = new SecretKeySpec(sha2.getBytes(), Keys.INSTANCE.getDefaultAlgorithm());

        // Initialize cipher in DECRYPT_MODE with the same IV
        Cipher cipher = Cipher.getInstance(cypherInstance); // Ensure cypherInstance is consistent with encryption
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(initializationVector.getBytes()));

        // Decode base64 encoded encrypted data
        byte[] decodedEncryptedData = Base64.decode(encryptedData, Base64.DEFAULT);

        // Perform decryption
        byte[] decryptedBytes = cipher.doFinal(decodedEncryptedData);
        return new String(decryptedBytes);
    }

    private static byte[] getRaw(String plainText) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(secretKeyInstance);
            KeySpec spec = new PBEKeySpec(plainText.toCharArray(), AESSalt.getBytes(), passwdIterations, keySize);
            return factory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static String requestBodyToString(RequestBody requestBody) {
        Buffer buffer = new Buffer();
        try {
            requestBody.writeTo(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.readUtf8();
    }


}
