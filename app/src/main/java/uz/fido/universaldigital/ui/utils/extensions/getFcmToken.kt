package uz.fido.universaldigital.ui.utils.extensions

import androidx.fragment.app.Fragment
import com.google.firebase.messaging.FirebaseMessaging
import uz.fido.utils.const.Const

fun Fragment.getFCMToken() {
    if (getFromPaper(Const.PAPER_FCM_TOKEN).isEmpty()) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                saveToPaper(Const.PAPER_FCM_TOKEN, token)
            }
        }
    }
}