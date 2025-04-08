package uz.fido.utils.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import io.paperdb.Paper
import uz.fido.utils.utility.user.deleteFromPaper
import uz.fido.utils.utility.user.getFromPaper

object SecurePrefsManager {

    private const val PREF_FILE_NAME = "universal_secure_prefs"
    private lateinit var sharedPreferences: EncryptedSharedPreferences

    fun init(context: Context) {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            PREF_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences
    }

    fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun getInt(key: String): Int {
        return sharedPreferences.getInt(key, 0)
    }

    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean?): Boolean {
        return sharedPreferences.getBoolean(key, Paper.book().read<Boolean>(key, defaultValue) ?: false)
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

}

fun saveToSecureStore(key: String, value: String?) {
    try {
        SecurePrefsManager.putString(key, value.orEmpty())
        deleteFromPaper(key)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun getFromSecureStore(key: String, defaultValue: String? = null): String {
    return SecurePrefsManager.getString(key) ?: defaultValue.orEmpty()
}

fun Context.getFromSecureStore(key: String): String {
    return SecurePrefsManager.getString(key) ?: getFromPaper(key)
}

fun Context.getFromSecureStore(key: String, defaultValue: String?): String {
    return SecurePrefsManager.getString(key) ?: getFromPaper(key, defaultValue)
}