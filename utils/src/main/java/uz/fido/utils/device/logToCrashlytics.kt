package uz.fido.utils.device

import com.google.firebase.crashlytics.FirebaseCrashlytics

fun logToCrashlytics(tag: String, message: String) {
    FirebaseCrashlytics.getInstance().log("$tag: $message")
}