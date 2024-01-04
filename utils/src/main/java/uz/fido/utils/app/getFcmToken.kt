package uz.fido.utils.app

import com.google.firebase.messaging.FirebaseMessaging
import io.paperdb.Paper
import uz.fido.utils.const.Const
import uz.fido.utils.log.Logger

fun getFCMToken() {
    if ((Paper.book().read(Const.PAPER_FCM_TOKEN) ?: "").isEmpty()) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Logger.writeLog("=====FCM TOKEN1${token}")
                Logger.writeLogByKey(Const.PAPER_FCM_TOKEN, token)
                Paper.book().write(Const.PAPER_FCM_TOKEN, token)
            }
        }
    }
}