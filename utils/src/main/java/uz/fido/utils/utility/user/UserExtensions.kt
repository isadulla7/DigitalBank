package uz.fido.utils.utility.user

import android.content.Context
import android.util.Base64
import androidx.fragment.app.Fragment
import uz.fido.utils.const.Const
import java.nio.charset.StandardCharsets

fun Fragment.getClientToken(): String {
    val token = getFromPaper(Const.PAPER_CLIENT_TOKEN)
    if (token.isEmpty()) {
        return token
    }
    return String(Base64.decode(token, Base64.DEFAULT), StandardCharsets.UTF_8)
}

fun Context.getClientToken(): String {
    val token = getFromPaper(Const.PAPER_CLIENT_TOKEN)
    if (token.isEmpty()) {
        return token
    }
    return String(Base64.decode(token, Base64.DEFAULT), StandardCharsets.UTF_8)
}

fun Fragment.getClientPhoneNumber(): String {
    return getFromPaper(Const.PAPER_CLIENT_PHONE)
}

fun Fragment.getFormattedClientPhone(): String {
    val currentPhone = getFromPaper(Const.PAPER_CLIENT_PHONE)
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

fun Fragment.getClientId(): String {
    return getFromPaper(Const.PAPER_CLIENT_ID)
}

fun Context.getClientId(): String {
    return getFromPaper(Const.PAPER_CLIENT_ID)
}

