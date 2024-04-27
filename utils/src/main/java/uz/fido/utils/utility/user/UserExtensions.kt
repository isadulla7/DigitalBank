package uz.fido.utils.utility.user

import android.content.Context
import android.os.Build
import android.util.Base64
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import io.paperdb.Paper
import uz.fido.utils.const.Const
import uz.fido.utils.const.Const.USER_QWERTY_KEY
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.StandardCharsets

fun getClientToken(): String {
    val token = Paper.book().read(Const.PAPER_CLIENT_TOKEN, "") ?: ""
    if (token.isEmpty()) {
        return token
    }
    return String(Base64.decode(token, Base64.DEFAULT), StandardCharsets.UTF_8)
}

fun getClientPhoneNumber(): String {
    return Paper.book().read(Const.PAPER_CLIENT_PHONE, "") ?: ""
}

fun getFormattedClientPhone(): String {
    val currentPhone = Paper.book().read(Const.PAPER_CLIENT_PHONE, "") ?: ""
    return when {
        currentPhone.isEmpty() -> ""
        currentPhone.startsWith("+") && currentPhone.length == 13 -> {
            currentPhone.substring(0, 4) + " " +
                    currentPhone.substring(4, 6) +
                    " ••• •• " + currentPhone.substring(11, 13)
        }

        currentPhone.length == 12 && !currentPhone.startsWith("+") -> {
            "+" + currentPhone.substring(0, 3) + " " +
                    currentPhone.substring(3, 5) + " ••• •• " + currentPhone.substring(10, 12)
        }

        currentPhone.length == 9 -> {
            "+998" + currentPhone.substring(0, 2) + " •• ••• •• " + currentPhone.substring(7, 9)
        }

        else -> currentPhone
    }
}

fun getClientId(): String {
    return Paper.book().read(Const.PAPER_CLIENT_ID, "") ?: ""
}

fun Context.getUserQwerty(): String {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
        val mainKey = MasterKey.Builder(this, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

        val fileToRead = "universal_digital_qwerty.md"
        val file = File(filesDir, fileToRead)
        return try {
            val encryptedFile = EncryptedFile.Builder(
                applicationContext,
                file,
                mainKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            val inputStream = encryptedFile.openFileInput()
            val byteArrayOutputStream = ByteArrayOutputStream()
            var nextByte: Int = inputStream.read()
            while (nextByte != -1) {
                byteArrayOutputStream.write(nextByte)
                nextByte = inputStream.read()
            }

            val plaintext: ByteArray = byteArrayOutputStream.toByteArray()
            String(plaintext, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            "404"
        }
    } else {
        return Paper.book().read(USER_QWERTY_KEY, "")
    }
}
