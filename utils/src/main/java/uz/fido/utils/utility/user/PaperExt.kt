package uz.fido.utils.utility.user

import android.content.Context
import io.paperdb.Paper
import uz.fido.utils.security.CryptoUtil
import uz.fido.utils.utility.context.getDeviceIds

fun Context.getFromPaper(key: String, defaultValue: String? = ""): String {
    val encryptedValue = Paper.book().read<String>(key, defaultValue)
    var decryptedValue = ""
    try {
        decryptedValue = CryptoUtil.decrypt(encryptedValue, getDeviceIds())
    } catch (e: Exception) {
        return encryptedValue.orEmpty()
    }
    return decryptedValue
}

fun deleteFromPaper(key: String) {
    try {
        Paper.book().delete(key)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}