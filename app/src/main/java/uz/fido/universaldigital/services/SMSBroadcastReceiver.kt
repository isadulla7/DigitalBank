package uz.fido.universaldigital.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.util.regex.Pattern

open class SMSBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            if (extras != null) {
                val status = extras.get(SmsRetriever.EXTRA_STATUS) as? Status
                when (status?.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                        val pattern1 = Pattern.compile("\\d{8}")
                        val pattern2 = Pattern.compile("\\d{6}")
                        val matcher1 = pattern1.matcher(message)
                        val matcher2 = pattern2.matcher(message)
                        if (matcher1.find()) {
                            val otpCode = matcher1.group(0)
                            val `in` = Intent(SmsRetriever.SMS_RETRIEVED_ACTION)
                            val bundle = Bundle()
                            bundle.putString("otp", otpCode)
                            `in`.putExtras(bundle)
                            context.sendBroadcast(`in`)
                        } else if (matcher2.find()) {
                            val otpCode = matcher2.group(0)
                            val `in` = Intent(SmsRetriever.SMS_RETRIEVED_ACTION)
                            val bundle = Bundle()
                            bundle.putString("otp", otpCode)
                            `in`.putExtras(bundle)
                            context.sendBroadcast(`in`)
                        }
                    }

                    CommonStatusCodes.TIMEOUT -> {
                        //timeout
                    }
                }
            }
        }
    }
}