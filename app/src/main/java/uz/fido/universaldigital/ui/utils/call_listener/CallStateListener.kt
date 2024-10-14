package uz.fido.universaldigital.ui.utils.call_listener

import android.content.Context
import android.telephony.TelephonyManager

class CallStateListener (context: Context){

    val telephonyManager: TelephonyManager =
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

}