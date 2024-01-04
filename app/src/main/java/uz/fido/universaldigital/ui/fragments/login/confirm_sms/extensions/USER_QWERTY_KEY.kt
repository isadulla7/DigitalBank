package uz.fido.universaldigital.ui.fragments.login.confirm_sms.extensions

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Base64
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import com.google.firebase.messaging.FirebaseMessaging
import io.paperdb.Paper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.sign_in.SignInResponse
import uz.fido.network.domain.model.sign_up.FinishRegRequest
import uz.fido.universaldigital.ui.activities.LoginActivity
import uz.fido.utils.const.APIServiceConst.profileImageUrl
import uz.fido.utils.const.Const
import uz.fido.utils.const.Const.USER_QWERTY_KEY
import uz.fido.utils.utility.context.startActivityWithClearTask
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.ArrayList

const val USER_SMS_KEY = "user_sms_key"

fun Context.saveSignInResponse(signInResponse: SignInResponse) {
    Paper.book().write(Const.PAPER_CLIENT_INFO, signInResponse)
    Paper.book().write(Const.PAPER_CLIENT_TOKEN, getClientEncodedToken(signInResponse.token))
    Paper.book().write(Const.PAPER_PAYMENT_VERSION, signInResponse.version ?: "0")
    Paper.book().write(
        Const.PAPER_CLIENT_PHONE,
        signInResponse.phone_number?.replace("+", "")?.replace(" ", "")
    )
    Paper.book().write(Const.PAPER_CLIENT_ID, signInResponse.user_id)
    Paper.book().write(Const.PAPER_CLIENT_NAME, signInResponse.name)
    Paper.book().write(Const.PAPER_CLIENT_SURNAME, signInResponse.surname)
    Paper.book()
        .write(Const.PAPER_CLIENT_FULL_NAME, signInResponse.name + " " + signInResponse.surname)
    (signInResponse.password_enc.ifEmpty { signInResponse.password })?.let { saveUserQwerty(it) }
}

fun saveSignInPinResponse(signInResponse: SignInResponse) {
    Paper.book().write(Const.PAPER_CLIENT_INFO, signInResponse)
    Paper.book().write(Const.PAPER_CLIENT_TOKEN, getClientEncodedToken(signInResponse.token))
    Paper.book().write(Const.PAPER_PAYMENT_VERSION, signInResponse.version ?: "0")
    Paper.book().write(Const.PAPER_USER_PHOTO_PATH, profileImageUrl(signInResponse.user_avatar))
    Paper.book().write(Const.PAPER_CLIENT_ID, signInResponse.user_id)
    Paper.book().write(Const.PAPER_CLIENT_NAME, signInResponse.name)
    Paper.book().write(Const.PAPER_CLIENT_SURNAME, signInResponse.surname)
    Paper.book()
        .write(Const.PAPER_CLIENT_FULL_NAME, signInResponse.name + " " + signInResponse.surname)
    Paper.book().write(Const.PAPER_CLIENT_POINTS, signInResponse.points ?: "0")
    Paper.book().write(Const.PAPER_CLIENT_STATUS_NAME, signInResponse.user_status_name)
    Paper.book().write(Const.PAPER_CLIENT_STATUS_ID, signInResponse.user_status_id)
    Paper.book().write(
        Const.PAPER_CLIENT_PHONE,
        signInResponse.phone_number?.replace("+", "")?.replace(" ", "")
    )
    Paper.book().write(
        Const.PAPER_CLIENT_APPLICATION_COUNT,
        signInResponse.phone_number?.replace("+", "")?.replace(" ", "")
    )
    Paper.book().write(Const.APPLICATION_COUNT, signInResponse.application_count.toString())
    Paper.book().write(
        Const.USER_FULL_NAME,
        signInResponse.surname + " " + signInResponse.name + " " + signInResponse.patronymic
    )
    Paper.book().write(Const.USER_BIRTHDAY, signInResponse.date_of_birth)
    Paper.book().write(
        Const.USER_PASSWORD_DATA,
        signInResponse.passport_serial + " " + signInResponse.passport_number
    )
    Paper.book().write(Const.USER_PASS_GIVEN_DATE, signInResponse.passport_registration_date)
}

