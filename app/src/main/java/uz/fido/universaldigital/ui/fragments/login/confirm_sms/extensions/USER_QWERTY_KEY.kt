package uz.fido.universaldigital.ui.fragments.login.confirm_sms.extensions

import android.app.Activity
import android.content.Context
import android.util.Base64
import androidx.fragment.app.Fragment
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import com.google.firebase.messaging.FirebaseMessaging
import io.paperdb.Paper
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import uz.fido.network.domain.model.sign_in.SignInResponse
import uz.fido.universaldigital.ui.activities.LoginActivity
import uz.fido.utils.const.APIServiceConst.profileImageUrl
import uz.fido.utils.const.Const
import uz.fido.utils.security.DiffieHellman
import uz.fido.utils.security.saveToSecureStore
import uz.fido.utils.utility.context.startActivityWithClearTask
import java.io.File
import java.nio.charset.StandardCharsets

fun Context.saveSignInResponse(signInResponse: SignInResponse) {
    saveToSecureStore(Const.PAPER_CLIENT_USER_TYPE_ID, signInResponse.user_type_id.toString())
    saveToSecureStore(Const.PAPER_CLIENT_FILIAL_CODE, signInResponse.filial_code.orEmpty())
    saveToSecureStore(Const.PAPER_CLIENT_TOKEN, getClientEncodedToken(signInResponse.token))
    saveToSecureStore(Const.PAPER_PAYMENT_VERSION, signInResponse.version ?: "0")
    saveToSecureStore(Const.PAPER_CLIENT_PHONE, signInResponse.phone_number?.replace("+", "")?.replace(" ", ""))
    saveToSecureStore(Const.PAPER_CLIENT_ID, signInResponse.user_id)
    saveToSecureStore(Const.FIRST_NAME, signInResponse.name)
    saveToSecureStore(Const.LAST_NAME, signInResponse.surname)
    saveToSecureStore(Const.PAPER_CLIENT_FULL_NAME, signInResponse.name + " " + signInResponse.surname)
    saveToSecureStore(Const.EMAIL, signInResponse.email.toString().lowercase())
    saveToSecureStore(Const.ADDRESS, signInResponse.address.toString().lowercase())
    signInResponse.password?.let {
        saveUserQwerty(it)
    }
}

fun Fragment.saveSignInPinResponse(signInResponse: SignInResponse) {
    saveToSecureStore(Const.PAPER_CLIENT_USER_TYPE_ID, signInResponse.user_type_id.toString())
    saveToSecureStore(Const.PAPER_CLIENT_FILIAL_CODE, signInResponse.filial_code.orEmpty())
    saveToSecureStore(Const.PAPER_CLIENT_TOKEN, getClientEncodedToken(signInResponse.token))
    saveToSecureStore(Const.PAPER_PAYMENT_VERSION, signInResponse.version ?: "0")
    saveToSecureStore(Const.PAPER_USER_PHOTO_PATH, profileImageUrl(signInResponse.user_avatar))
    saveToSecureStore(Const.PAPER_CLIENT_ID, signInResponse.user_id)
    saveToSecureStore(Const.FIRST_NAME, signInResponse.name)
    saveToSecureStore(Const.LAST_NAME, signInResponse.surname)
    saveToSecureStore(Const.PATRONYMIC, signInResponse.patronymic)
    saveToSecureStore(Const.PAPER_CLIENT_FULL_NAME, signInResponse.name + " " + signInResponse.surname)
    saveToSecureStore(Const.PAPER_CLIENT_PHONE, signInResponse.phone_number?.replace("+", "")?.replace(" ", ""))
    saveToSecureStore(Const.USER_BIRTHDAY, signInResponse.date_of_birth)
    saveToSecureStore(Const.USER_PASSWORD_DATA, signInResponse.passport_serial + " " + signInResponse.passport_number)
    saveToSecureStore(Const.USER_PASS_GIVEN_DATE, signInResponse.passport_registration_date)
    saveToSecureStore(Const.PINFL, signInResponse.pnfl)
    saveToSecureStore(Const.EMAIL, signInResponse.email.toString().lowercase())
    saveToSecureStore(Const.ADDRESS, signInResponse.address.toString().lowercase())
}

fun Context.saveUserQwerty(qwerty: String) {
    val mainKey = MasterKey.Builder(this, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

    val fileToWrite = "universal_digital_qwerty.md"
    val file = File(filesDir, fileToWrite)
    if (file.exists()) {
        file.delete()
    }
    try {
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
    } catch (e: Exception) {
        e.stackTrace
    }
}

fun Context.saveUserSms(qwerty: String) {
    val mainKey = MasterKey.Builder(this, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

    val fileToWrite = "universal_digital_random_message.md"
    val file = File(filesDir, fileToWrite)
    if (file.exists()) {
        file.delete()
    }
    try {

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
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun getClientEncodedToken(token: String): String {
    return Base64.encodeToString(token.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
}

@OptIn(DelicateCoroutinesApi::class)
fun Activity.logOut() {
    GlobalScope.launch { FirebaseMessaging.getInstance().deleteToken() }
    Paper.book().destroy()
    DiffieHellman.clearDiffieHellman()
    startActivityWithClearTask(LoginActivity::class.java)
}