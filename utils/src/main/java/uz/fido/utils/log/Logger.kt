package uz.fido.utils.log

import android.util.Log

class Logger {

    companion object {
        val isDebug = true

        @JvmStatic
        fun writeLogByKey(key: String, text: String) {
            if (isDebug) {
                Log.d("Logger====$key", text)
            }
        }

        @JvmStatic
        fun writeLog(s: String) {
            if (isDebug) {
                Log.d("Logger====", s)
            }
        }

        @JvmStatic
        fun writeErrorLog(s: String) {
            if (isDebug) {
                Log.e("Logger====", s)
            }
        }
    }
}