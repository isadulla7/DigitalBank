package uz.fido.universaldigital.ui.utils.extensions

import androidx.fragment.app.Fragment
import com.google.firebase.messaging.FirebaseMessaging
import uz.fido.utils.const.Const
import uz.fido.utils.security.getFromSecureStore
import uz.fido.utils.security.saveToSecureStore

fun Fragment.getFCMToken() {
    if (requireContext().getFromSecureStore(Const.PAPER_FCM_TOKEN).isEmpty()) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                saveToSecureStore(Const.PAPER_FCM_TOKEN, token)
            }
        }
    }
}