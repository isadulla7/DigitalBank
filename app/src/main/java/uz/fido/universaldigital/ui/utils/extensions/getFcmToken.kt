package uz.fido.universaldigital.ui.utils.extensions

import androidx.fragment.app.Fragment
import com.google.firebase.messaging.FirebaseMessaging
import uz.fido.utils.const.Const
import uz.fido.utils.log.Logger

fun Fragment.getFCMToken() {
    if (getFromPaper(Const.PAPER_FCM_TOKEN).isEmpty()) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Logger.writeLog("===== FCM TOKEN ===== $token")
                Logger.writeLogByKey(Const.PAPER_FCM_TOKEN, token)
                saveToPaper(Const.PAPER_FCM_TOKEN, token)
            }
        }
    }
}