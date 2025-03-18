package uz.fido.utils.log

import android.util.Log

class Logger {

    companion object {

        private const val DEBUG = true

        @JvmStatic
        fun writeLogByKey(key: String, text: String) {
            if (DEBUG) {
                Log.d("Logger====$key", text)
            }
        }

        @JvmStatic
        fun writeLog(s: String) {
            if (DEBUG) {
                Log.d("Logger====", s)
            }
        }

        @JvmStatic
        fun writeErrorLog(s: String) {
            if (DEBUG) {
                Log.e("Logger====", s)
            }
        }
    }
}