fun Context.saveSignUpRequest(signUpRequest: FinishRegRequest) {
    Paper.book().write(Const.PAPER_CLIENT_PHONE, signUpRequest.phone_number)
    Paper.book().write(Const.PAPER_CLIENT_TOKEN, getClientEncodedToken(signUpRequest.fcm_token!!))
    Paper.book().write(Const.PAPER_CLIENT_NAME, signUpRequest.name)
    Paper.book().write(Const.PAPER_CLIENT_SURNAME, signUpRequest.surname)
    Paper.book()
        .write(Const.PAPER_CLIENT_FULL_NAME, signUpRequest.name + " " + signUpRequest.surname)
    Paper.book().write(Const.PAPER_CLIENT_ID, signUpRequest.client_id)
    saveUserQwerty(signUpRequest.password.toString())
}

fun Context.saveUserQwerty(qwerty: String) {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
        val mainKey = MasterKey.Builder(this, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

        val fileToWrite = "universal_digital_qwerty.md"
        val file = File(filesDir, fileToWrite)
        if (file.exists()) {
            file.delete()
        }

        val encryptedFile = EncryptedFile.Builder(
            applicationContext,
            file,
            mainKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        val fileContent = qwerty.toByteArray(StandardCharsets.UTF_8)
        encryptedFile.openFileOutput().apply {
            write(fileContent)
            flush()
            close()
        }
    } else {
        Paper.book().write(USER_QWERTY_KEY, qwerty)
    }
}

fun Context.saveUserSms(qwerty: String) {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
        val mainKey = MasterKey.Builder(this, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

        val fileToWrite = "universal_digital_random_message.md"
        val file = File(filesDir, fileToWrite)
        if (file.exists()) {
            file.delete()
        }

        val encryptedFile = EncryptedFile.Builder(
            applicationContext,
            file,
            mainKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        val fileContent = qwerty.toByteArray(StandardCharsets.UTF_8)
        encryptedFile.openFileOutput().apply {
            write(fileContent)
            flush()
            close()
        }
    } else {
        Paper.book().write(USER_SMS_KEY, qwerty)
    }
}

fun Context.getUserQwerty(): String {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
        val mainKey = MasterKey.Builder(this, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

        val fileToRead = "universal_digital_qwerty.md"
        val file = File(filesDir, fileToRead)
        val encryptedFile = EncryptedFile.Builder(
            applicationContext,
            file,
            mainKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
        return try {
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
            ""
        }
    } else {
        return Paper.book().read(USER_QWERTY_KEY, "")
    }
}

fun getClientEncodedToken(token: String): String {
    return Base64.encodeToString(token.toString().toByteArray(Charsets.UTF_8), Base64.DEFAULT)
}

fun Activity.logOut() {
    GlobalScope.launch { FirebaseMessaging.getInstance().deleteToken() }
    Paper.book().write(Const.PAPER_CARDS_WITH_BALANCE_VIS, ArrayList<CardResponse>())
    Paper.book().write(Const.PAPER_CLIENT_CARDS, ArrayList<CardResponse>())
    Paper.book().write(Const.USER_LOGGED, false)
    Paper.book().write(Const.PAPER_USER_PHOTO_PATH, "")
    Paper.book().write(Const.PAPER_FCM_TOKEN, "")
    Paper.book().write(Const.USER_NAME, "")
    Paper.book().write(Const.USER_FULL_NAME, "")
    Paper.book().write(Const.USER_CITIZENSHIP, "")
    Paper.book().write(Const.USER_PASSWORD_DATA, "")
    Paper.book().write(Const.USER_PASS_EXPIRE_DATE, "")
    startActivityWithClearTask(LoginActivity::class.java)
}