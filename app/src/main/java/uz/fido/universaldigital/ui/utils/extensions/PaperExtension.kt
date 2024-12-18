package uz.fido.universaldigital.ui.utils.extensions

import android.content.Context
import androidx.fragment.app.Fragment
import io.paperdb.Paper
import io.paperdb.PaperDbException
import uz.fido.utils.security.CryptoUtil
import uz.fido.utils.utility.context.getDeviceIds

fun Fragment.saveToPaper(key: String, value: String?) {
    try {
        if (value == null) {
            throw PaperDbException("Paper doesn't support writing null root values")
        } else {
            val encryptedValue = CryptoUtil.encrypt(value, requireContext().getDeviceIds())
            Paper.book().write(key, encryptedValue)
        }
    } catch (e: Exception) {
        recordException(e)
    }
}

fun Context.saveToPaper(key: String, value: String?) {
    if (value == null) {
        throw PaperDbException("Paper doesn't support writing null root values")
    } else {
        val encryptedValue = CryptoUtil.encrypt(value, getDeviceIds())
        Paper.book().write(key, encryptedValue)
    }
}

fun Fragment.getFromPaper(key: String, defaultValue: String? = ""): String {
    val encryptedValue = Paper.book().read<String>(key, defaultValue)
    val decryptedValue: String
    try {
        decryptedValue = CryptoUtil.decrypt(encryptedValue, requireContext().getDeviceIds())
    } catch (e: Exception) {
        return encryptedValue.orEmpty()
    }
    return decryptedValue
}

fun Context.getFromPaper(key: String, defaultValue: String? = ""): String {
    val encryptedValue = Paper.book().read<String>(key, defaultValue)
    val decryptedValue: String
    try {
        decryptedValue = CryptoUtil.decrypt(encryptedValue, getDeviceIds())
    } catch (e: Exception) {
        return encryptedValue.orEmpty()
    }
    return decryptedValue
}