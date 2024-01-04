package uz.fido.utils.security

import android.util.Base64
import uz.fido.utils.log.Logger.Companion.writeLog
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

fun encryptPassword(data: String): String {
    val sha2 = AeSimpleSHA1.SHA2(data)
    writeLog(sha2)
    val skeySpec = SecretKeySpec(sha2.toByteArray(), "AES")
    val cipher = Cipher.getInstance(CryptoUtil.cypherInstance)
    cipher.init(
        Cipher.ENCRYPT_MODE,
        skeySpec,
        IvParameterSpec(CryptoUtil.initializationVector.toByteArray())
    )
    val encrypted = cipher.doFinal(data.toByteArray())
    return Base64.encodeToString(encrypted, Base64.DEFAULT).replace("\\r\\n|\\r|\\n".toRegex(), "")
}

fun getEncodedString(token: String): String {
    return Base64.encodeToString(token.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
}

fun getDecodedString(token: String): String {
    return String(Base64.decode(token, Base64.DEFAULT), StandardCharsets.UTF_8)
}

fun getClientEncodedToken(token: String): String {
    return Base64.encodeToString(token.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
}